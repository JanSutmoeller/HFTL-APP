package bkmi.de.hftl_app.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    URL url;
    HttpsURLConnection connection;
    CookieManager cookieManager;
    String urlParameters;

    public StundenplanResolver(Context context) {
        this.context = context;
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    private void erstelleIcal(@Nullable String woche) throws stundenplanException{

        String urlAdresse; //Hier wird der Link zum jeweiligen Studiengang eingefügt
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        urlAdresse = prefs.getString("studiengang", "Fehler"); // Aus den Einstellungen wird der ausgewählte Studiengang gesucht

        if (woche != null) {

            // Es wird im übergebenen String nach der KW und dem Jahr gesucht
            // Siehe Javadoc Class Pattern
            String temp="";
            Pattern pattern = Pattern.compile("(?<=KW\\s)\\d{2}");
            Matcher matcher = pattern.matcher(woche);
            if(matcher.find())
                temp=matcher.group() + "_";
            pattern = Pattern.compile("\\d{4}");
            matcher = pattern.matcher(woche);
            if(matcher.find())
                temp += matcher.group();
            // Erzeugter String hat die Form KW_Jahr
            urlParameters = "week=" + temp;
        }
        try {
            url = new URL(findeIcal(urlAdresse)); //Navigation zur HIS/QIS-Seite
            InputStream in = url.openConnection().getInputStream();

            ical = Biweekly.parse(in).first(); //Ical parser wird erzeugt
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String findeIcal(String urlAdresse) throws stundenplanException{

        String s = "";
        Document doc;
        URL url, url2;

        try {
            url2 = new URL(urlAdresse);
            if (urlParameters != null) { //Falls eine andere Woche ausgewählt wurde
                url = new URL("https://qisweb.hispro.de/tel/rds?state=wplan&amp;act=add&amp;show=plan");

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(urlParameters.length()));

                connection = (HttpsURLConnection) url2.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(urlParameters); //POST an HIQ/QIS
                writer.flush(); //Stream leeren
                doc = Jsoup.parse(connection.getInputStream(), "UTF-8", urlAdresse); //HTML-Parser Dokument um das Ical-Objekt zufinden
            }

            else {
                connection = (HttpsURLConnection) url2.openConnection();

                doc = Jsoup.parse(connection.getInputStream(), "UTF-8", urlAdresse);
            }

            Elements e = doc.getElementsByAttributeValue("title", "iCalendar Export für Outlook");//suche Ical
            if (e.size()==0) throw new stundenplanException(); //Falls es kein Stundenplan gibt wird eine Exception geworfen
            s = e.get(0).parent().attr("href"); //ansonsten wird der Link zum Ical-Objekt gespeichert
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public StundenplanEvent[] erzeugeStundenplan(@Nullable String woche) throws stundenplanException{

        erstelleIcal(woche); //Ical suchen und erzeugen

        List<VEvent>  events = ical.getEvents(); //alle Events auslesen
        StundenplanEvent[] stundenplan = new StundenplanEvent[events.size()];

        int i = 0;
        for (VEvent event : events) {
            stundenplan[i]=new StundenplanEvent();
            stundenplan[i].setFach(event.getSummary().getValue());  //Fach auslesen
            stundenplan[i].setDate(event.getDateStart().getValue());
            stundenplan[i].setUhrzeitStart(String.format("%02d:%02d", event.getDateStart().getValue().getHours(), event.getDateStart().getValue().getMinutes())); //Startuhrzeit auslesen
            stundenplan[i].setUhrzeitEnde(String.format("%02d:%02d", event.getDateEnd().getValue().getHours(), event.getDateEnd().getValue().getMinutes())); //Enduhrzeit auslesen
            stundenplan[i].setRaum(event.getLocation().getValue()); //Raum auslesen
            stundenplan[i].setKategorie(event.getCategories().get(0).getValues().get(0)); //Vorlesungstyp auslesen
            i++;
        }

        return stundenplan;
    }

    /*public String[] icalAusgeben(@Nullable String woche) throws stundenplanException{
        String temp;
        erstelleIcal(woche);
        List<VEvent> events = ical.getEvents();
        String[] strings = new String[events.size()];
        int i = 0;
        for (VEvent event : events) {
            temp = event.getSummary().getValue() + "\n";
            temp += String.format("%02d:%02d",event.getDateStart().getValue().getHours(), event.getDateStart().getValue().getMinutes()) + "\n";
            temp += event.getDateEnd().getValue() + "\n";
            temp += event.getCategories().get(0).getValues().get(0)+ "\n";
            temp += event.getLocation().getValue();
            strings[i++] = temp;
        }
        return strings;
    }
    */
}
