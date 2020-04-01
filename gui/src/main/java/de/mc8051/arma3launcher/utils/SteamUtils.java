package de.mc8051.arma3launcher.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Created by gurkengewuerz.de on 23.03.2020.
 */
public class SteamUtils {

    private static final Logger logger = LogManager.getLogger(SteamUtils.class);

    public static boolean findProcess(String findProcess) {
        Optional<ProcessHandle> p = ProcessHandle.allProcesses()
                .filter(processHandle -> processHandle.info().command().isPresent())
                .filter(process -> process.info().command().get().toLowerCase().endsWith(findProcess)).findFirst();
        if(p.isEmpty()) return false;
        logger.debug("Found process {}", findProcess);
        logger.debug("    PID {}", p.get().pid());
        logger.debug("    Name {}", p.get().info().command());
        logger.debug("    User {}", p.get().info().user());
        return true;
    }
}
