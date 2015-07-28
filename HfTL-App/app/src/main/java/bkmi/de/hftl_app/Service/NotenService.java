package bkmi.de.hftl_app.Service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.net.UnknownHostException;

import bkmi.de.hftl_app.Database.NotenDB;
import bkmi.de.hftl_app.Database.NotenTabelle;
import bkmi.de.hftl_app.EinstellungActivity;
import bkmi.de.hftl_app.Fragmente.NotenFragment;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;
import bkmi.de.hftl_app.help.NotenResolver;
import bkmi.de.hftl_app.help.wrongUserdataException;


public class NotenService extends Service {

    ThreadTest thread;

    @Override
    public IBinder onBind(Intent intent) {
        //wird zur direkten Kommunikation mit dem Hauptprogramm benötigt, für die App nicht notwendig
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        thread = new ThreadTest(this);
        thread.start();
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class ThreadTest extends Thread {

    Context context;
    NotenResolver nr;
    NotenDB notenDB;
    Cursor cursor;

    public ThreadTest(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        try {
            int i, j;
            //Erzeuge einen NotenResolver um Noten abzufragen
            nr = new NotenResolver(context);

            //DB wird geöffnet um die Anzahl der vorhanden Noten abzufragen
            notenDB = NotenDB.getInstance(context);
            cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.NOTENABFRAGE, null, null, null, null, null);
            i = cursor.getCount();

            //Datenbank schließen -- notwendig??
            cursor.close();
            notenDB.close();

            //Noten abrufen
            nr.getNoten();

            //Anzahl der Noten abfragen
            cursor = notenDB.getReadableDatabase().query(NotenTabelle.TABLE_NAME, NotenTabelle.NOTENABFRAGE, null, null, null, null, null);
            j = cursor.getCount();

            //vorher nachher vergleich
            if (i < j) {
                //Erzeugen einer Notifikation die auf das Notenfragment zeigt
                //Todo start von Notenfragment
                Intent internalIntent = new Intent(context, NewsActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, internalIntent, 0);
                //Todo Layout: Bitte das Icon ändern
                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context).setContentTitle("HfTL-App Noten Service").setSmallIcon(R.drawable.ic_drawer).setContentText("Es sind neue Noten verfügbar").setContentIntent(pendingIntent).setAutoCancel(true);
                int nNotificationId = 1;
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(nNotificationId, nBuilder.build());
            }


            //Datenbank schließen
            cursor.close();
            notenDB.close();

        //Falls keine Internetverbindung vorhanden ist wird ein Fehler angezeigt
        } catch (UnknownHostException e) {
            Intent internalIntent = new Intent(context, NotenFragment.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, internalIntent, 0);
            //Todo Layout: Bitte das Icon ändern
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context).setContentTitle("HfTL-App Noten Service").setSmallIcon(R.drawable.ic_drawer).setContentText("Fehler beim Laden der Noten").setContentIntent(pendingIntent).setAutoCancel(true);
            //final Notification notification = new NotificationCompat.Builder(context).setContentTitle("HfTL-App Noten Service, es sind neue Noten verfügbar").setSmallIcon(R.drawable.hftl_l_3c_n).setContentIntent(pendingIntent).build();
            int nNotificationId = 1;
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(nNotificationId, nBuilder.build());

        //Falls die benutzerdaten falsch sind wird der Push-Service beendet und ein Fehler angezeigt
        } catch (wrongUserdataException e) {
            //Der laufende Alarm wird deaktivert
            AlarmManager alarmMgr;
            Intent intent = new Intent(context, NotenService.class);
            PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(pIntent);

            //Die Checkbox in den Einstellungen wird auch deaktiviert
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("service_checkbox", false).apply();

            //Falls Benutzername/Passwort falsch wird eine Notifcation gesenden
            Intent internalIntent = new Intent(context, EinstellungActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, internalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Todo Layout: Bitte das Icon ändern
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context).setContentTitle("HfTL-App Noten Service").setSmallIcon(R.drawable.ic_drawer).setStyle(new NotificationCompat.BigTextStyle().bigText("Password/Benutzername falsch\nService beendet")).setContentIntent(pendingIntent).setAutoCancel(true);
            int nNotificationId = 1;
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(nNotificationId, nBuilder.build());
        }

    }
}
