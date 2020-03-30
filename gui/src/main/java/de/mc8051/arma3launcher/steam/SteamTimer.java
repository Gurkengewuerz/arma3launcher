package de.mc8051.arma3launcher.steam;

import de.mc8051.arma3launcher.utils.SteamUtils;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.ralleytn.simple.registry.Key;
import de.ralleytn.simple.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class SteamTimer extends TimerTask {

    private static ArrayList<Observer> observers = new ArrayList<>();
    public static boolean steam_running = false;
    public static boolean arma_running = false;

    @Override
    public void run() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (!OS.contains("WIN")) return;

        boolean old_steamrunning = steam_running;
        boolean old_arma_running = arma_running;

        try {
            if(!SteamUtils.findProcess("steam.exe")) {
                steam_running = false;
                if(old_steamrunning != steam_running) notifyObservers("steamtimer");
                return;
            }

            Key activeSteamUserKey = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software\\Valve\\Steam\\ActiveProcess");

            String activeSteamUser = activeSteamUserKey.getValueByName("ActiveUser").getRawValue();

            if(activeSteamUser.equals("0x0")) {
                steam_running = false;
                if(old_steamrunning != steam_running) notifyObservers("steamtimer");
                return;
            }

            steam_running = true;

            arma_running = SteamUtils.findProcess("arma3.exe")
                    || SteamUtils.findProcess("arma3_x64.exe")
                    || SteamUtils.findProcess("arma3battleye.exe")
                    || SteamUtils.findProcess("arma3launcher.exe");

            if(old_steamrunning != steam_running || old_arma_running != arma_running) notifyObservers("steamtimer");
        } catch (IOException e) {
            steam_running = false;
            arma_running = false;
            notifyObservers("steamtimer");
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String obj) {
        for(Observer o : observers) o.update(obj);
    }
}
