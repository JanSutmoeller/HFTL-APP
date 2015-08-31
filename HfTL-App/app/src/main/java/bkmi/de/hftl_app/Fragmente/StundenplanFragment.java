package bkmi.de.hftl_app.Fragmente;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import bkmi.de.hftl_app.EinstellungActivity;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;

import bkmi.de.hftl_app.help.Customadapter.CustomAdapterStundenplan;
import bkmi.de.hftl_app.help.Events.StundenplanEvent;
import bkmi.de.hftl_app.help.Resolver.StundenplanResolver;
import bkmi.de.hftl_app.help.Exceptions.stundenplanException;


import static android.R.layout.simple_list_item_1;

/**
 * Ein Fragment das den Stundenplan des ausgewählten Studienganges (Einstellungen) ausgibt
 */
public class StundenplanFragment extends ListFragment {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String STUNDENPLANSPEICHER = "stundenplan";
    String[] fachList;
    String[] dateList;
    String[] timeList;
    String[] categoryList;
    String[] roomList;
    Spinner spinner;
    Button buttonVor, buttonZuruck;
    StundenplanEvent events[][] = null;

    /**
     * Erstellt ein neues StundenplanFragment.
     */
    public static StundenplanFragment newInstance(int sectionNumber) {
        StundenplanFragment fragment = new StundenplanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StundenplanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_stundenplan, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NewsActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            events = (StundenplanEvent[][]) savedInstanceState.getSerializable(STUNDENPLANSPEICHER);
            erstelleStundenplan();
            return;
        }
        erzeugeDropdown();
        events = new StundenplanEvent[7][];

