package de.mc8051.arma3launcher.utils;

import de.mc8051.arma3launcher.Parameter;
import de.mc8051.arma3launcher.Parameters;
import de.mc8051.arma3launcher.WinRegistry;
import de.mc8051.arma3launcher.objects.Modset;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gurkengewuerz.de on 30.03.2020.
 */
public class ArmaUtils {

    public static Path getInstallationPath() {
        String regKey = null;
        try {
            regKey = WinRegistry.getValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\bohemia interactive\\arma 3", "main");
        } catch (IOException | InterruptedException ignored) {
            try {
                regKey = WinRegistry.getValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\bohemia interactive\\arma 3", "main");
            } catch (IOException | InterruptedException e) {
                Logger.getLogger(ArmaUtils.class.getName()).log(Level.INFO, "Arma patch cant be detected automatically");
            }
        }

        if (regKey == null) return null;
        final Path main = Paths.get(regKey);
        if (!checkArmaPath(main)) return null;
        return main;
    }

    public static boolean checkArmaPath(Path path) {
        ArrayList<String> search = new ArrayList<>(Arrays.asList("arma3.exe", "steam.dll"));
        final File f = path.toFile();
        if (!f.exists() || !f.isDirectory()) return false;
        if (f.listFiles() == null) return false;
        File[] listOfFiles = f.listFiles();

        try {
            for (File file : listOfFiles) {
                if (search.isEmpty()) return true;
                if (file.isFile()) {
                    search.remove(file.getName().toLowerCase());
                }
            }
        } catch (NullPointerException ex) {
            return false;
        }
        return false;
    }

    public static String getGameParameter(Modset modset) {
        StringBuilder sb = new StringBuilder();

        List<String> parameters = Arrays.stream(Parameters.values())
                .filter(p -> p.getType() == Parameter.ParameterType.ARMA)
                .filter(p -> !p.getStartParameter().isEmpty())
                .filter(p -> {
                    if (p.getClazz() == Boolean.class) {
                        boolean b = (boolean) p.toParameter().getValue();
                        return b;
                    }
                    String paraVal = (String) p.toParameter().getValue();
                    return !(paraVal == null || paraVal.isEmpty() || paraVal.equals("-1") || paraVal.equals("0"));
                })
                .map(parameter -> {
                    final Class<?> clazz = parameter.getClazz();
                    if (clazz == Boolean.class) {
                        return "-" + parameter.getStartParameter();
                    }
                    return "\"-" + parameter.getStartParameter() + "=" + parameter.toParameter().getValue() + "\"";
                })
                .collect(Collectors.toList());
        sb.append(String.join(" ", parameters)).append(" ");

        final List<String> modParameter = modset.getStartParamter();
        if (!modParameter.isEmpty())
            sb.append("\"-mod=").append(String.join(";", modParameter)).append("\"");

        return sb.toString();
    }

    public static void start(Modset modset) {
        start(modset, new String[]{});
    }

    public static void start(Modset modset, String... additionalParams) {
        final Parameter armaPathParameter = Parameters.ARMA_PATH.toParameter();
        File arma3battleye = new File((String) armaPathParameter.getValue(), "arma3battleye.exe");
        final Parameter use64Bit = Parameters.USE_64_BIT_CLIENT.toParameter();

        String gameParameters = getGameParameter(modset);
        String additionalParameters = String.join(" ", additionalParams);
        String battleEye = "\"" + arma3battleye.getAbsolutePath() + "\" 2 1 1 -exe " + ((Boolean) use64Bit.getValue() ? "arma3_x64.exe" : "arma3.exe");
        String command = battleEye + " " + gameParameters + " " + additionalParameters;
        Logger.getLogger(ArmaUtils.class.getName()).log(Level.INFO, command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            Logger.getLogger(ArmaUtils.class.getName()).log(Level.INFO, "Starting failed!", e);
        }
    }
}
