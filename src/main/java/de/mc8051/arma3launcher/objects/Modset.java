package de.mc8051.arma3launcher.objects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Modset {

    public static HashMap<String, Modset> MODSET_LIST = new HashMap<>();

    private String name;
    private Type type;
    private List<Mod> mods = new ArrayList<>();

    public Modset(String name, Type type, List<Mod> mods) {
        this(name, type, mods, true);
    }

    public Modset(String name, Type type, List<Mod> mods, boolean add) {
        this.name = name;
        this.type = type;
        this.mods = mods;

        if(add) MODSET_LIST.put(name, this);
    }

    public Modset(JSONObject o, Type type) {
        if(!o.has("name") || !o.has("mods")) return;
        name = o.getString("name");

        JSONArray modlist = o.getJSONArray("mods");
        for(int j = 0; j < modlist.length(); j++){
            mods.add(new Mod(modlist.getString(j)));
        }

        this.type = type;

        MODSET_LIST.put(name, this);
    }

    public String getName() {
        return name;
    }

    public List<Mod> getMods() {
        return mods;
    }

    public Type getType() {
        return type;
    }

    public void play() {
        // TODO: Implement play with this Modset
    }

    public static enum Type {
        SERVER,
        CLIENT
    }
}
