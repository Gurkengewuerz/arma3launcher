package de.mc8051.arma3launcher.objects;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class ModFile implements AbstractMod {

    private File f;
    private long size;
    private String folder;
    private String filename;
    private String extension;
    private String modfileString;

    public ModFile(File f, String modfile, long size) {
        // File: Abosolut Path
        // modfile: addons/config/something.pbo
        // size: size as in metafile on server
        this.f = f;
        this.size = size;
        this.folder = FilenameUtils.getPath(modfile);
        this.filename = FilenameUtils.getBaseName(modfile);
        this.extension = FilenameUtils.getExtension(modfile);
        this.modfileString = modfile;
    }

    public long getSize() {
        return size;
    }

    public String getReletaivePath() {
        return folder;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public ArrayList<String> getPath() {
        ArrayList<String> list = new ArrayList<>();
        File relativePath = new File("./"+ modfileString);
        do {
            list.add(relativePath.getName());
            relativePath = relativePath.getParentFile();
        } while (relativePath.getParentFile() != null);
        list.remove(0);
        Collections.reverse(list);
        return list;
    }

    public long getLocalSize() {
        if(!f.exists() || !f.isFile()) return -1;
        return f.length();
    }

    public boolean exists() {
        if(!f.exists() || !f.isFile()) return false;
        return true;
    }

    public String getName() {
        return filename + (extension.equals("") ? "" : "." + extension);
    }

    public String getModfileString() {
        return modfileString;
    }

    public File getLocaleFile() {
        return f;
    }
}
