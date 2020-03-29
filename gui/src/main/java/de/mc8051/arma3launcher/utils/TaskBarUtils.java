package de.mc8051.arma3launcher.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 27.03.2020.
 */
public class TaskBarUtils {

    public static BufferedImage IMAGE_ICON = createIcon();
    public static BufferedImage IMAGE_LGO = createLogo();

    private static TaskBarUtils instance;

    private final boolean isTaskbarSupported;
    private final boolean isSystemtraySupported;
    private Taskbar taskbar;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Window w;

    private TaskBarUtils() {
        isTaskbarSupported = Taskbar.isTaskbarSupported();
        if (isTaskbarSupported) {
            taskbar = Taskbar.getTaskbar();
        }

        isSystemtraySupported = SystemTray.isSupported();
        if (isSystemtraySupported) {
            tray = SystemTray.getSystemTray();

            try {
                trayIcon = new TrayIcon(IMAGE_ICON);
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                trayIcon.addActionListener(e -> {
                    if (w == null) return;
                    if(!(w instanceof JFrame)) return;
                    SwingUtilities.invokeLater(() -> {
                        JFrame frame = (JFrame) w;
                        if(frame.getState()!=Frame.NORMAL) { frame.setState(Frame.NORMAL); }
                        frame.setVisible(true);
                        frame.setAlwaysOnTop(true);
                        frame.toFront();
                        frame.requestFocus();
                        frame.setAlwaysOnTop(false);
                        frame.repaint();
                    });
                });
            } catch (AWTException e) {
                Logger.getLogger(TaskBarUtils.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static TaskBarUtils getInstance() {
        if (instance == null) instance = new TaskBarUtils();
        return instance;
    }

    public void error(Window w) {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) return;
        taskbar.setWindowProgressState(w, Taskbar.State.ERROR);
    }

    public void normal() {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) return;
        taskbar.setWindowProgressState(w, Taskbar.State.NORMAL);
    }

    public void off() {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) return;
        taskbar.setWindowProgressState(w, Taskbar.State.OFF);
    }

    public void paused() {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) return;
        taskbar.setWindowProgressState(w, Taskbar.State.PAUSED);
    }

    public void setValue(int val) {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.PROGRESS_VALUE_WINDOW)) return;
        taskbar.setWindowProgressValue(w, val);
    }

    public void attention() {
        if (w == null) return;
        if (!isTaskbarSupported) return;
        if (!taskbar.isSupported(Taskbar.Feature.USER_ATTENTION_WINDOW)) return;
        taskbar.requestWindowUserAttention(w);
    }

    public void notification(String caption, String text, TrayIcon.MessageType type) {
        if (!isSystemtraySupported) return;
        if (trayIcon == null) return;

        trayIcon.displayMessage(caption, text, type);
    }

    public void setWindow(Window w) {
        this.w = w;
    }

    public void removeTrayIcon() {
        if (!isSystemtraySupported) return;
        if (trayIcon == null) return;
        tray.remove(trayIcon);
    }

    static BufferedImage createIcon() {
        try {
            return ImageIO.read(TaskBarUtils.class.getResourceAsStream("/icons/logo_32.png"));
        } catch (IOException e) {
            Logger.getLogger(TaskBarUtils.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    static BufferedImage createLogo() {
        try {
            return ImageIO.read(TaskBarUtils.class.getResourceAsStream("/icons/logo_256.png"));
        } catch (IOException e) {
            Logger.getLogger(TaskBarUtils.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
}
