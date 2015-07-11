package bkmi.de.hftl_app.Fragmente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Toast;

import java.net.UnknownHostException;

import bkmi.de.hftl_app.Database.NotenDB;
import bkmi.de.hftl_app.Database.NotenTabelle;
import bkmi.de.hftl_app.EinstellungActivity;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.CustomAdapterNoten;
import bkmi.de.hftl_app.help.NotenResolver;
import bkmi.de.hftl_app.help.TextSecure;
import bkmi.de.hftl_app.help.wrongUserdataException;

import static android.R.layout.simple_list_item_1;

public class NotenFragment extends ListFragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    NotenDB notenDB;
    ArrayAdapter<String> arrayAdapter;
    Button button;
    String [] fachList;
    String [] notenList;
    String [] versuchList;
    String [] semesterList;

    /**
     * Erstellt ein neues NotenFragment.
     */
    public static NotenFragment newInstance(int sectionNumber) {
        NotenFragment fragment = new NotenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NotenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //zeige Notenview

        View notenView = inflater.inflate(R.layout.fragment_noten, container, false);

        // Custom Typeface - Einbinden der Schriftarten nach CI/CD

        //Überschrift "hl" mit ORCA

        TextView hl = (TextView) notenView.findViewById(R.id.hl_Noten);
        Typeface headline = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OCRA.TTF");
        hl.setTypeface(headline);


        return notenView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NewsActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        notenDB = NotenDB.getInstance(getActivity());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        notenDB.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        button = (Button) getView().findViewById(R.id.noten_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!testeBenutzerdaten()) {
                    keineBenutzerdaten();
                } else {
                    if(NewsFragment.isOnline(getActivity())) {
                        NotenHelper nh = new NotenHelper();
                        nh.execute("");
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), "keine Online-Verbindung!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });

        //Prüfen ob Datenbank befüllt

        Cursor cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.NOTENABFRAGE, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            setzeListview(true);
        }
        //falls Datenbank leer --> Benutzerdaten eingegeben?
        else {
            if (!testeBenutzerdaten()) {
                keineBenutzerdaten();
            }
            //wenn Benutzerdaten richtig Noten abrufen
            else {
                if(NewsFragment.isOnline(getActivity())) {
                    NotenHelper nh = new NotenHelper();
                    nh.execute("");
                }
            }
        }
    }

    private boolean testeBenutzerdaten() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        TextSecure ts;
        try {
            ts = new TextSecure(getActivity());
            password = ts.decrypt(password);
            username = ts.decrypt(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !("".equals(username) || "".equals(password));
    }

    //gibt die Noten als Stringarray zurück...kann noch geändert werden
    private String[] getNoten() {
        Cursor cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.NOTENABFRAGE, null, null, null, null, NotenTabelle.SEMESTER);
        cursor.moveToFirst();

        int i = 0;
        String[] s = new String[cursor.getCount()];
        while (!cursor.isAfterLast()) {
            s[i++] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return s;
    }

    //gibt das Semester als Stringarray zurück...kann noch geändert werden
    private String[] getSemester() {
        Cursor cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.SEMESTERABFRAGE, null, null, null, null, NotenTabelle.SEMESTER);
        cursor.moveToFirst();
        int i = 0;
        String[] s = new String[cursor.getCount()];
        String[] vgl = new String[1];
        vgl[0]="";
        while (!cursor.isAfterLast()) {
            s[i] =  cursor.getString(0);
            if(s[i].equals(vgl[0])){
                s[i] = null;
                vgl[0] = cursor.getString(0);
            }
            else
                vgl[0]=s[i];
            i++;
           cursor.moveToNext();

        }
        cursor.close();
        return s;
    }

    //gibt das Fach als Stringarray zurück...kann noch geändert werden
    private String[] getFach() {
        Cursor cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.FACHABFRAGE, null, null, null, null,  NotenTabelle.SEMESTER);
        cursor.moveToFirst();
        int i = 0;
        String[] s = new String[cursor.getCount()];

        while (!cursor.isAfterLast()) {
            s[i++] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return s;
    }

    //gibt die Versuche als Stringarray zurück...kann noch geändert werden
    private String[] getVersuche() {
        Cursor cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.VERSUCHABFRAGE, null, null, null, null, NotenTabelle.SEMESTER);
        cursor.moveToFirst();

        int i = 0;
        String[] s = new String[cursor.getCount()];
        while (!cursor.isAfterLast()) {
            s[i++] = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return s;
    }

    //Fragt die Datenbank ab und befüllt das Listview
    //Parameter = true wenn die Methode aus dem Hauptprogramm/Thread aufgerufen wird
    private void setzeListview(boolean bool) {
         notenList = getNoten();
         semesterList = getSemester();
         fachList = getFach();
         versuchList = getVersuche();

        if (bool) setListAdapter(new CustomAdapterNoten(getActivity(),semesterList, fachList, versuchList, notenList));
        else {
            if(getActivity()==null) return;
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            setListAdapter(new CustomAdapterNoten(getActivity(),semesterList, fachList, versuchList, notenList));
                        }
                    });

        }
    }

    //Gibt eine Warnmeldung aus falls die Benutzerdaten falsch sind
    private void keineBenutzerdaten() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage(R.string.noUsernamePassword);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), EinstellungActivity.class);
                startActivity(intent);
            }
        });
        adb.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        adb.show();
    }

    class NotenHelper extends AsyncTask<String, Integer, Long> {

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            setzeListview(false);
        }

        @Override
        protected Long doInBackground(String... params) {
            NotenResolver nr;
            try {
                nr = new NotenResolver(getActivity());
                nr.getNoten();
            } catch (wrongUserdataException e) {
                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                                adb.setMessage(R.string.wrongUsernamePassword);
                                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity(), EinstellungActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                adb.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                adb.show();
                                setListAdapter(null);
                            }
                        });
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }

            return null;
        }

    }
}
