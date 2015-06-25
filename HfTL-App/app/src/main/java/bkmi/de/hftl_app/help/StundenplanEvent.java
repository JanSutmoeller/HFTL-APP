package bkmi.de.hftl_app.help;

import java.io.Serializable;
import java.util.Date;


public class StundenplanEvent implements Serializable {
    private String Fach;
    private String uhrzeitStart;
    private String uhrzeitEnde;
    private String raum;
    private String kategorie;
    private boolean keineDaten=false;
    private Date date;

    public String getFach() {
        return Fach;
    }

    public void setFach(String fach) {
        Fach = fach;
    }

    public String getUhrzeitStart() {
        return uhrzeitStart;
    }

    public void setUhrzeitStart(String uhrzeitStart) {
        this.uhrzeitStart = uhrzeitStart;
    }

    public String getUhrzeitEnde() {
        return uhrzeitEnde;
    }

    public void setUhrzeitEnde(String uhrzeitEnde) {
        this.uhrzeitEnde = uhrzeitEnde;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public boolean isKeineDaten() {
        return keineDaten;
    }

    public void setKeineDaten(boolean keineDaten) {
        this.keineDaten = keineDaten;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
