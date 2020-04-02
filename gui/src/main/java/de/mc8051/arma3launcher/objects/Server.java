package de.mc8051.arma3launcher.objects;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Server {

    public static HashMap<String, Server> SERVER_LIST = new HashMap<>();

    private String name;
    private String password;
    private String ip;
    private int port;
    private Modset preset;

    public Server(String name, String password, String ip, int port, Modset preset) {
        this.name = name;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.preset = preset;

        SERVER_LIST.put(name, this);
    }

    public Server(JSONObject o) {
        if (!o.has("name") || !o.has("password") || !o.has("ipaddress") || !o.has("port") || !o.has("preset")) return;
        name = o.getString("name");
        password = o.getString("password");
        ip = o.getString("ipaddress");
        port = o.getInt("port");

        if (!Modset.MODSET_LIST.containsKey(o.getString("preset"))) return;
        preset = Modset.MODSET_LIST.get(o.getString("preset"));

        SERVER_LIST.put(name, this);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Modset getPreset() {
        return preset;
    }

    public ArrayList<String> getStartparameter() {
        ArrayList<String> list = new ArrayList<>();
        list.add("-connect=" + ip);
        list.add("-port=" + port);
        if (!password.isEmpty())
            list.add("-password=" + ip);
        return list;
    }
}
