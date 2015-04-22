package bkmi.de.hftl_app;

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
    TextView tv5;
    TextView tv6;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_clicked);

        final Bundle extras = getIntent().getExtras();
        url=extras.getString(NewsFragment.TERMINDETAIL);

        tv1 = (TextView) findViewById(R.id.tv_news_clicked1);
        tv2 = (TextView) findViewById(R.id.tv_news_clicked2);
        tv3 = (TextView) findViewById(R.id.tv_news_clicked3);
        tv4 = (TextView) findViewById(R.id.tv_news_clicked4);
        tv5 = (TextView) findViewById(R.id.tv_news_clicked5);
        tv6 = (TextView) findViewById(R.id.tv_news_clicked6);

        new DetailHelper().execute();


    }

    class DetailHelper extends AsyncTask<String, Integer, Long>{

        String[] s;
        @Override
        protected Long doInBackground(String... params) {

            NewsResolver nr= new NewsResolver(url);
            s = nr.getDetailsStringArray();

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv1.setText(s[0]);
                    tv2.setText(s[1]);
                    tv3.setText(s[2]);
                    tv4.setText(s[3]);
                    tv5.setText(s[4]);
                    tv6.setText(s[5]);
                }
            });

        }
    }

}
