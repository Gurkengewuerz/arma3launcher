/*
 * This file is part of the arma3launcher distribution.
 * Copyright (c) 2020-2020 Niklas Schütrumpf (Gurkengewuerz)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.mc8051.arma3launcher;

import com.formdev.flatlaf.FlatDarkLaf;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.mc8051.arma3launcher.steam.SteamTimer;
import de.mc8051.arma3launcher.utils.TaskBarUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class ArmA3Launcher {

    private static final Logger logger = LogManager.getLogger(ArmA3Launcher.class);

    public static final String[] SUPPORTED_LANGUAGES = {"en_US", "de_DE"};

    public static String VERSION;
    public static String CLIENT_NAME;
    public static String APPLICATION_PATH;
    public static String USER_AGENT;

    public static Config config;
    public static Ini user_config;

    public static void main(String... args) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);

        config = ConfigFactory.load("arma3launcher");

        CLIENT_NAME = config.getString("name");
        logger.info("Application with client name {} started", CLIENT_NAME);

        final Properties properties = new Properties();
        try {
            properties.load(ArmA3Launcher.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            logger.error(e);
            System.exit(0);
        }
        VERSION = properties.getProperty("version");
        logger.info("Application version v{}", VERSION);

        APPLICATION_PATH = getAppData() + CLIENT_NAME;
        logger.debug("Application path {}", APPLICATION_PATH);

        USER_AGENT = config.getString("sync.useragent") + "/" + VERSION;

        if (new File(APPLICATION_PATH).mkdirs()) {
            logger.error("Can not create " + APPLICATION_PATH);
            System.exit(0);
        }

        File userConfigFile = new File(APPLICATION_PATH + File.separator + "config.ini");
        if (!userConfigFile.exists()) {
            try {
                if (!userConfigFile.createNewFile()) {
                    logger.error("Can not create " + userConfigFile.getAbsolutePath());
                    System.exit(0);
                }
            } catch (IOException e) {
                logger.error(e);
                System.exit(0);
            }
        }

        try {
            user_config = new Ini(userConfigFile);
        } catch (IOException e) {
            logger.error("Couldn't read " + userConfigFile.getAbsolutePath(), e);
            System.exit(0);
        }

        final Parameter debugParameter = Parameters.DEBUG.toParameter();
        if(debugParameter.getValue() != null && (Boolean) debugParameter.getValue())
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.ALL);

        logger.debug("Setup steam timer");
        Timer steamTimer = new Timer();

        setLanguage();

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            logger.error("Failed to set LAF", e);
        }

        logger.debug("Setup frame with client name {}", CLIENT_NAME);
        JFrame frame = new JFrame(CLIENT_NAME);
        TaskBarUtils.getInstance().setWindow(frame);

        LauncherGUI gui = new LauncherGUI();
        frame.setContentPane(gui.mainPanel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("Shutting down application correctly");
                steamTimer.cancel();
                steamTimer.purge();
                TaskBarUtils.getInstance().removeTrayIcon();
                gui.exit();
                frame.dispose();
                logger.info("Shut down");
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
        logger.info("SteamTimer scheduled at fixed rate");

        logger.debug("GUI launched");
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
        logger.debug("Default language {}", lang);

        String clientSetting = ArmA3Launcher.user_config.get("client", "language");
        if (clientSetting != null && !clientSetting.equals("system") && Arrays.asList(SUPPORTED_LANGUAGES).contains(clientSetting)) {
            Locale.setDefault(new Locale(clientSetting.split("_")[0], clientSetting.split("_")[1]));
            logger.info("Using config language {}", lang);
            return;
        }

        if (!Arrays.asList(SUPPORTED_LANGUAGES).contains(lang))
            Locale.setDefault(new Locale("en", "US"));
        logger.info("Using language {}", lang);
    }
}
