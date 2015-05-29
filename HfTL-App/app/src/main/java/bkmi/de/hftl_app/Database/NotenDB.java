package bkmi.de.hftl_app.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Eine Klasse für die Datenbank in der Anwendung
 */
public class NotenDB extends SQLiteOpenHelper {
    private static final String DATENBANKNAME = "hftlapp.db";
    private static final int DATENBANK_VERSION = 1;
    private static NotenDB sINSTANCE;
    private static final Object sLOCK = "";

    //Einmaliges Erzeugen der Datenbank
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NotenTabelle.SQL_CREATE);
    }

    //Falls die Datenbank existiert wird onUpgrade aufgerufen
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NotenTabelle.SQL_DELETE);
        onCreate(db);
    }
    //Private Konstruktor --- soll nur von der Methode getInstance aufgerufen werden
    private NotenDB(Context context){
        super(context, DATENBANKNAME, null, DATENBANK_VERSION);
    }

    //Erzeugt falls es noch kein NotenDB Objekt gibt ein neues ansonsten wird das schon erzeugt zurück gegeben
    public static NotenDB getInstance(Context context){
        if(sINSTANCE == null)
            synchronized (sLOCK){
                if (sINSTANCE == null && context != null)
                sINSTANCE = new NotenDB(context.getApplicationContext());
            }
        return sINSTANCE;
    }
}