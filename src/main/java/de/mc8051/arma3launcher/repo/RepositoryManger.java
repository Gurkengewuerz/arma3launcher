package de.mc8051.arma3launcher.repo;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.interfaces.Observable;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.utils.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class RepositoryManger implements Observable {

    private static RepositoryManger instance;
    private List<Observer> observerList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    private RepositoryManger() {
    }

    public void refreshMeta() {
        downloadMeta(new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cant open " + r.request().url().toString() + " code " + r.code());
                    RepositoryManger.getInstance().notifyObservers("refreshMetaFailed");
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(r.body().string());

                    if (jsonObject.has("modsets")) {
                        JSONArray modsets = jsonObject.getJSONArray("modsets");
                        if (modsets.length() > 0) {
                            for (int i = 0; i < modsets.length(); i++) {
                                JSONObject modset = modsets.getJSONObject(i);
                                new Modset(modset, Modset.Type.SERVER);
                            }
                        }
                    }

                    // Init servers after modsets because server search preset string in modsets
                    if (jsonObject.has("servers")) {
                        JSONArray servers = jsonObject.getJSONArray("servers");
                        if (servers.length() > 0) {
                            for (int i = 0; i < servers.length(); i++) {
                                JSONObject server = servers.getJSONObject(i);
                                new Server(server);
                            }
                        }
                    }

                    RepositoryManger.getInstance().notifyObservers("refreshMeta");
                } catch (IOException | NullPointerException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    private void downloadMeta(Callback.HttpCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(ArmA3Launcher.config.getString("sync.url") + "/.sync/server.json")
                        .build();

                callback.response(client.newCall(request).execute());
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                callback.response(null);
            }
        }).start();
    }

    public static RepositoryManger getInstance() {
        if (instance == null) instance = new RepositoryManger();
        return instance;
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
    public void notifyObservers(Object obj) {
        for (Observer obs : observerList) obs.update(obj);
    }
}
