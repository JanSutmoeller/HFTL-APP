package bkmi.de.hftl_app.Fragmente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bkmi.de.hftl_app.EinstellungActivity;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.NotenResolver;
import bkmi.de.hftl_app.help.TextSecure;
import bkmi.de.hftl_app.help.wrongUserdataException;

public class NotenFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView tv;

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
        return inflater.inflate(R.layout.fragment_noten, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NewsActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        tv = (TextView) getActivity().findViewById(R.id.test);


        //Kontrolle ob Benutzername und Passwort befüllt sind
        if(!testeBenutzerdaten()){
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setMessage(R.string.noUsernamePassword);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), EinstellungActivity.class);
                    startActivity(intent);
                } });
            adb.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                } });
            adb.show();
            tv.setText("keine Daten vorhanden");
        }

        //falls Benutzername/Password vorhanden --> Noten abfragen
        else{
            NotenHelper nh = new NotenHelper();
            nh.execute("bla");
        }
    }

    private boolean testeBenutzerdaten() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sp.getString("username","");
        String password = sp.getString("password","");
        TextSecure ts;
        try {
            ts = new TextSecure(getActivity());
            password= ts.decrypt(password);
            username= ts.decrypt(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(username.equals("") || password.equals(""));
    }

    class NotenHelper extends AsyncTask<String, Integer, Long> {

        String s;
        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            //ListView befüllen
            getActivity().runOnUiThread(
                    new Runnable() {
                @Override
                public void run() {
                    tv.setText(s);
                }
            });
        }

        @Override
        protected Long doInBackground(String... params) {
            NotenResolver nr;
            try {
                nr = new NotenResolver(getActivity());
                s=nr.getNoten();
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
                                s="keine Daten vorhanden";
                            }
                            });
                        }

            return null;
        }

        }
}
