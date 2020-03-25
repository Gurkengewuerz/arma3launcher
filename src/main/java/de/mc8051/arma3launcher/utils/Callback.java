package de.mc8051.arma3launcher.utils;

import okhttp3.Response;

import java.io.File;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class Callback {

    public static interface JFileSelectCallback {                   //declare an interface with the callback methods, so you can use on more than one class and just refer to the interface
        boolean allowSelection(File path);
    }

    public static interface HttpCallback {
        void response(Response r);
    }
}
