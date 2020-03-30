/*
 * This file is part of the arma3launcher distribution.
 * Copyright (c) 2020-2020 Niklas Schütrumpf (Gurkengewuerz)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.mc8051.arma3launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class Arma3LauncherPatcher {

    public static void main(String[] args) {
        if (args.length != 2) {
            Logger.getLogger(Arma3LauncherPatcher.class.getName()).log(Level.SEVERE, "<url> <old_programm>");
            return;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        JFrame frame = new JFrame("Auto Patcher");
        Patcher patcher = new Patcher();
        frame.setContentPane(patcher.mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setMinimumSize(new Dimension(500, 100));
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                frame.dispose();
            }
        });

        frame.pack();
        frame.setIconImage(createIcon());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(args[0]))
                    .GET()
                    .build();

            File f = new File(args[1]);

            String fileName = Paths.get(f.getPath()).getFileName().toString();
            String fileExt = "";
            if (fileName.indexOf(".") > 0) {
                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }

            Path tempFile = Files.createTempFile(fileName, fileExt);

            HttpResponse<Path> response = client.send(request, responseInfo -> {
                HttpResponse.BodyHandler<Path> bodyHandler = HttpResponse.BodyHandlers.ofFile(tempFile);
                final String s = responseInfo.headers().firstValue("content-length").get();
                long contentSize = Long.parseLong(s);
                return new DownloadObserver(bodyHandler.apply(responseInfo), contentSize, patcher.progressBar1);
            });

            if (response.statusCode() != 200)
                throw new IllegalStateException("Download file is invalid. Got response code " + response.statusCode());

            Files.copy(tempFile, f.toPath(), StandardCopyOption.REPLACE_EXISTING);

            run(f.getAbsolutePath());

            JOptionPane.showMessageDialog(
                    frame,
                    "Launcher has been successfully updated.",
                    "Update", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } catch (IOException | URISyntaxException | InterruptedException | IllegalStateException e) {
            Logger.getLogger(Arma3LauncherPatcher.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(
                    null,
                    "An error occured.\n" + e.getMessage() + "\nUpdate process aborded.",
                    "Update failed", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void run(String path) {
        try {
            Runtime.getRuntime().exec("\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\" -jar \"" + path + "\"");
        } catch (IOException ignored) {
        }
    }

    static BufferedImage createIcon() {
        try {
            return ImageIO.read(Arma3LauncherPatcher.class.getResourceAsStream("/icons/logo_32.png"));
        } catch (IOException e) {
            Logger.getLogger(Arma3LauncherPatcher.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static class DownloadObserver implements HttpResponse.BodySubscriber<Path> {

        private int counter = 0;
        private long total = 0L;
        private long contentSize = 0L;
        private HttpResponse.BodySubscriber<Path> subscriber;
        private JProgressBar progressBar;

        public DownloadObserver(HttpResponse.BodySubscriber<Path> subscriber, long contentSize, JProgressBar progressBar) {
            this.subscriber = subscriber;
            this.contentSize = contentSize;
            this.progressBar = progressBar;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(List<ByteBuffer> item) {
            item.forEach((size) -> {
                total += size.remaining();
            });

            int progress = (int) (((double) total / (double) contentSize) * 100);
            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));

            counter++;
            subscriber.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            subscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();
        }

        @Override
        public CompletionStage<Path> getBody() {
            return subscriber.getBody();
        }
    }
}
