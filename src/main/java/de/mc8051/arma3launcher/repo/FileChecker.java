package de.mc8051.arma3launcher.repo;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.interfaces.Observable;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gurkengewuerz.de on 26.03.2020.
 */
public class FileChecker implements Observable {

    private List<Observer> observerList = new ArrayList<>();
    private JProgressBar pb;
    private boolean stop = false;

    private boolean checked = false;

    private ArrayList<Path> deleted = new ArrayList<>();
    private HashMap<String, ArrayList<ModFile>> changed = new HashMap<>();
    int changedCount = 0;
    private HashMap<String, ArrayList<ModFile>> added = new HashMap<>();
    int addedCount = 0;

    long size = 0;

    public FileChecker(JProgressBar pb) {
        this.pb = pb;
    }

    public void check() {
        check(false);
    }

    public void check(boolean fastscan) {
        deleted.clear();
        changed.clear();
        changedCount = 0;
        added.clear();
        addedCount = 0;
        size = 0;

        int i = 0;
        SwingUtilities.invokeLater(() -> {
            pb.setMaximum(RepositoryManger.MOD_LIST_SIZE);
            pb.setValue(0);
        });

        for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
            if (stop) {
                stop = false;
                notifyObservers("fileCheckerStopped");
                return;
            }
            if (abstractMod instanceof Mod) {
                Mod m = (Mod) abstractMod;

                for (ModFile mf : m.getFiles()) {
                    checkFile(m.getName(), mf, fastscan);

                    i++;
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> {
                        pb.setValue(finalI);
                    });

                    if (stop) {
                        stop = false;
                        notifyObservers("fileCheckerStopped");
                        return;
                    }
                }
            } else if (abstractMod instanceof ModFile) {
                ModFile mf = (ModFile) abstractMod;
                checkFile(mf.getName(), mf, fastscan);
                i++;
                int finalI1 = i;
                SwingUtilities.invokeLater(() -> {
                    pb.setValue(finalI1);
                });
            }
        }

        checkDeleted();
        notifyObservers("fileChecker");
        checked = true;
    }

    public boolean isChecked() {
        return checked;
    }

    public void stop() {
        stop = true;
    }

    private void checkFile(String mod, ModFile mf, boolean fastscan) {
        ArrayList<ModFile> temp = new ArrayList<>();

        if (!mf.exists()) {
            if (added.containsKey(mod)) temp = added.get(mod);
            temp.add(mf);
            added.put(mod, temp);
            addedCount++;
            size += mf.getSize();
            return;
        }


        if (fastscan || !mf.getSHA1Sum().equalsIgnoreCase(mf.getLocalGeneratedSHA1Sum())) {
            if (mf.getLocalSize() != mf.getSize()) {
                if (changed.containsKey(mod)) temp = changed.get(mod);
                temp.add(mf);
                changed.put(mod, temp);
                changedCount++;
                size += mf.getSize();
                return;
            }
        }
    }

    private void checkDeleted() {
        String modPath = ArmA3Launcher.user_config.get("client", "modPath");
        if (modPath == null) modPath = "";

        try {
            List<Path> filePathList = Files.find(Paths.get(modPath),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .collect(Collectors.toList());


            for (Path localPath : filePathList) {
                ModFile deleteable = null;

                outerloop:
                for (AbstractMod abstractMod : RepositoryManger.MOD_LIST) {
                    if (abstractMod instanceof Mod) {
                        Mod m = (Mod) abstractMod;

                        for (ModFile mf : m.getFiles()) {
                            if (mf.getLocaleFile().getPath().equals(localPath.toString())) {
                                deleteable = mf;
                                break outerloop;
                            }
                        }
                    } else if (abstractMod instanceof ModFile) {
                        ModFile mf = (ModFile) abstractMod;
                        if (mf.getLocaleFile().getPath().equals(localPath.toString())) {
                            deleteable = mf;
                            break outerloop;
                        }
                    }
                }

                if (deleteable == null) {
                    deleted.add(localPath);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public ArrayList<Path> getDeleted() {
        return deleted;
    }

    public HashMap<String, ArrayList<ModFile>> getChanged() {
        return changed;
    }

    public HashMap<String, ArrayList<ModFile>> getAdded() {
        return added;
    }

    public int getDeletedCount() {
        return deleted.size();
    }

    public int getChangedCount() {
        return changedCount;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public long getSize() {
        return size;
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
