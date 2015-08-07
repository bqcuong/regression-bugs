package com.milaboratory.core.alignment.blast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores Blast path and manages interaction between JVM and binary blast commands. It will try to find
 * blast on the system's PATH if it is not set explicitly.
 *
 * To set blast path use {@link #setBlastPath(String)} or set blastPath java property.
 */
public class Blast {
    private static final String ERROR_CMD_PATH = "***ERROR***";
    private static String path = "";
    private static Map<String, String> commandPaths = new HashMap<>();

    static {
        String propertyBlastPath = System.getProperty("blastPath");
        if (propertyBlastPath != null)
            setBlastPath(propertyBlastPath);
    }

    /**
     * Sets the blast path (e.g. ".../tools/blast/bin" or ".../tools/blast/").
     *
     * @param path path to blast distribution
     */
    public static synchronized void setBlastPath(String path) {
        // Adding "/" at the end of the sequence
        if (!path.endsWith(File.separator))
            path += File.separatorChar;

        // Setting path
        Blast.path = path;

        // Resetting all cached command paths
        commandPaths.clear();
    }

    static synchronized String getBlastCommand(String command, boolean withError) {
        String cmdPath = commandPaths.get(command);

        if (cmdPath == null)
            commandPaths.put(command, cmdPath = createPathFor(command));

        if (withError && cmdPath.equals(ERROR_CMD_PATH))
            throw new RuntimeException("Can't find path for \"" + command + "\".");

        return cmdPath;
    }

    static synchronized boolean isBlastAvailable() {
        return !getBlastCommand("blastn", false).equals(ERROR_CMD_PATH);
    }

    private static String createPathFor(String command) {
        String cmd = path + command;
        if (checkCmd(cmd))
            return cmd;

        if (!path.isEmpty() && checkCmd(cmd = path + "bin/" + command))
            return cmd;

        return ERROR_CMD_PATH;
    }

    private static boolean checkCmd(String cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd, "-h");
            Process process = pb.start();
            process.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
