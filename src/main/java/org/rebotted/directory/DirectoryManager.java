package org.rebotted.directory;


import org.rebotted.directory.exceptions.InvalidDirectoryNameException;

import java.io.File;
import java.io.IOException;

public class DirectoryManager {

    public final static String BOT_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + ".2006rebotted_file_system";
    public final static String CACHE = "Cache";
    public final static String SCRIPTS = "Scripts";
    public final static String TEMP = "Temp";
    public final static String SCREENSHOTS = "Screenshots";
    public static String CACHE_PATH;
    public static String SCRIPTS_PATH;
    public static String TEMP_PATH;
    public static String SCREENSHOTS_PATH;
    private static DirectoryManager instance;
    private final Directory botDirectory;

    private DirectoryManager() {
        botDirectory = getRootDirectory();
        try {
            validateSubDirectories();
        } catch (InvalidDirectoryNameException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new DirectoryManager();
        }
    }

    public static DirectoryManager getInstance() {
        return instance;
    }

    public Directory getRootDirectory() {
        final Directory directory = new Directory(BOT_DIRECTORY_PATH);
        if (!directory.exists()) {
            try {
                if (directory.create()) {
                    return directory;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return directory;
    }

    private void validateSubDirectories() throws IOException, InvalidDirectoryNameException {
        botDirectory.createSubDirectory(CACHE);
        CACHE_PATH = botDirectory.getSubDirectory(CACHE).getPath();
        botDirectory.createSubDirectory(SCRIPTS);
        SCRIPTS_PATH = botDirectory.getSubDirectory(SCRIPTS).getPath();
        botDirectory.getSubDirectory(CACHE).createSubDirectory(TEMP);
        TEMP_PATH = botDirectory.getSubDirectory(CACHE).getSubDirectory(TEMP).getPath();
        botDirectory.getSubDirectory(CACHE).createSubDirectory(SCREENSHOTS);
        SCREENSHOTS_PATH = botDirectory.getSubDirectory(CACHE).getSubDirectory(SCREENSHOTS).getPath();
        System.out.println("Directories validated.");
    }

}
