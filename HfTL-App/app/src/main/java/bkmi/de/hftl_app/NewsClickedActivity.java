package bkmi.de.hftl_app;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import bkmi.de.hftl_app.Fragmente.NewsFragment;
import bkmi.de.hftl_app.help.NewsResolver;


public class NewsClickedActivity extends ActionBarActivity {

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    ProgressDialog ladebalken;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_clicked);

        final Bundle extras = getIntent().getExtras();
        url=extras.getString(NewsFragment.TERMINDETAIL);

        tv1 = (TextView) findViewById(R.id.tv_news_date);
        Typeface datum = Typeface.createFromAsset(this.getAssets(), "fonts/TeleGrotNorm.ttf");
        tv1.setTypeface(datum);


        tv2 = (TextView) findViewById(R.id.tv_news_hl1);
        Typeface headline1 = Typeface.createFromAsset(this.getAssets(), "fonts/OCRA.TTF");
        tv2.setTypeface(headline1);

        tv3 = (TextView) findViewById(R.id.tv_news_hl2);
        Typeface headline2 = Typeface.createFromAsset(this.getAssets(), "fonts/TeleGrotNorm.ttf");
        tv3.setTypeface(headline2);


        tv4 = (TextView) findViewById(R.id.tv_news_content);
        Typeface inhalt = Typeface.createFromAsset(this.getAssets(), "fonts/TeleGrotNorm.ttf");
        tv4.setTypeface(inhalt);

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
                    tv1.setText(s[2]);
                    tv2.setText(s[0]);
                    tv3.setText(s[1]);
                    tv4.setText(s[3]);
                }
            });

        }
    }

}
