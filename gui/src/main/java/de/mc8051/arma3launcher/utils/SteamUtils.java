package de.mc8051.arma3launcher.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class SteamUtils {

    public static boolean findProcess(String findProcess) throws IOException {
        String filenameFilter = "/nh /fi \"Imagename eq "+findProcess+"\"";
        String tasksCmd = System.getenv("windir") +"/system32/tasklist.exe "+filenameFilter;

        Process p = Runtime.getRuntime().exec(tasksCmd);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        ArrayList<String> procs = new ArrayList<String>();
        String line = null;
        while ((line = input.readLine()) != null)
            procs.add(line);

        input.close();

        return procs.stream().anyMatch(row -> row.contains(findProcess));
    }
}
