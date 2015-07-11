package bkmi.de.hftl_app.Database;

/**
 * Funktionen/Namen der Noten-Datenbank
 */
public final class NotenTabelle {

    public static final String TABLE_NAME = "notendatenbank"; // Name der Tabelle

    //Namen der einzelnen Spalten
    public static final String FACH = "fach";
    public static final String NOTE = "note";
    public static final String SEMESTER = "semester";
    public static final String LINK = "link";
    public static final String VERSUCH = "versuch";

    //Datenbank erzeugen
    public static final String SQL_CREATE =
            "CREATE TABLE notendatenbank (" +
                "fach TEXT NOT NULL, " +
                "note TEXT NOT NULL, " +
                "semester TEXT NOT NULL, " +
                "link TEXT NOT NULL, " +
                "versuch TEXT NOT NULL, " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT " +
                ");";

    //Datenbank löschen
    public static final String SQL_DELETE =
            "DROP TABLE IF EXIST " + TABLE_NAME;

    //Werte einfügen
    public static final String SQL_INSERT_Noten =
            "INSERT INTO notendatenbank " +
                    "(fach, semester, note, link, versuch) " +
                    "VALUES (?,?,?,?,?)";

    public static final String[] NOTENABFRAGE1 =
    {FACH, NOTE, SEMESTER, LINK, VERSUCH};

    public static final String[] NOTENABFRAGE =
            {NOTE};

    public static final String[] FACHABFRAGE =
            {FACH};

    public static final String[] SEMESTERABFRAGE =
            {SEMESTER};

    public static final String[] VERSUCHABFRAGE =
            {VERSUCH};
}
