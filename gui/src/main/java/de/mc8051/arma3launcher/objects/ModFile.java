package de.mc8051.arma3launcher.objects;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.utils.FileUtils;
import de.mc8051.arma3launcher.utils.URLUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class ModFile implements AbstractMod {

    private static final Logger logger = LogManager.getLogger(ModFile.class);

    private File f;
    private long size;
    private String folder;
    private String filename;
    private String modfileString;
    private String sha1sum;
    private String parent;
    private String localGeneratedSHA1sum = "";
    private long lastModified = -1;

    public ModFile(File f, String modfile, String parent, long size, String sha1sum, long lastModified) {
        // File: Abosolut Path
        // modfile: addons/config/something.pbo
        // size: size as in metafile on server
        this.f = f;
        this.size = size;
        this.filename = Paths.get(f.getPath()).getFileName().toString();
        this.modfileString = modfile;
        this.sha1sum = sha1sum.toLowerCase();
        this.parent = parent;
        this.lastModified = lastModified;
    }

    public ModFile(File f, String modfile, long size, String sha1sum, long lastModified) {
        this(f, modfile, null, size, sha1sum, lastModified);
    }

    public long getSize() {
        return size;
    }

    public ArrayList<String> getPath() {
        ArrayList<String> list = new ArrayList<>();
        File relativePath = new File("./" + modfileString);

        do {
            list.add(relativePath.getName());
            relativePath = relativePath.getParentFile();
        } while (relativePath.getParentFile() != null);
        Collections.reverse(list);
        return list;
    }

    public long getLocalSize() {
        if (!f.exists() || !f.isFile()) return -1;
        return f.length();
    }

    public boolean exists() {
        if (!f.exists() || !f.isFile()) return false;
        return true;
    }

    public String getName() {
        return filename;
    }

    public String getModfileString() {
        return modfileString;
    }

    public String getModPath() {
        return (parent == null ? "" : parent + "/") + modfileString;
    }

    public File getLocaleFile() {
        return f;
    }

    public String getSHA1Sum() {
        return sha1sum;
    }

    public long getLocalLastModified() {
        return (int) (f.lastModified() / 1000);
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getLocalGeneratedSHA1Sum() {
        try {
            if (localGeneratedSHA1sum.isEmpty() && exists()) {
                localGeneratedSHA1sum = FileUtils.sha1Hex(f);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error(e);
        }
        return localGeneratedSHA1sum;
    }

    public String getRemoteFile() {
        String s = ArmA3Launcher.config.getString("sync.url");
        if (parent == null || parent.isEmpty()) {
            return s + "/" + URLUtils.encodeToURL(getName());
        }

        s += "/" + URLUtils.encodeToURL(parent);
        for (String seg : getPath()) {
            s += "/" + URLUtils.encodeToURL(seg);
        }
        return s;
    }

    public String getParent() {
        return parent;
    }
}
