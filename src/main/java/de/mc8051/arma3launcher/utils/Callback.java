package de.mc8051.arma3launcher.utils;

import de.mc8051.arma3launcher.repo.Response;

import java.io.File;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class Callback {

    public interface JFileSelectCallback {
        boolean allowSelection(File path);
    }

    public interface HttpCallback {
        void response(Response r);
    }

    public interface ChangelogCallback {
        void response(String changelog);
    }
}
