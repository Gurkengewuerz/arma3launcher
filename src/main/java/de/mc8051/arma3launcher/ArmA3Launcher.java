package de.mc8051.arma3launcher;

import com.formdev.flatlaf.FlatDarkLaf;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.mc8051.arma3launcher.repo.RepositoryManger;
import de.mc8051.arma3launcher.steam.SteamTimer;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class ArmA3Launcher {

    public static final String[] SUPPORTED_LANGUAGES = {"en_US", "de_DE"};

    public static String VERSION;
    public static String CLIENT_NAME;
    public static String APPLICATION_PATH;

    public static Config config;
    public static Ini user_config;

    public static void main(String... args) throws Exception {
        config = ConfigFactory.load("arma3launcher");

        CLIENT_NAME = config.getString("name");
        VERSION = config.getString("version");

        APPLICATION_PATH = getAppData() + CLIENT_NAME;

        if (new File(APPLICATION_PATH).mkdirs()) {
            Logger.getLogger(ArmA3Launcher.class.getName()).log(Level.SEVERE, "Can not create " + APPLICATION_PATH);
            System.exit(0);
        }

        File userConfigFile = new File(APPLICATION_PATH + File.separator + "config.ini");
        if(!userConfigFile.exists()) {
            if(!userConfigFile.createNewFile()) {
                Logger.getLogger(ArmA3Launcher.class.getName()).log(Level.SEVERE, "Can not create " + userConfigFile.getAbsolutePath());
                System.exit(0);
            }
        }

        user_config = new Ini(userConfigFile);

        Timer steamTimer = new Timer();

        setLanguage();

        UIManager.setLookAndFeel(new FlatDarkLaf());

        JFrame frame = new JFrame(CLIENT_NAME);
        LauncherGUI gui = new LauncherGUI();
        frame.setContentPane(gui.mainPanel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                steamTimer.cancel();
                steamTimer.purge();
                frame.dispose();
            }
        });

        frame.setMinimumSize(new Dimension(1000, 500));

        frame.pack();
        frame.setLocationRelativeTo(null);

        steamTimer.scheduleAtFixedRate(
                new SteamTimer(gui),
                500,      // run first occurrence immediately
                10000);  // run every thirty seconds

        frame.setVisible(true);
    }

    public static String getAppData() {
        String path = "";
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN"))
            path = System.getenv("APPDATA");
        else if (OS.contains("MAC"))
            path = System.getProperty("user.home") + "/Library/";
        else if (OS.contains("NUX"))
            path = System.getProperty("user.home");
        else path = System.getProperty("user.dir");

        path = path + File.separator;

        return path;
    }

    private static void setLanguage() {
        String lang = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();

        String clientSetting = ArmA3Launcher.user_config.get("client", "language");
        if(clientSetting != null && !clientSetting.equals("system") && Arrays.asList(SUPPORTED_LANGUAGES).contains(clientSetting)) {
            Locale.setDefault(new Locale(clientSetting.split("_")[0], clientSetting.split("_")[1]));
            return;
        }

        if(!Arrays.asList(SUPPORTED_LANGUAGES).contains(lang))
            Locale.setDefault(new Locale("en", "US"));
    }
}
