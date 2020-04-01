package de.mc8051.arma3launcher.objects;

import de.mc8051.arma3launcher.repo.RepositoryManger;

/**
 * Created by gurkengewuerz.de on 27.03.2020.
 */
public class Changelog {

    private static long lastUpdate = 0;
    private static String cache = "";


    public static void refresh() {
        if(cache.isEmpty() || System.currentTimeMillis() - lastUpdate > 5 * 60 * 1000) { // 5 Minuten
            RepositoryManger.getInstance().refreshChangelog();
            lastUpdate = System.currentTimeMillis();
        }
    }

    public static String get() {
        return cache;
    }

    public static void setChangelog(String changelog) {
        cache = changelog;
    }
}
