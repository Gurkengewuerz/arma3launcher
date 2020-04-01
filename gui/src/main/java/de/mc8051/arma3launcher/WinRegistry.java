package de.mc8051.arma3launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WinRegistry {

    public static String getValue(String keyPath, String keyName) throws IOException, InterruptedException {
        Process keyReader = Runtime.getRuntime().exec(
                "reg query \"" + keyPath + "\" /v \"" + keyName + "\"");

        BufferedReader outputReader;
        String readLine;
        StringBuffer outputBuffer = new StringBuffer();

        outputReader = new BufferedReader(new InputStreamReader(
                keyReader.getInputStream()));

        while ((readLine = outputReader.readLine()) != null) {
            outputBuffer.append(readLine);
        }

        String[] outputComponents = outputBuffer.toString().split("    ");
        keyReader.waitFor();
        return outputComponents[outputComponents.length - 1];
    }
}