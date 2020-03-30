package de.mc8051.arma3launcher.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class URLUtils {

    public static String encodeToURL(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name()).replace("+", "%20").replace("@", "%40");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(URLUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return "";
    }
}
