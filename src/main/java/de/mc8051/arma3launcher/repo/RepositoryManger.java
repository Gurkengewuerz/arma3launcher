package de.mc8051.arma3launcher.repo;

import de.mc8051.arma3launcher.ArmA3Launcher;
import de.mc8051.arma3launcher.interfaces.Observable;
import de.mc8051.arma3launcher.interfaces.Observer;
import de.mc8051.arma3launcher.objects.AbstractMod;
import de.mc8051.arma3launcher.objects.Changelog;
import de.mc8051.arma3launcher.objects.Mod;
import de.mc8051.arma3launcher.objects.ModFile;
import de.mc8051.arma3launcher.objects.Modset;
import de.mc8051.arma3launcher.objects.Server;
import de.mc8051.arma3launcher.utils.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class RepositoryManger implements Observable {

    private static RepositoryManger instance;

    public static ArrayList<AbstractMod> MOD_LIST = new ArrayList<>();
    public static int MOD_LIST_SIZE = 0;
    private static HashMap<Type, DownloadStatus> statusMap = new HashMap<>();

    private List<Observer> observerList = new ArrayList<>();

    private RepositoryManger() {
        statusMap.put(Type.METADATA, DownloadStatus.FINNISHED);
        statusMap.put(Type.MODSET, DownloadStatus.FINNISHED);
        statusMap.put(Type.CHANGELOG, DownloadStatus.FINNISHED);
    }

    private void getAsync(String urlS, Callback.HttpCallback callback) {
        new Thread(() -> {
            try {
                URI url = new URI(urlS);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .headers("User-Agent", ArmA3Launcher.USER_AGENT)
                        .timeout(Duration.of(3, SECONDS))
                        .build();

                HttpResponse<String> response = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build().send(request, HttpResponse.BodyHandlers.ofString());

                Response r = new Response(response);

                if (!r.isSuccessful()) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cant open " + r.request().uri() + " code " + r.getStatusCode());
                    return;
                }

                callback.response(r);
            } catch (IOException | URISyntaxException | InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                callback.response(null);
            }
        }).start();
    }

    public void refreshMeta() {
        statusMap.replace(Type.METADATA, DownloadStatus.RUNNING);
        RepositoryManger.getInstance().notifyObservers(Type.METADATA.toString());
        getAsync(ArmA3Launcher.config.getString("sync.url") + "/.sync/server.json", new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) {
                    statusMap.replace(Type.METADATA, DownloadStatus.ERROR);
                    RepositoryManger.getInstance().notifyObservers(Type.METADATA.toString());
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(r.getBody());

                    if (jsonObject.has("modsets")) {
                        Modset.MODSET_LIST.clear();
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
                        Server.SERVER_LIST.clear();
                        JSONArray servers = jsonObject.getJSONArray("servers");
                        if (servers.length() > 0) {
                            for (int i = 0; i < servers.length(); i++) {
                                JSONObject server = servers.getJSONObject(i);
                                new Server(server);
                            }
                        }
                    }

                    statusMap.replace(Type.METADATA, DownloadStatus.FINNISHED);
                    RepositoryManger.getInstance().notifyObservers(Type.METADATA.toString());
                } catch (NullPointerException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    public void refreshModset() {
        statusMap.replace(Type.MODSET, DownloadStatus.RUNNING);
        RepositoryManger.getInstance().notifyObservers(Type.MODSET.toString());
        getAsync(ArmA3Launcher.config.getString("sync.url") + "/.sync/modset.json", new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) {
                    statusMap.replace(Type.MODSET, DownloadStatus.ERROR);
                    RepositoryManger.getInstance().notifyObservers(Type.MODSET.toString());
                    return;
                }

                try {
                    RepositoryManger.MOD_LIST.clear();
                    RepositoryManger.MOD_LIST_SIZE = 0;
                    JSONObject jsonObject = new JSONObject(r.getBody());

                    String modPath = ArmA3Launcher.user_config.get("client", "modPath");
                    if(modPath == null) modPath = "";

                    String finalModPath = modPath;
                    jsonObject.keySet().forEach(modname ->
                    {
                        Object keyvalue = jsonObject.get(modname);

                        if (!(keyvalue instanceof JSONObject)) return;

                        JSONObject jsonMod = (JSONObject)keyvalue;
                        if(!jsonMod.has("size")) return;

                        long modsize = jsonMod.getLong("size");

                        if(jsonMod.has("content")) {
                            // Mod Directory
                            JSONObject content = jsonMod.getJSONObject("content");

                            ArrayList<ModFile> modFiles = new ArrayList<>();
                            Iterator<String> keys = content.keys();
                            while (keys.hasNext()) {
                                String modfile = keys.next();
                                JSONObject jo = content.getJSONObject(modfile);
                                long modfilesize = jo.getLong("size");
                                String sha1 = jo.getString("sha1");

                                modFiles.add(new ModFile(new File(finalModPath + File.separator + modname + File.separator + modfile), modfile, modname, modfilesize, sha1));
                                RepositoryManger.MOD_LIST_SIZE++;
                            }

                            MOD_LIST.add(new Mod(modname, modsize, modFiles));
                        } else {
                            // Single File
                            MOD_LIST.add(new ModFile(new File(finalModPath + File.separator + modname), modname, modsize, jsonMod.getString("sha1")));
                            RepositoryManger.MOD_LIST_SIZE++;
                        }

                    });

                    statusMap.replace(Type.MODSET, DownloadStatus.FINNISHED);
                    RepositoryManger.getInstance().notifyObservers(Type.MODSET.toString());
                } catch (NullPointerException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    public void refreshChangelog() {
        statusMap.replace(Type.CHANGELOG, DownloadStatus.RUNNING);
        RepositoryManger.getInstance().notifyObservers(Type.CHANGELOG.toString());

        getAsync(ArmA3Launcher.config.getString("sync.url") + "/.sync/changelog.txt", new Callback.HttpCallback() {
            @Override
            public void response(Response r) {
                if (!r.isSuccessful()) {
                    statusMap.replace(Type.CHANGELOG, DownloadStatus.ERROR);
                    RepositoryManger.getInstance().notifyObservers(Type.CHANGELOG.toString());
                    return;
                }

                statusMap.replace(Type.CHANGELOG, DownloadStatus.FINNISHED);
                RepositoryManger.getInstance().notifyObservers(Type.CHANGELOG.toString());
                Changelog.setChangelog(r.getBody());
            }
        });
    }

    public static RepositoryManger getInstance() {
        if (instance == null) instance = new RepositoryManger();
        return instance;
    }

    public DownloadStatus getStatus(Type type) {
        return statusMap.get(type);
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

    public enum Type {

        METADATA("metadata"),
        MODSET("modset"),
        CHANGELOG("changelog");

        private String modset;

        Type(String modset) {
            this.modset = modset;
        }

        @Override
        public String toString() {
            return modset;
        }
    }
}
