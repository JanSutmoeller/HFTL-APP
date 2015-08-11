package bkmi.de.hftl_app.help.Typefaces;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Stefan on 11.08.2015.
 */
public class Ocra extends TextView {

    public Ocra(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public Ocra(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public Ocra(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/OCRA.TTF", context);
        setTypeface(customFont);
    }
}