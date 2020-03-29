package de.mc8051.arma3launcher.objects;

import de.mc8051.arma3launcher.ArmA3Launcher;
import org.ini4j.Ini;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class Modset implements Comparable {

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

        if (add) MODSET_LIST.put(name, this);
    }

    public Modset(String name, JSONArray modlist, Type type) {
        for (int j = 0; j < modlist.length(); j++) {
            mods.add(new Mod(modlist.getString(j)));
        }

        this.type = type;
        this.name = name;

        MODSET_LIST.put(name, this);
    }

    public Modset(JSONObject o, Type type) {
        this(o.getString("name"), o.getJSONArray("mods"), type);
    }

    public void save() {
        if (type != Type.CLIENT) return;

        Ini.Section section = ArmA3Launcher.user_config.get("presets");
        if (section == null) {
            section = ArmA3Launcher.user_config.add("presets");
        }
        if (section != null) {

            List<String> list = mods.stream()
                    .map(Mod::getName)
                    .collect(Collectors.toList());
            JSONArray ja = new JSONArray(list);
            if (section.containsKey(name))
                section.replace(name, ja.toString());
            else
                section.add(name, ja.toString());

            try {
                ArmA3Launcher.user_config.store();
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void removeFromConfig() {
        MODSET_LIST.remove(name);
        if (type != Type.CLIENT) return;

        Ini.Section section = ArmA3Launcher.user_config.get("presets");
        if (section != null) {
            if (section.containsKey(name)) {
                section.remove(name);

                try {
                    ArmA3Launcher.user_config.store();
                } catch (IOException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        }

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

    public Modset clone(String newName, Type newType) {
        return new Modset(newName, newType, new ArrayList<>(mods));
    }

    public Modset clone() {
        return new Modset(name, type, mods, false);
    }

    public void setMods(List<String> selectedMods) {
        mods.addAll(selectedMods.stream().map(Mod::new).collect(Collectors.toList()));
    }

    @Override
    public int compareTo(Object o) {
        return getName().compareToIgnoreCase(((Modset) o).getName());
    }

    public static enum Type {
        SERVER,
        CLIENT,
        PLACEHOLDER;
    }

    @Override
    public String toString() {
        return getName();
    }
}
