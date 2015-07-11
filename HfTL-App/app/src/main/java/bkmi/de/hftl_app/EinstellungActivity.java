package bkmi.de.hftl_app;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Calendar;

import bkmi.de.hftl_app.Service.NotenService;
import bkmi.de.hftl_app.help.TextSecure;

public class EinstellungActivity extends PreferenceActivity {

    //public static final String CONTEXTDATA= "ContextData";

    SharedPreferences shared;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    CheckBoxPreference check;
    Preference list;
    AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.einstellung);

        shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        check = (CheckBoxPreference) findPreference("service_checkbox");
        list = (Preference) findPreference("service_intervall");

        //Falls der Service noch nicht läuft wird das Auswahlmenü deaktiviert
        if (!shared.getBoolean("service_checkbox", false))
            list.setEnabled(false);

        registerPreferenceListener();
    }

    private void registerPreferenceListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                //Wenn der Wert in der Service Checkbox true ist mache die Auswahlliste Sichtbar
                if (key.equals("service_checkbox")) {
                    if (shared.getBoolean("service_checkbox", false)) {
                        if (!testeBenutzerdaten()) {
                            keineBenutzerdaten();
                            check.setChecked(false);
                            return;
                        }
                        list.setEnabled(true);
                        long time = Long.parseLong(shared.getString("service_intervall", "3600000"));
                        Log.d("test", "Zeit: "+ time);

                        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), NotenService.class);
                        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

                        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), time, pIntent);

                        //getApplicationContext().startService(intent);

                    } else {
                        list.setEnabled(false);
                        Intent intent = new Intent(getApplicationContext(), NotenService.class);
                        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        alarmMgr.cancel(pIntent);

                    }
                }
                if(key.equals("service_intervall")){
                    Intent intent = new Intent(getApplicationContext(), NotenService.class);
                    PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                    alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmMgr.cancel(pIntent);
                    alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    long time = Long.parseLong(shared.getString("service_intervall", "3600000"));
                    alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), time, pIntent);
                }
            }

        };

        shared.registerOnSharedPreferenceChangeListener(listener);
    }

    private boolean testeBenutzerdaten() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        TextSecure ts;
        try {
            ts = new TextSecure(this);
            password = ts.decrypt(password);
            username = ts.decrypt(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !("".equals(username) || "".equals(password));
    }

    private void keineBenutzerdaten() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Benutzername/Passwort nicht eingetragen!").setCancelable(false).setPositiveButton(
                this.getResources().getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        builder.show();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

