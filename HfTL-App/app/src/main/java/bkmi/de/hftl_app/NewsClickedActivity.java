package bkmi.de.hftl_app;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import bkmi.de.hftl_app.Fragmente.NewsFragment;
import bkmi.de.hftl_app.help.NewsResolver;


public class NewsClickedActivity extends ActionBarActivity {

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    ProgressDialog ladebalken;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_clicked);

        final Bundle extras = getIntent().getExtras();
        url=extras.getString(NewsFragment.TERMINDETAIL);

        new DetailHelper().execute();
    }

    class DetailHelper extends AsyncTask<String, Integer, Long>{

        String[] s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ladebalken = ProgressDialog.show(NewsClickedActivity.this, "Bitte warten", "Nachricht wird geladen", true, false);
        }

        @Override
        protected Long doInBackground(String... params) {

            NewsResolver nr= new NewsResolver(url);
            s = nr.getDetailsStringArray();

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            ladebalken.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv1=(TextView)findViewById(R.id.tv_news_date);
                    tv1.setTextIsSelectable(true);
                    tv1.setText(s[2]);
                    tv2=(TextView)findViewById(R.id.tv_news_hl1);
                    tv2.setTextIsSelectable(true);
                    tv2.setText(s[0]);
                    tv3=(TextView)findViewById(R.id.tv_news_hl2);
                    tv3.setTextIsSelectable(true);
                    tv3.setText(s[1]);
                    tv4=(TextView)findViewById(R.id.tv_news_content);
                    tv4.setClickable(true);
                    tv4.setTextIsSelectable(true);
                    tv4.setMovementMethod((LinkMovementMethod.getInstance()));
                    tv4.setLinkTextColor(getResources().getColor(R.color.grün));
                    tv4.setText(Html.fromHtml(s[3]));
                }
            });

        }
    }

}
