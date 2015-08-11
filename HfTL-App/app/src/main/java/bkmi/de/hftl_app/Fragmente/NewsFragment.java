package bkmi.de.hftl_app.Fragmente;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.NewsClickedActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.CustomAdapterNews;
import bkmi.de.hftl_app.help.NewsResolver;

import static android.R.layout.simple_list_item_1;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends ListFragment {

    public static final String TERMINDETAIL= "Details";
    public static final String ARRAYSPEICHER = "Array";

    NewsResolver newsResolver;
    Intent intent;
    String stringArray[];
    Button button;
    ArrayAdapter<String> arrayAdapter;
    String headlineList[];
    String dateList[];
    String contentList[];

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsFragment newInstance(int sectionNumber) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        //zeigeNews();

       return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
             switch (position){
                default:
                    intent = new Intent(getActivity(), NewsClickedActivity.class);
                    intent.putExtra(TERMINDETAIL, newsResolver.getUrlAsString(position));
                    startActivity(intent);
                    break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NewsActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            stringArray=savedInstanceState.getStringArray(ARRAYSPEICHER);
            arrayAdapter = new ArrayAdapter<String>(getActivity(), simple_list_item_1, stringArray);
            setListAdapter(arrayAdapter);
            return;
        }
        zeigeNews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(ARRAYSPEICHER, stringArray);
    }

    @Override
    public void onStart() {
        super.onStart();
        button = (Button) getView().findViewById(R.id.button_refresh_news);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zeigeNews();
            }
        });
    }

    private void zeigeNews() {

        if(isOnline(getActivity())){
            NewsHelper nh = new NewsHelper();
            nh.execute();
        }
        else getActivity().runOnUiThread(new Runnable() {
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
    }

    //Netzwerkstatus abrufen
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    class NewsHelper extends AsyncTask<String, Integer, Long> {


        @Override
        protected Long doInBackground(String... params) {
            newsResolver = new NewsResolver("https://www.hft-leipzig.de/de/start.html");
            dateList=newsResolver.writeNewsListDate();
            headlineList=newsResolver.writeNewsListHeadline();
            contentList=newsResolver.writeNewsListContent();



            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

           // arrayAdapter = new ArrayAdapter<String>(getActivity(), simple_list_item_1, stringArray);
            if(getActivity()==null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  setListAdapter(arrayAdapter);
                    setListAdapter(new CustomAdapterNews(getActivity(), dateList, headlineList, contentList));
                }
            });
        }
    }

}
