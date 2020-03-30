package de.mc8051.arma3launcher;

import org.ini4j.Ini;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class Parameter<T> {

    private String name;
    private ParameterType pType;
    private Class<T> persistentClass;
    private String[] values = null;
    private String startParameter = "";

    public Parameter(String name, ParameterType pType, Class<T> persistentClass) {
        this(name, pType, persistentClass, null, "");
    }

    public Parameter(String name, ParameterType pType, Class<T> persistentClass, String startParameter) {
        this(name, pType, persistentClass, null, startParameter);
    }

    public Parameter(String name, ParameterType pType, Class<T> persistentClass, String[] values, String startParameter) {
        this.name = name;
        this.pType = pType;
        this.persistentClass = persistentClass;
        this.values = values;
        this.startParameter = startParameter;
    }

    public String getName() {
        return name;
    }

    public String getUserConfigSectionName() {
        if (pType == ParameterType.CLIENT) return "client";
        if (pType == ParameterType.ARMA) return "arma";
        return "";
    }

    public void save(T data) {
        T def = getDefault();

        if (data == def || (persistentClass.getTypeName().equals("java.lang.String") && String.valueOf(data).equals(String.valueOf(def)))) {
            // remove entry from user config
            ArmA3Launcher.user_config.remove(getUserConfigSectionName(), name);
        } else {
            // save to user config
            Ini.Section section = ArmA3Launcher.user_config.get(getUserConfigSectionName());
            if (section == null) {
                section = ArmA3Launcher.user_config.add(getUserConfigSectionName());
            }
            if (section != null) {
                if (section.containsKey(name)) {
                    ArmA3Launcher.user_config.remove(getUserConfigSectionName(), name);
                }
                section.add(name, data);
            }
        }

        try {
            ArmA3Launcher.user_config.store();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void save(int index) {
        if(values == null) throw new IllegalAccessError("call of save(int index) is only allowed for ComboBoxes");
        if(index > values.length - 1) throw new IndexOutOfBoundsException("index " + index + " is out of bound. Max: " + (values.length -1));
        save((T) values[index]);
    }

    public String getParameter() {
        if(startParameter.isEmpty()) return null;
        return startParameter;
    }

    public T getConfigValue() {
        // Get User Value else Default else null
        Ini.Section section = ArmA3Launcher.user_config.get(getUserConfigSectionName());
        if (section != null) {
            if(section.containsKey(name)) {
                String val = section.get(name);

                if (persistentClass.getTypeName().equals("java.lang.Boolean")) {
                    return (T) (Boolean) Boolean.valueOf(val.toLowerCase());
                } else return (T) val;
            }
        }

        return null;
    }

    public T getValue() {
        final T configValue = getConfigValue();
        if(configValue != null) return configValue;
        return getDefault();
    }

    public int getIndex() {
        if(values == null) throw new IllegalAccessError("call of save(int index) is only allowed for ComboBoxes");
        String value =String.valueOf(getValue());
        for(int i = 0; i < values.length; i++) {
            if(value.equalsIgnoreCase(values[i])) return i;
        }
        return -1;
    }

    public T getDefault() {
        if (persistentClass.getTypeName().equals("java.lang.Boolean")) {
            return (T) (Boolean) ArmA3Launcher.config.getBoolean( getUserConfigSectionName() + "." + name);
        } else if (persistentClass.getTypeName().equals("java.lang.String"))
            return (T) ArmA3Launcher.config.getString(getUserConfigSectionName()+ "." + name);
        else return null;
    }

    public String getStartParameter() {
        return startParameter;
    }

    enum ParameterType {
        ARMA,
        CLIENT
    }
}
