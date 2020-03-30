package de.mc8051.arma3launcher.repo.sync;

import co.bitshfted.xapps.zsync.Zsync;
import co.bitshfted.xapps.zsync.ZsyncException;
import co.bitshfted.xapps.zsync.http.ContentRange;
import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.LauncherGUI;
import de.mc8051.arma3launcher.interfaces.Observable;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.utils.Humanize;
import de.mc8051.arma3launcher.utils.TaskBarUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Syncer implements Observable, SyncListener {

    private List<Observer> observerList = new ArrayList<>();

    private boolean stopped = false;
    private boolean paused = false;
    private boolean running = false;

    private ModFile currentDownload = null;
    private SyncList modlist;

    private boolean currentDownload_failed = false;
    private int failed = 0;
    private int success = 0;

    private long syncSize;
    private int syncCount;

    private int syncRealCount;

    private SyncObserver syncObserver;

    private long speedCalcSize = 0L;
    private long speedCalcTime = 0L;

    private long downloadDownloaded = 0L;
    private long downloadStarted = 0L;
    private long downloadSize = 0L;

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

        speedCalcSize = 0;
        speedCalcTime = 0;

        syncRealCount = 0;

        syncSize = ml.getSize();
        syncCount = ml.getCount();
        SwingUtilities.invokeLater(() -> {
            gui.syncDownloadProgress.setMaximum(syncCount);
            gui.syncDownloadProgress.setValue(0);
            TaskBarUtils.getInstance().normal();
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
                o.setUseragent(ArmA3Launcher.USER_AGENT);

                try {
                    currentDownload = mf;
                    currentDownload_failed = false;

                    syncObserver = new SyncObserver(this);
                    zsync.zsync(URI.create(mf.getRemoteFile() + ".zsync"), o, syncObserver);
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
    public void zsyncStarted(Zsync.Options options) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "ZSync started " + options.getOutputFile());
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Sync started"));
    }

    @Override
    public void zsyncFailed(Exception exception) {
        currentDownload_failed = true;
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Zsync failed " + exception.getMessage());
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Sync failed"));
    }

    @Override
    public void zsyncComplete() {
        speedCalcSize+=downloadDownloaded;
        speedCalcTime+=System.currentTimeMillis()-downloadStarted;

        if (speedCalcSize > 20 * 1024 * 1024) {

            final double speedByte = ((double)speedCalcSize)/((double)speedCalcTime /1000);
            SwingUtilities.invokeLater(() -> gui.syncDownloadSpeedLabel.setText(Humanize.binaryPrefix(Double.valueOf(speedByte).longValue()) + "/s"));
            speedCalcSize = 0L;
            speedCalcTime = 0L;
        }

        Logger.getLogger(getClass().getName()).log(Level.INFO, "Zsync complete");

        if (currentDownload_failed)
            failed++;
        else success++;

        syncRealCount+=downloadDownloaded;

        final long finalSize = syncSize - ((syncSize - modlist.getSize() + currentDownload.getSize()) - syncRealCount);
        final int i = success + failed;
        final int percentage = (int) ((double) i / (double) Long.valueOf(syncCount).intValue() * 100);
        final String modPath = currentDownload.getModPath();

        SwingUtilities.invokeLater(() -> {
            gui.syncDownloadProgress.setValue(i);
            gui.syncFileCountLabel.setText(i + "/" + syncCount + " (" + failed + " failed)");
            gui.syncSizeLabel.setText(Humanize.binaryPrefix(syncRealCount) + "/" + Humanize.binaryPrefix(finalSize));

            if (currentDownload_failed)
                gui.syncStatusLabel.setText(modPath + ": Sync failed");
            else
                gui.syncStatusLabel.setText(modPath + ": Sync finished");

            gui.syncDownloadProgress.setString(percentage + " %");
            TaskBarUtils.getInstance().setValue(percentage);
        });

        finnishCurrent();
    }

    @Override
    public void controlFileDownloadingStarted(Path path, long length) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "controlFileDownloadingStarted " + length);
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Get Header"));
    }

    @Override
    public void controlFileReadingComplete() {

    }

    @Override
    public void outputFileWritingStarted(long length) {
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Writing File"));
    }

    @Override
    public void outputFileWritingCompleted() {

    }

    @Override
    public void inputFileReadingStarted(Path inputFile, long length) {
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Reading File"));
    }

    @Override
    public void inputFileReadingComplete() {

    }

    @Override
    public void controlFileDownloadingComplete() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "controlFileDownloadingComplete");
    }

    @Override
    public void remoteFileDownloadingInitiated(List<ContentRange> ranges) {
        downloadStarted = System.currentTimeMillis();
        Logger.getLogger(getClass().getName()).log(Level.INFO, "remoteFileDownloadingInitiated");
        SwingUtilities.invokeLater(() -> gui.syncStatusLabel.setText(currentDownload.getModPath() + ": Downloading"));
    }

    @Override
    public void remoteFileDownloadingStarted(long length) {
        downloadDownloaded = 0;
        downloadSize = length;

        Logger.getLogger(getClass().getName()).log(Level.INFO, "remoteFileDownloadingStarted " + length);
    }

    @Override
    public void remoteFileDownloadingComplete() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "remoteFileDownloadingStarted");
    }

    @Override
    public void bytesDownloaded(long bytes) {
        downloadDownloaded += bytes;
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
