package bkmi.de.hftl_app.Fragmente;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;

import bkmi.de.hftl_app.help.CustomAdapterStundenplan;
import bkmi.de.hftl_app.help.StundenplanEvent;
import bkmi.de.hftl_app.help.StundenplanResolver;
import bkmi.de.hftl_app.help.stundenplanException;


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
    String [] fachList;
    String [] dateList;
    String [] timeList;
    String [] categoryList;
    String [] roomList;
    Spinner spinner;
    Button button, buttonVor, buttonZuruck;
    StundenplanEvent events[][]=null;

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
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View stundenPlanView = inflater.inflate(R.layout.fragment_stundenplan, container, false);

        TextView hl = (TextView) stundenPlanView.findViewById(R.id.hl_Stundenplan);
        Typeface headline = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OCRA.TTF");
        hl.setTypeface(headline);
        // Inflate the layout for this fragment
        return stundenPlanView;
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

        if(savedInstanceState!=null){
            events=(StundenplanEvent[][])savedInstanceState.getSerializable(STUNDENPLANSPEICHER);
            erstelleStundenplan();
            return;
        }
        erzeugeDropdown();
        events= new StundenplanEvent[7][];

        //Listner zum Button hinzufügen
        button = (Button) getActivity().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(events[spinner.getSelectedItemPosition()]==null) new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                else erstelleStundenplan();
            }
        });
        buttonVor = (Button) getActivity().findViewById(R.id.button_vor);
        buttonVor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() < 6) {
                    spinner.setSelection(spinner.getSelectedItemPosition() + 1);
                    //if(events[spinner.getSelectedItemPosition()]==null) new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                    //else erstelleStundenplan();
                    if (spinner.getSelectedItemPosition() == 6) buttonVor.setEnabled(false);
                    else buttonVor.setEnabled(true);
                }

            }
        });

        buttonZuruck = (Button) getActivity().findViewById(R.id.button_zurueck);
        buttonZuruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() > 0) {
                    spinner.setSelection(spinner.getSelectedItemPosition() - 1);
                    //if(events[spinner.getSelectedItemPosition()]==null) new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                    //else erstelleStundenplan();
                    if (spinner.getSelectedItemPosition() == 0) buttonVor.setEnabled(false);
                    else buttonVor.setEnabled(true);

                }
            }
        });
        buttonZuruck.setEnabled(false);

       // new StundenplanHelper().execute(""); // Stundenplan wird abgerufen
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {

        //Todo kontrolle Studiengang
        super.onStart();

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STUNDENPLANSPEICHER, events);
    }

    private void erzeugeDropdown() {
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinner.getSelectedItemPosition() == 0) buttonZuruck.setEnabled(false);
                else buttonZuruck.setEnabled(true);
                if (spinner.getSelectedItemPosition() == 6) buttonVor.setEnabled(false);
                else buttonVor.setEnabled(true);
                if(events[spinner.getSelectedItemPosition()]==null) new StundenplanHelper().execute(spinner.getSelectedItem().toString());
                else erstelleStundenplan();
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

    protected void erstelleStundenplan(){
        Calendar calendar1= Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();
        ArrayList<String> liste=new ArrayList<String>();
        //String data[] = new String[events[spinner.getSelectedItemPosition()].length+7];
        StundenplanEvent tempevent;
        //if(events[spinner.getSelectedItemPosition()]==null) return;

        dateList = new String[events[spinner.getSelectedItemPosition()].length];
        fachList = new String[events[spinner.getSelectedItemPosition()].length];
        timeList = new String[events[spinner.getSelectedItemPosition()].length];
        roomList = new String[events[spinner.getSelectedItemPosition()].length];
        categoryList = new String[events[spinner.getSelectedItemPosition()].length];

        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd.MM.yyyy");

       if (events[spinner.getSelectedItemPosition()][0].isKeineDaten()){
            setListAdapter(new ArrayAdapter<>(getActivity(), simple_list_item_1, new String[]{"keine Daten"}));

            return;
        }

        //data[0]=format.format(events[spinner.getSelectedItemPosition()][0].getDate().getTime());
        tempevent=events[spinner.getSelectedItemPosition()][0];
        dateList[0] = format.format(tempevent.getDate().getTime());
        fachList[0] = tempevent.getFach();
        timeList[0] = tempevent.getUhrzeitStart() + " - " + tempevent.getUhrzeitEnde()+ " Uhr, ";
        categoryList[0] = tempevent.getKategorie();
        roomList[0] = tempevent.getRaum();

        for (int i=1; i<events[spinner.getSelectedItemPosition()].length; i++){
            calendar1.setTime(events[spinner.getSelectedItemPosition()][i-1].getDate());
            tempevent=events[spinner.getSelectedItemPosition()][i];
            calendar2.setTime(tempevent.getDate());
            roomList[i] = tempevent.getRaum();
            fachList[i] = tempevent.getFach();
            timeList[i] = tempevent.getUhrzeitStart() + " - " + tempevent.getUhrzeitEnde() + " Uhr, ";
            categoryList[i] = tempevent.getKategorie();
            if(calendar1.get(Calendar.DAY_OF_WEEK)!=calendar2.get(Calendar.DAY_OF_WEEK)){
                //data[i+j]=format.format(events[spinner.getSelectedItemPosition()][i-1].getDate());
                dateList[i]=format.format(events[spinner.getSelectedItemPosition()][i].getDate());
                //j++;
            }
            else
                dateList[i]=null;
           //fachList=tempevent.getFach() + "\n";
           //timeList = tempevent.getUhrzeitStart() + "-" + tempevent.getUhrzeitEnde();
           // temp+=tempevent.getUhrzeitStart() + "-" + tempevent.getUhrzeitEnde() + ", " + tempevent.getRaum() + "\n";
           //categoryList=tempevent.getKategorie();
            //temp+=tempevent.getKategorie();
           // roomList=tempevent.getRaum();
            //getView(dateList,fachList, timeList, roomList, categoryList);
            //data[i+j]=temp;
        }
        /*for (StundenplanEvent event: events[spinner.getSelectedItemPosition()] ){
                temp=event.getFach() + "\n";
                temp+=event.getUhrzeitStart() + "-" + event.getUhrzeitEnde() + ", " + event.getRaum() + "\n";
                temp+=event.getKategorie();
                data[i++]=temp;
            }
        */
        setListAdapter(new CustomAdapterStundenplan(getActivity(), dateList, fachList, timeList, roomList,  categoryList));
            //setListAdapter(new ArrayAdapter<>(getActivity(), simple_list_item_1, liste));

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
                }
                else {
                    events[spinner.getSelectedItemPosition()] = sr.erzeugeStundenplan(params[0]);
                }
            } catch (stundenplanException e) {
                StundenplanEvent event = new StundenplanEvent();
                event.setKeineDaten(true);
                StundenplanEvent hilfsArray[] = new StundenplanEvent[1];
                hilfsArray[0]=event;
                events[spinner.getSelectedItemPosition()] = hilfsArray;
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
