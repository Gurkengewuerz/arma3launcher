package de.mc8051.arma3launcher.repo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 24.03.2020.
 */
public class DownloadThread implements Runnable {

    private ProcessBuilder processBuilder;
    private Process process;
    private Thread thread;

    private Status status = Status.PENDING;

    public DownloadThread(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
        this.processBuilder.redirectErrorStream(true);

        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        process.destroy();
        thread.interrupt();
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public void run() {
        try {
            process = processBuilder.start();
            status = Status.RUNNING;

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ( (line = reader.readLine()) != null && !thread.isInterrupted()) {
                System.out.println(line);
            }

            int exitVal = process.waitFor();
            if(exitVal == 0) status = Status.FINNISHED;
            else status = Status.ERROR;

            System.out.println(exitVal);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            status = Status.ERROR;
        }
    }

    private enum Status {
        PENDING(0),
        RUNNING(1),
        FINNISHED(2),
        ERROR(3);

        Status(int i) {

        }
    }
}
