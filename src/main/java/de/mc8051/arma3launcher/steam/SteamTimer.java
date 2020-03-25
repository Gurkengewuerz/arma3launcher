package de.mc8051.arma3launcher.steam;

import de.mc8051.arma3launcher.LauncherGUI;
import de.mc8051.arma3launcher.SteamUtils;
import de.ralleytn.simple.registry.Key;
import de.ralleytn.simple.registry.Registry;

import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class SteamTimer extends TimerTask {

    public static boolean steam_running = false;
    public static boolean arma_running = false;

    private LauncherGUI gui;

    public SteamTimer(LauncherGUI gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (!OS.contains("WIN")) return;

        try {
            if(!SteamUtils.findProcess("steam.exe")) {
                steam_running = false;
                gui.updateLabels(steam_running, arma_running);
                return;
            }

            Key activeSteamUserKey = Registry.getKey(Registry.HKEY_CURRENT_USER + "\\Software\\Valve\\Steam\\ActiveProcess");

            String activeSteamUser = activeSteamUserKey.getValueByName("ActiveUser").getRawValue();

            if(activeSteamUser.equals("0x0")) {
                steam_running = false;
                gui.updateLabels(steam_running, arma_running);
                return;
            }

            steam_running = true;

            arma_running = SteamUtils.findProcess("arma3.exe") || SteamUtils.findProcess("arma3_x64.exe") || SteamUtils.findProcess("arma3launcher.exe");
        } catch (IOException e) {
            steam_running = false;
            arma_running = false;
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

        gui.updateLabels(steam_running, arma_running);
    }
}