        buttonVor = (Button) getActivity().findViewById(R.id.button_vor);
        buttonVor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonVor.setPressed(true);
                if (spinner.getSelectedItemPosition() < 6) {
                    spinner.setSelection(spinner.getSelectedItemPosition() + 1);
                    if (spinner.getSelectedItemPosition() == 6)
                        buttonVor.setEnabled(false);
                    else
                        buttonVor.setEnabled(true);

                }

            }
        });

        buttonZuruck = (Button) getActivity().findViewById(R.id.button_zurueck);
        buttonZuruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonZuruck.setPressed(true);
                if (spinner.getSelectedItemPosition() > 0) {
                    spinner.setSelection(spinner.getSelectedItemPosition() - 1);
                    if (spinner.getSelectedItemPosition() == 0)
                        buttonZuruck.setEnabled(false);
                    else
                        buttonZuruck.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STUNDENPLANSPEICHER, events);
    }

    private void erzeugeDropdown() {
        //Dropdown aus XML suchen
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        //Listner für das Dropdownmenü
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Falls erste Woche deaktiviere "zurück" Button
                if (spinner.getSelectedItemPosition() == 0)
                    buttonZuruck.setEnabled(false);
                else
                    buttonZuruck.setEnabled(true);
                //Falls letzte Woche deaktiviere "vor" Button
                if (spinner.getSelectedItemPosition() == 6)
                    buttonVor.setEnabled(false);
                else
                    buttonVor.setEnabled(true);
                //wenn das Datum zum ersten mal aufgerufen wird, muss der Stundenplan gedownloadet werden
                if (pruefeStudiengang()) {
                    if (events[spinner.getSelectedItemPosition()] == null) {
                        if (!NewsFragment.isOnline(getActivity())) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage(R.string.noNetwork).setCancelable(false).setPositiveButton(
                                            getActivity().getResources().getText(android.R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // do nothing
                                                }
                                            });
                                    builder.show();
                                }
                            });
                            StundenplanEvent event = new StundenplanEvent();
                            event.setKeineDaten(true);
                            StundenplanEvent hilfsArray[] = new StundenplanEvent[1];
                            hilfsArray[0] = event;
                            events[spinner.getSelectedItemPosition()] = hilfsArray;
                        } else {
                            new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                        }
                    } else {
                        erstelleStundenplan();
                    }


                } else keinStudiengang();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> list = new ArrayList<String>();
        String temp;

        Calendar calendar = Calendar.getInstance();     //heutiges Datum
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);    //Kalender auf Montag setzen
        Date date = calendar.getTime();
        SimpleDateFormat formatEins = new SimpleDateFormat("ww: dd.MM.yyyy");
        SimpleDateFormat formatZwei = new SimpleDateFormat(" - dd.MM.yyyy");
        temp = "KW " + formatEins.format(date);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //Kalender aus Sonntag setzen
        date = calendar.getTime();
        temp += formatZwei.format(date);

        list.add(temp);
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DATE, 1); //Kalender steht auf Sonntag --> ein Tag addieren = Montag der nächsten Woche
            date = calendar.getTime();
            temp = "KW " + formatEins.format(date);
            calendar.add(Calendar.DATE, 6); //Kalender steht auf Montag --> sechs Tage addieren = Sontag der selben Woche
            date = calendar.getTime();
            temp += formatZwei.format(date);
            list.add(temp);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void keinStudiengang() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage(R.string.keinStudiengang);
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

    //Prüft ob ein Studiengang ausgewähl wurde
    private boolean pruefeStudiengang() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String studiengang = sp.getString("studiengang", "");

        //Falls der Studiengang leer ist wird false zurück gegeben
        if ("".equals(studiengang)) return false;
        return true;
    }

    protected void erstelleStundenplan() {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        StundenplanEvent tempevent;
        //if(events[spinner.getSelectedItemPosition()]==null) return;

        dateList = new String[events[spinner.getSelectedItemPosition()].length];
        fachList = new String[events[spinner.getSelectedItemPosition()].length];
        timeList = new String[events[spinner.getSelectedItemPosition()].length];
        roomList = new String[events[spinner.getSelectedItemPosition()].length];
        categoryList = new String[events[spinner.getSelectedItemPosition()].length];

        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd.MM.yyyy");

        if (events[spinner.getSelectedItemPosition()][0].isKeineDaten()) {
            setListAdapter(new ArrayAdapter<>(getActivity(), simple_list_item_1, new String[]{"keine Daten"}));

            return;
        }

        tempevent = events[spinner.getSelectedItemPosition()][0];
        dateList[0] = format.format(tempevent.getDate().getTime());
        fachList[0] = tempevent.getFach();
        timeList[0] = tempevent.getUhrzeitStart() + " - " + tempevent.getUhrzeitEnde() + " Uhr, ";
        categoryList[0] = tempevent.getKategorie();
        roomList[0] = tempevent.getRaum();

        for (int i = 1; i < events[spinner.getSelectedItemPosition()].length; i++) {
            calendar1.setTime(events[spinner.getSelectedItemPosition()][i - 1].getDate());
            tempevent = events[spinner.getSelectedItemPosition()][i];
            calendar2.setTime(tempevent.getDate());
            roomList[i] = tempevent.getRaum();
            fachList[i] = tempevent.getFach();
            timeList[i] = tempevent.getUhrzeitStart() + " - " + tempevent.getUhrzeitEnde() + " Uhr, ";
            categoryList[i] = tempevent.getKategorie();
            if (calendar1.get(Calendar.DAY_OF_WEEK) != calendar2.get(Calendar.DAY_OF_WEEK)) {
                dateList[i] = format.format(events[spinner.getSelectedItemPosition()][i].getDate());
            } else
                dateList[i] = null;

        }
        setListAdapter(new CustomAdapterStundenplan(getActivity(), dateList, fachList, timeList, roomList, categoryList));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stundenplan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            synchronisiereKalender();
            return true;
        }

        if (id == R.id.aktualisieren){
            if (pruefeStudiengang()) {
                    if (!NewsFragment.isOnline(getActivity())) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(R.string.noNetwork).setCancelable(false).setPositiveButton(
                                        getActivity().getResources().getText(android.R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // do nothing
                                            }
                                        });
                                builder.show();
                            }
                        });
                        StundenplanEvent event = new StundenplanEvent();
                        event.setKeineDaten(true);
                        StundenplanEvent hilfsArray[] = new StundenplanEvent[1];
                        hilfsArray[0] = event;
                        events[spinner.getSelectedItemPosition()] = hilfsArray;
                    } else {
                        new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                    }

            } else keinStudiengang();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void synchronisiereKalender() {


        long calID = 1;
        if (!events[spinner.getSelectedItemPosition()][0].isKeineDaten()) {
            for (int i = 0; i < events[spinner.getSelectedItemPosition()].length; i++) {
                StundenplanEvent tempevent = events[spinner.getSelectedItemPosition()][i];
                ContentResolver cr = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, tempevent.getKalenderStart());
                values.put(CalendarContract.Events.DTEND, tempevent.getKalenderEnde());
                values.put(CalendarContract.Events.TITLE, tempevent.getFach());
                values.put(CalendarContract.Events.EVENT_LOCATION, tempevent.getRaum());
                values.put(CalendarContract.Events.DESCRIPTION, tempevent.getKategorie());
                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            }
            Toast toast = Toast.makeText(getActivity(), "Stundenplan erfolgreich eingefügt", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getActivity(), "keine Daten vorhanden", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    class StundenplanHelper extends AsyncTask<String, Integer, Long> {
        ProgressDialog ladebalken;

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if (getActivity() == null) return;
            erstelleStundenplan();
            ladebalken.dismiss();

        }

        @Override
        protected Long doInBackground(String... params) {
            StundenplanResolver sr = new StundenplanResolver(getActivity());
            try {
                if (params[0].equals("")) {
                    events[0] = sr.erzeugeStundenplan(null);
                } else {
                    events[spinner.getSelectedItemPosition()] = sr.erzeugeStundenplan(params[0]);
                }
            } catch (stundenplanException e) {
                StundenplanEvent event = new StundenplanEvent();
                event.setKeineDaten(true);
                StundenplanEvent hilfsArray[] = new StundenplanEvent[1];
                hilfsArray[0] = event;
                events[spinner.getSelectedItemPosition()] = hilfsArray;
                if(e.fehlerNetzwerk==1){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.networkError).setCancelable(false).setPositiveButton(
                                    getActivity().getResources().getText(android.R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // do nothing
                                        }
                                    });
                            builder.show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ladebalken = ProgressDialog.show(getActivity(), "Bitte warten", "Stundenplan wird geladen", true, false);
        }
    }
}
