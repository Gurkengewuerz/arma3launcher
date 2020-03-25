package de.mc8051.arma3launcher.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class LangUtils {

    private static LangUtils i;

    private ResourceBundle r = ResourceBundle.getBundle("lang", Locale.getDefault());

    private LangUtils() {
    }

    public String getString(String key) {
        return r.getString(key);
    }

    public static LangUtils getInstance() {
        if(i == null) i = new LangUtils();
        return i;
    }
}
