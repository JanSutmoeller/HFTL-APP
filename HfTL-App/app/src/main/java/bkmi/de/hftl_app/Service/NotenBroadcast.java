package bkmi.de.hftl_app.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Die Klasse überprüft beim Start des Gerätes ob der Notenservice aktiviert werden muss und startet ihn ggf
 */
public class NotenBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean aktiv = sp.getBoolean("service_checkbox", false);
            Log.d("aktiv", " " + aktiv);
            if (aktiv){
                long time = Long.parseLong(sp.getString("service_intervall", "3600000"));
                Log.d("zeit", " " + time);
                AlarmManager alarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent startIntent = new Intent(context.getApplicationContext(), NotenService.class);
                PendingIntent pIntent = PendingIntent.getService(context.getApplicationContext(), 0, startIntent, 0);

                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), time, pIntent);
            }
        }
    }
}
