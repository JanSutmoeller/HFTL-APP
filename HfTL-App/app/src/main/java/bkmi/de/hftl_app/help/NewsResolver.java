package bkmi.de.hftl_app.help;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Klasse um die News von der HftL-Seite zu laden
 */
public class NewsResolver {

    Document doc;
    ArrayList<HftlEvent> termine;
    Activity activity;

    /*
    * Konstruktor initialisiert die übergebene URL als Document
     */
    public NewsResolver(String url) {
        this.activity = activity;
        if(!getHtmlAsDoc(url));
    }

    private boolean getHtmlAsDoc(String url) {
        try {
            doc = Jsoup.connect(url).get();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----- Funktionen für das NewsFragment -----

    /*
    * Erzeugt eine Liste mit den nächsten Terminen von der Hftl-Startseite
     */
    private void getHftlEvent(){
        termine= new ArrayList<HftlEvent>();

        Elements elements = doc.getElementsByClass("news-events-item-start");
        for(int i=0; i<elements.size(); i++){
            HftlEvent termin = new HftlEvent();
            termin.month= elements.get(i).child(0).child(0).text();
            termin.day= elements.get(i).child(0).child(1).text();
            termin.time= elements.get(i).child(0).ownText();
            termin.text=elements.get(i).child(1).child(1).ownText();
            termin.url="https://www.hft-leipzig.de/"+elements.get(i).child(1).child(0).child(0).attr("href");
            termine.add(termin);
        }
    }

    /*
    * Erzeugt eine String-Array mit allen Terminen
     */
    public String[] getTermineStringArray(){

        getHftlEvent();
        if(termine.size()==0) return null; //Falls keine Termine anstehen

        String[] s = new String[termine.size()+1];
        s[0]="Die nächsten Termin sind:";   // Für die ListView das erste Element,

        int i = 1;
        for(HftlEvent termin:termine ){
            s[i++]= "Am " + termin.day + ". " + termin.month + " um " + termin.time + ":\n" + termin.text + "\nFür mehr Infos klicken." ;
        }

        return s;
    }

    /*
    * Gibt die URL des Termines an Position "position" zurück, wird für die Detailanzeige benötigt.
     */
    public String getUrlAsString(int position){
        return termine.get(position).url;
    }

    /* ----- Funktion für die NewsClickedActivty -----
    *   Sucht nach den Details auf der HfTl-Seite und gibt sie als String-Array zurück
     */
    public String[] getDetailsStringArray(){
        String[] s=new String[6];
        Elements elements = doc.getElementsByClass("news-single-item");

        s[0]=elements.get(0).child(0).text()+"\n";   //Überschrift
        s[1]=elements.get(0).child(1).text()+"\n";   //Subhead
        s[2]=elements.get(0).child(2).ownText()+"\n";//Zeit
        s[3]=elements.get(0).child(3).ownText();//Ort
        s[4]=elements.get(0).child(4).ownText()+"\n";//Adresse
        s[5]=elements.get(0).child(5).text();   //Text

        return s;
    }

}
