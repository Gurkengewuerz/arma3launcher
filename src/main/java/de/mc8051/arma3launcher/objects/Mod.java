package de.mc8051.arma3launcher.objects;

import java.util.ArrayList;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Mod implements AbstractMod {

    private String name;
    private long size;
    private ArrayList<ModFile> files;

    public Mod(String name) {
        this(name, -1, new ArrayList<>());
    }

    public Mod(String name, long size, ArrayList<ModFile> files) {
        this.name = name;
        this.size = size;
        this.files = files;
    }

    public ArrayList<ModFile> getFiles() {
        return files;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }


    public Mod clone() {
        return new Mod(name, size, new ArrayList<>(files));
    }
}
