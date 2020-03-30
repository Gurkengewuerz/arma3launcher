package de.mc8051.arma3launcher.utils;

import de.ralleytn.simple.registry.Key;
import de.ralleytn.simple.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 30.03.2020.
 */
public class ArmaUtils {

    public static Path getInstallationPath() {
        Key regKey = null;
        try {
            regKey = Registry.getKey(Registry.HKEY_LOCAL_MASHINE + "\\SOFTWARE\\bohemia interactive\\arma 3");
        } catch (IOException ignored) {
            try {
                regKey = Registry.getKey("HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\bohemia interactive\\arma 3");
            } catch (IOException e) {
                Logger.getLogger(ArmaUtils.class.getName()).log(Level.INFO, "Arma patch cant be detected automatically");
            }
        }

        if (regKey == null) return null;
        final Path main = Paths.get(regKey.getValueByName("main").getRawValue());
        if(!checkArmaPath(main)) return null;
        return main;
    }

    public static boolean checkArmaPath(Path path) {
        ArrayList<String> search = new ArrayList<>(Arrays.asList("arma3.exe", "steam.dll"));
        final File f = path.toFile();
        if (!f.exists() || !f.isDirectory()) return false;
        if (f.listFiles() == null) return false;
        File[] listOfFiles = f.listFiles();

        try {
            for (File file : listOfFiles) {
                if (search.isEmpty()) return true;
                if (file.isFile()) {
                    search.remove(file.getName().toLowerCase());
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }
}
