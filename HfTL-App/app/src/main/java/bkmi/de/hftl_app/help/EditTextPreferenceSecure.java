package android.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;

import bkmi.de.hftl_app.help.TextSecure;

import static bkmi.de.hftl_app.help.TextSecure.*;

/**
 * Created by Georg on 01.05.15.
 * Ein EditText für das Einstellungmenü, dass die Daten verschlüsselt auf dem Handy ablegt
 */
public class EditTextPreferenceSecure extends EditTextPreference {

    Context context;
    TextSecure textSecure;

    public EditTextPreferenceSecure(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        try {
            textSecure = new TextSecure(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EditTextPreferenceSecure(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        try {
            textSecure = new TextSecure(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EditTextPreferenceSecure(Context context) {
        super(context);
        this.context = context;
        try {
            textSecure = new TextSecure(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        try {
            super.getEditText().setText(textSecure.encrypt(getEditText().getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        try {
            this.getEditText().setText(textSecure.decrypt(getEditText().getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
