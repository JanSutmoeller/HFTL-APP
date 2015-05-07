package bkmi.de.hftl_app.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Klasse die eine Verbindung zu HIS/QIS aufbaut und dort die Noten abfragt
 */
public class NotenResolver {

    Context context;
    URL url;
    String s;   //kann wieder gelöscht werden, nur für Testzwecke
    Document doc;
    HttpsURLConnection connection;
    CookieManager cookieManager;

    public NotenResolver(Context context) {
        this.context = context;

        s="";   //Testzwecke

        //KEKSE!!!
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        init();
    }

    //Login im QIS durchführen
    private boolean init() {

        String user;
        String password;

        //Zugriff auf die Daten in den Einstellungen
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        user = sp.getString("username", "Fehler");

        //Entschlüsseln
        TextSecure ts;
        try {
            ts = new TextSecure(context);
            password = ts.decrypt(sp.getString("password", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //Hier erfolgt der eigentliche Login über eine HTTP Post methode
        String urlParameters = "asdf=" + user + "&fdsa=" + password;

        try {
            url = new URL("https://qisweb.hispro.de/tel/rds?state=user&type=1&;category=auth.login&startpage=portal.vm&breadCrumbSource=portal");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(urlParameters.length()));
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

            writer.write(urlParameters);
            writer.flush();
            connection.getContent();

            //TODO Hier muss noch überprüft werden ob der Login erfolgreich war

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //Zugriff auf Noten
    public String getNoten() {

        Element element;
        Elements elements;

        String notenspiegelInfo= qisNavigation();

        url = null;
        try {
            url = new URL(notenspiegelInfo);
            connection = (HttpsURLConnection) url.openConnection();
            doc = Jsoup.parse(connection.getInputStream(), "UTF-8", notenspiegelInfo);

            elements = doc.getElementsByAttributeValue("src", "/QIS/images//his_info3.gif");
            for (Element ele : elements) {
                element = ele.parent().parent().parent();
                s += "Fach: " + element.child(1).ownText()+ " Note: " + element.child(3).ownText() + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;

    }

    //Navigiert zu Notenspiegel --> Info
    //gibt die URL als String zurück
    private String qisNavigation() {
        Elements e;
        String seite = "https://qisweb.hispro.de/tel/rds?state=user&type=0&category=menu.browse&breadCrumbSource=portal&startpage=portal.vm";
        try {
             url = new URL(seite);
            connection = (HttpsURLConnection) url.openConnection();
            doc = Jsoup.parse(connection.getInputStream(), "UTF-8", seite);
            e = doc.getElementsByAttribute("href");
            seite = e.get(21).attr("href");//Suche Prüfungsverwaltung...könnte noch überarbeitet werden damit es nicht so statisch ist

            url = new URL(seite);
            connection = (HttpsURLConnection) url.openConnection();
            doc = Jsoup.parse(connection.getInputStream(), "UTF-8", seite);
            e = doc.getElementsByAttribute("href");
            seite= e.get(28).attr("href");//Suche Notenspiegel

            url = new URL(seite);
            connection = (HttpsURLConnection) url.openConnection();
            doc = Jsoup.parse(connection.getInputStream(), "UTF-8", seite);
            e = doc.getElementsByAttribute("href");
            return e.get(29).attr("href");//Suche Leistung anzeigen


            // iterate HttpCookie object
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return "FEHLER";
    }

    //Die Methode soll als Platzhalter für die Login-kontrolle dienen
    //TODO Programmierung :-)
    private String testeZugang() {

        /*
        BufferedReader reader;
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(urlParameters);
                writer.flush();
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                for (String line; (line = reader.readLine()) != null;)
                    s+=line;
            }
            reader.close();
         */

        return "möp";
    }
}
