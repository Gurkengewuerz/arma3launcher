package de.mc8051.arma3launcher.repo;

import co.bitshfted.xapps.zsync.Zsync;
import co.bitshfted.xapps.zsync.ZsyncException;
import co.bitshfted.xapps.zsync.ZsyncObserver;
import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.LauncherGUI;
import de.mc8051.arma3launcher.interfaces.Observable;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.utils.Humanize;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Syncer extends ZsyncObserver implements Observable {

    private List<Observer> observerList = new ArrayList<>();

    private boolean stopped = false;
    private boolean paused = false;
    private boolean running = false;

    private ModFile currentDownload = null;
    private SyncList modlist;

    private boolean currentDownload_failed = false;
    private boolean controlfile_downloaded = false;
    private int failed = 0;
    private int success = 0;

    long syncSize;
    int syncCount;

    long downloadStarted;
    long downloadEnded;
    long downloadSize;
    long downloadDownloaded;

    private Zsync zsync;
    private LauncherGUI gui;

    public Syncer(LauncherGUI gui) {
        zsync = new Zsync();
        this.gui = gui;
    }

    public void sync(SyncList ml) {
        modlist = ml;

        stopped = false;
        paused = false;
        running = true;

        currentDownload = null;

        failed = 0;
        success = 0;

        syncSize = ml.getSize();
        syncCount = ml.getCount();
        SwingUtilities.invokeLater(() -> {
            gui.syncDownloadProgress.setMaximum(syncCount);
            gui.syncDownloadProgress.setValue(0);
        });

        boolean lastPause = false;
        while (running) {
            if (stopped) {
                running = false;
                break;
            }
            if (modlist.isEmpty()) {
                running = false;
                break;
            }

            if (paused) {
                if (!lastPause) {
                    lastPause = true;
                    notifyObservers("syncPaused");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
                continue;
            } else if (lastPause) {
                lastPause = false;
                notifyObservers("syncContinue");
            }

            if (currentDownload != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }

            AbstractMod abstractMod = modlist.get(0);

            ModFile mf = null;
            if (abstractMod instanceof ModFile) {
                mf = (ModFile) abstractMod;
            }

            if (mf != null) {
                Zsync.Options o = new Zsync.Options();
                o.setOutputFile(Paths.get(mf.getLocaleFile().getAbsolutePath()));

                try {
                    currentDownload = mf;
                    currentDownload_failed = false;
                    controlfile_downloaded = false;

                    zsync.zsync(URI.create(mf.getRemoteFile() + ".zsync"), o, this);
                } catch (ZsyncException | IllegalArgumentException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            } else {
                modlist.remove(0);
            }
        }

        deleteFiles();
        cleanUpEmptyFolders();

        if (stopped) {
            notifyObservers("syncStopped");
        } else {
            notifyObservers("syncComplete");
        }
    }

    public void finnishCurrent() {
        modlist.remove(0);
        currentDownload = null;
    }

    public void deleteFiles() {
        modlist.getDeleted().stream()
                .filter((p) -> p.toFile().exists())
                .filter((p) -> p.toFile().canRead())
                .filter((p) -> p.toFile().canWrite())
                .forEach((p) -> p.toFile().delete());
    }

    public void cleanUpEmptyFolders() {
        try {
            String modPath = ArmA3Launcher.user_config.get("client", "modPath");
            if (modPath == null) modPath = "";
            if (modPath.isEmpty()) return;
            Files.find(Paths.get(modPath),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isDirectory())
                    .filter((p) -> p.toFile().canRead())
                    .filter((p) -> p.toFile().canWrite())
                    .filter((p) -> p.toFile().list().length == 0)
                    .forEach((p) -> p.toFile().delete());
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void zsyncStarted(URI requestedZsyncUri, Zsync.Options options) {
        super.zsyncStarted(requestedZsyncUri, options);
        System.out.println("ZSync started " + options.getOutputFile());
    }

    @Override
    public void controlFileDownloadingComplete() {
        super.controlFileDownloadingComplete();
        System.out.println("controlFileDownloadingComplete");
        controlfile_downloaded = true;
    }

    @Override
    public void zsyncFailed(Exception exception) {
        super.zsyncFailed(exception);
        currentDownload_failed = true;
        System.out.println("Zsync failed " + exception.getMessage());
    }

    @Override
    public void zsyncComplete() {
        super.zsyncComplete();

        downloadEnded = System.nanoTime();
        System.out.println(downloadSize);
        System.out.println(downloadEnded - downloadStarted);
        System.out.println((downloadSize / (downloadEnded - downloadStarted)) / 1000);

        System.out.println("Zsync complete");

        if (currentDownload_failed)
            failed++;
        else success++;

        final long finalSize = syncSize - modlist.getSize();
        int i = success + failed;
        int percentage = (int) ((double)i / (double)Long.valueOf(syncCount).intValue() * 100);

        SwingUtilities.invokeLater(() -> {
            gui.syncDownloadProgress.setValue(i);
            gui.syncDownloadedLabel.setText(Humanize.binaryPrefix(finalSize) + " " + " (" + failed + " failed)");
            gui.syncDownloadProgress.setString(percentage + "%");
        });

        finnishCurrent();
    }

    @Override
    public void controlFileDownloadingStarted(URI uri, long length) {
        super.controlFileDownloadingStarted(uri, length);
        System.out.println("controlFileDownloadingStarted " + length);
    }

    @Override
    public void remoteFileDownloadingStarted(URI uri, long length) {
        super.remoteFileDownloadingStarted(uri, length);
        System.out.println("remoteFileDownloadingStarted " + length);

        SwingUtilities.invokeLater(() -> {
            gui.syncFileProgress.setMaximum(Long.valueOf(length).intValue());
            gui.syncFileProgress.setValue(0);
        });

        downloadSize = length;
        downloadDownloaded = 0;
        downloadStarted = System.nanoTime();
    }

    @Override
    public void bytesDownloaded(long bytes) {
        super.bytesDownloaded(bytes);
//        System.out.println("Downloaded " + bytes);
        downloadDownloaded += bytes;

        // TODO: Fix file Download Progress
        if (controlfile_downloaded) {
            SwingUtilities.invokeLater(() -> {
                gui.syncFileProgress.setValue(Long.valueOf(downloadDownloaded).intValue());
            });
        }
    }


    public boolean isStopped() {
        return stopped;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.stopped = true;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getCountFailed() {
        return failed;
    }

    public int getCountSuccess() {
        return success;
    }

    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(String obj) {
        for (Observer obs : observerList) obs.update(obj);
    }
}
