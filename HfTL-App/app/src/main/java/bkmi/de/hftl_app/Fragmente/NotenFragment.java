package bkmi.de.hftl_app.Fragmente;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Typeface;

import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.NotenResolver;
import bkmi.de.hftl_app.help.TextSecure;

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
        NotenHelper nh = new NotenHelper();
        nh.execute("bla");
        }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

       // tv = (TextView) getActivity().findViewById(R.id.test);

        /*Beispiel wie man auf verschlüsselte Daten zugreift

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String test = sp.getString("password", "");
        TextSecure ts = null;
        try {
            ts = new TextSecure(getActivity());
            tv.setText(ts.decrypt(test));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */


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
            NotenResolver nr= new NotenResolver(getActivity());
            s=nr.getNoten();
            return null;
        }

        }
}
