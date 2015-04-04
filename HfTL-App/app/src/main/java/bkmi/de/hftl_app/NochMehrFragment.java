package bkmi.de.hftl_app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Dieses Fragment ist nur für Testzwecke
 * Es kann geändert und später muss es gelöscht werden
 * Es ruft z.Z. den Inhalt einer Website auf (in HTML-Code)
 */
public class NochMehrFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private String testString="loading";

    //benötigte Views
    TextView tv;

    //Listner wird vllt benötigt???
    //private OnFragmentInteractionListener mListener;

    /**
     * Standardmethode um ein Fragment zu erzeugen
     *
     * @return A new instance of fragment NochMehrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NochMehrFragment newInstance(int sectionNumber) {
        NochMehrFragment fragment = new NochMehrFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NochMehrFragment() {
        // müss überschrieben werden
        // hat vllt noch Sinn

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Hier wird das Layout mit Hilfe der XML festgelegt
        View view = inflater.inflate(R.layout.fragment_noch_mehr, container, false);

        // Views in Variablen übernehmen, scheinbar nur an dieser Stelle möglich
        tv = (TextView) view.findViewById(R.id.text_noch_mehr_1);
        tv.setText(testString);
        try {
            URL url = new URL("https://qisweb.hispro.de/tel/rds?state=user&type=0");
            new HtmlThread().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //aufräumarbeiten
    }

    class HtmlThread extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(final URL... urls) {
            HttpURLConnection urlConnection = null;
            if (urls[0] != null) {
                try {
                    urlConnection = (HttpURLConnection) urls[0].openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                InputStream in = null;
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                testString = new Scanner(in, "UTF-8").useDelimiter("\\A").next();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(testString);
                    }
                });


            } finally {
                urlConnection.disconnect();
            }
            return Long.valueOf(1);
        }
    }
}
