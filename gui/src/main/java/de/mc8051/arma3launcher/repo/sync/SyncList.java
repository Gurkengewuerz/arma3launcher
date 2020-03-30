package de.mc8051.arma3launcher.repo.sync;

import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by gurkengewuerz.de on 26.03.2020.
 */
public class SyncList extends ArrayList<AbstractMod> {

    private long size = 0;
    private int count = 0;

    private ArrayList<Path> deleted = new ArrayList<>();

    private SyncList(long size, int count) {
        this.size = size;
        this.count = count;
    }

    public SyncList() {

    }

    public void setDeleted(ArrayList<Path> deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean add(AbstractMod abstractMod) {
        addedMod(abstractMod);
        return super.add(abstractMod);
    }

    @Override
    public boolean addAll(Collection<? extends AbstractMod> c) {
        for (AbstractMod abstractMod : c) {
            addedMod(abstractMod);
        }
        return super.addAll(c);
    }

    private void addedMod(AbstractMod abstractMod) {
        if (abstractMod instanceof Mod) {
            Mod mod = (Mod) abstractMod;
            for (ModFile mf : mod.getFiles()) {
                size += mf.getSize();
                count++;
            }
        } else if (abstractMod instanceof ModFile) {
            ModFile mf = (ModFile) abstractMod;
            size += mf.getSize();
            count++;
        }
    }

    @Override
    public AbstractMod remove(int index) {
        AbstractMod abstractMod = get(index);

        if (abstractMod instanceof Mod) {
            Mod mod = (Mod) abstractMod;
            for (ModFile mf : mod.getFiles()) {
                size -= mf.getSize();
            }
        } else if (abstractMod instanceof ModFile) {
            ModFile mf = (ModFile) abstractMod;
            size -= mf.getSize();
        }

        return super.remove(index);
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public SyncList clone() {
        SyncList clone = new SyncList();
        clone.addAll(this);
        clone.getDeleted().addAll(deleted);
        clone.setSize(size);
        clone.setDeleted(deleted);
        return clone;
    }

    public ArrayList<Path> getDeleted() {
        return deleted;
    }

    public long getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }
}
