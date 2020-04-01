package de.mc8051.arma3launcher.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class URLUtils {

    private static final Logger logger = LogManager.getLogger(URLUtils.class);

    public static String encodeToURL(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name()).replace("+", "%20").replace("@", "%40");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return "";
    }
}
