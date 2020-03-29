package de.mc8051.arma3launcher.repo;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.utils.Callback;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class Updater {

    private boolean canPatch = false;

    public void update() {

    }

    public void getNewestVersion(Callback.HttpCallback callback) {
        RepositoryManger.getInstance().getAsync(ArmA3Launcher.config.getString("sync.url") + "/.sync/version.txt", callback);
    }

    public void needUpdate(Callback.NeedUpdateCallback callback) {
        final Version currentVersion = new Version(ArmA3Launcher.VERSION);
        getNewestVersion(new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) callback.response(false, null);
                final String[] split = r.getBody().split(":");
                final Version newestVersion = new Version(r.getBody().split(":")[0]);
                callback.response((currentVersion.compareTo(newestVersion) < 0), newestVersion);
            }
        });
    }
}
