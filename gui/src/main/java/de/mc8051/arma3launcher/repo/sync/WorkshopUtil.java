package de.mc8051.arma3launcher.repo.sync;

import de.mc8051.arma3launcher.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gurkengewuerz.de on 30.03.2020.
 */
public class WorkshopUtil {

    public static Map<Path, Long> workshopFiles() {
        Map<Path, Long> fileMap = new HashMap<>();

        final String armaPath = Parameters.ARMA_PATH.toStringParameter().getValue();
        if(armaPath == null) return fileMap;

        final Path workshopPath = Paths.get(armaPath, "!Workshop");

        if(!workshopPath.toFile().exists()) return fileMap;
        if(!workshopPath.toFile().isDirectory()) return fileMap;

        try {
            fileMap = Files.find(workshopPath,
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .filter((p) -> p.toFile().getName().endsWith(".pbo"))
                    .collect(Collectors.toMap(path -> path, path -> path.toFile().length()));
            return fileMap;
        } catch (IOException ex) {
            return fileMap;
        }
    }
}
