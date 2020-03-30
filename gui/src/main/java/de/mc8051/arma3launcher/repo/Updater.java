package de.mc8051.arma3launcher.repo;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.utils.Callback;
import de.mc8051.arma3launcher.utils.URLUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class Updater {

    private File patcherFile = new File(ArmA3Launcher.APPLICATION_PATH + File.separator + "patcher.jar");
    private File me;
    private long lastCheck = 0;
    private Version newestVersion = null;
    private String newFile = "";

    public Updater() {
        try {
            me = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ignored) {}
    }

    public void update() throws IOException {
        if (!me.exists() || !me.isFile()) throw new IOException("Own jar not exists. Are you running in dev?");
        if (!patcherFile.exists()) throw new IOException("Patcher does not exists");
        Runtime.getRuntime().exec(
                "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"" +
                        " -jar \"" + patcherFile.getAbsolutePath() + "\"" +
                        " \"" + ArmA3Launcher.config.getString("sync.url") + "/.sync/" + URLUtils.encodeToURL(newFile) + "\"" +
                        " \"" + me.getAbsolutePath() + "\""
        );
    }

    public void downloadPatcher() {
        if(patcherFile.exists()) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Patcher already exists. Skip.");
            return;
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ArmA3Launcher.config.getString("sync.url") + "/.sync/patcher.jar"))
                    .GET()
                    .build();

            Path tempFile = Files.createTempFile("patcher", "jar");

            client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(tempFile)).thenAccept((r) -> {
                if (r.statusCode() != 200) return;
                try {
                    Files.copy(tempFile, patcherFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Patcher copied to " + patcherFile.getAbsolutePath());
                } catch (IOException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Copy patcher failed", e);
                }
            });
        } catch (IOException | URISyntaxException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void getNewestVersion(Callback.HttpCallback callback) {
        RepositoryManger.getInstance().getAsync(ArmA3Launcher.config.getString("sync.url") + "/.sync/version.txt", callback);
    }

    public void needUpdate(Callback.NeedUpdateCallback callback) {
        final Version currentVersion = new Version(ArmA3Launcher.VERSION);

        if(lastCheck != 0 && System.currentTimeMillis() - lastCheck < 2 * 60 * 1000) {
            callback.response(currentVersion.compareTo(newestVersion) < 0, newestVersion);
            lastCheck = System.currentTimeMillis();
            return;
        }

        getNewestVersion(new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) callback.response(false, null);
                final String[] split = r.getBody().split(":");
                newestVersion = new Version(split[0]);
                newFile = split[1].replace("\n", "").replace("\r","").trim();

                final boolean needUpdate = currentVersion.compareTo(newestVersion) < 0;
                if(needUpdate) downloadPatcher();
                lastCheck = System.currentTimeMillis();

                callback.response(needUpdate, newestVersion);
            }
        });
    }
}
