/*
 * MIT License
 *
 * Copyright (c) 2020-2020 Niklas Schütrumpf (Gurkengewuerz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.mc8051.arma3launcher;

import com.formdev.flatlaf.FlatDarkLaf;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.mc8051.arma3launcher.steam.SteamTimer;
import de.mc8051.arma3launcher.utils.TaskBarUtils;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
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
    public static String USER_AGENT;

    public static Config config;
    public static Ini user_config;

    public static void main(String... args) throws Exception {
        config = ConfigFactory.load("arma3launcher");

        CLIENT_NAME = config.getString("name");

        final Properties properties = new Properties();
        properties.load(ArmA3Launcher.class.getClassLoader().getResourceAsStream("project.properties"));
        VERSION = properties.getProperty("version");

        APPLICATION_PATH = getAppData() + CLIENT_NAME;

        USER_AGENT = config.getString("sync.useragent") + "/" + VERSION;

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
        TaskBarUtils.getInstance().setWindow(frame);

        LauncherGUI gui = new LauncherGUI();
        frame.setContentPane(gui.mainPanel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                steamTimer.cancel();
                steamTimer.purge();
                TaskBarUtils.getInstance().removeTrayIcon();
                gui.exit();
                frame.dispose();
            }
        });

        frame.setMinimumSize(new Dimension(1000, 550));

        frame.pack();
        frame.setIconImage(TaskBarUtils.IMAGE_ICON);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        steamTimer.scheduleAtFixedRate(
                new SteamTimer(),
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
