package bkmi.de.hftl_app.Fragmente;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import bkmi.de.hftl_app.EinstellungActivity;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.NotenResolver;
import bkmi.de.hftl_app.help.StundenplanResolver;
import bkmi.de.hftl_app.help.wrongUserdataException;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stundenplan, container, false);
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
        StundenplanHelper sh = new StundenplanHelper();
        sh.execute("");
    }

    class StundenplanHelper extends AsyncTask<String, Integer, Long> {
        String[] stringArray;

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListAdapter(new ArrayAdapter<>(getActivity(), simple_list_item_1, stringArray));
                }
            });

        }

        @Override
        protected Long doInBackground(String... params) {
            StundenplanResolver sr = new StundenplanResolver(getActivity());
            stringArray = sr.icalAusgeben();
            return null;
        }

    }
}
