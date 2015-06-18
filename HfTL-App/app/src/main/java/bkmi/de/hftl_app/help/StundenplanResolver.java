package bkmi.de.hftl_app.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

/**
 * Eine Hilfsklasse die die Stundenpläne abruft und bearbeitet
 */
public class StundenplanResolver {

    Context context;
    ICalendar ical;

    public StundenplanResolver(Context context) {
        this.context=context;
    }

    private void erstelleIcal(){
        URL url;
        String urlAdresse;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        urlAdresse = prefs.getString("studiengang", "Fehler" );

        try {
            url = new URL (findeIcal(urlAdresse));
            InputStream in = url.openConnection().getInputStream();

            ical = Biweekly.parse(in).first();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findeIcal(String urlAdresse) {
        URL url = null;
        String s = "";
        try {
            url = new URL(urlAdresse);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            Document doc = Jsoup.parse(connection.getInputStream(), "UTF-8", urlAdresse);

            Elements e = doc.getElementsByAttributeValue("title", "iCalendar Export für Outlook");
            s = e.get(0).parent().attr("href");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public String[] icalAusgeben(){
        String temp;
        erstelleIcal();
        List<VEvent> events = ical.getEvents();
        String[] strings = new String[events.size()];
        int i=0;
        for(VEvent event:events){
            temp = event.getSummary().getValue() + "\n";
            temp += event.getDateStart().getValue() + "\n";
            temp += event.getDateEnd().getValue() + "\n";
            temp += event.getLocation().getValue();
            strings[i++]=temp;
        }
        return strings;
    }
}
