package bkmi.de.hftl_app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class Impressum extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);

        TextView textView = (TextView) findViewById(R.id.textView14);
        textView.setText(Html.fromHtml(
                        "<a href=\"https://www.hft-leipzig.de/de/kontakt/impressum.html\">https://www.hft-leipzig.de/de/kontakt/impressum.html</a> "
                        ));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
