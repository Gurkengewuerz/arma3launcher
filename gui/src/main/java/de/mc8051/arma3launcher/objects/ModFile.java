package de.mc8051.arma3launcher.objects;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class ModFile implements AbstractMod {

    private File f;
    private long size;
    private String folder;
    private String filename;
    private String modfileString;
    private String sha1sum;
    private String parent;
    private String localGeneratedSHA1sum = "";

    public ModFile(File f, String modfile, String parent, long size, String sha1sum) {
        // File: Abosolut Path
        // modfile: addons/config/something.pbo
        // size: size as in metafile on server
        this.f = f;
        this.size = size;
        this.filename = Paths.get(f.getPath()).getFileName().toString();
        this.modfileString = modfile;
        this.sha1sum = sha1sum.toLowerCase();
        this.parent = parent;
    }

    public ModFile(File f, String modfile, long size, String sha1sum) {
        this(f, modfile, null, size, sha1sum);
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

    public String getLocalGeneratedSHA1Sum() {
        try {
            if (localGeneratedSHA1sum.isEmpty() && exists()) {
                localGeneratedSHA1sum = FileUtils.sha1Hex(f);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        return localGeneratedSHA1sum;
    }

    public String getRemoteFile() {
        String s = ArmA3Launcher.config.getString("sync.url");
        if (parent == null || parent.isEmpty()) {
            return s + "/" + encodeToURL(getName());
        }

        s += "/" + encodeToURL(parent);
        for (String seg : getPath()) {
            s += "/" + encodeToURL(seg);
        }
        return s;
    }

    public String getParent() {
        return parent;
    }

    private String encodeToURL(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name()).replace("+", "%20").replace("@", "%40");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        return "";
    }
}
