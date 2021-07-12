package utils;

import java.io.File;

public class FileUtils {

    // Ideally, this will be set through config and will be loaded once when the code is deployed for the first time.
    private static final String USER_DIR = "user.dir";

    public static boolean trySetCurrentDirectory(String directoryName) {
        boolean result = false;
        File directory;

        directory = new File(directoryName).getAbsoluteFile();
        if (directory.exists() || directory.mkdirs()) {

            result = (System.setProperty(USER_DIR, directory.getAbsolutePath()) != null);
        }

        return result;
    }

    public static String tryGetFullQualifiedFilePath(String folder, String fileName) {
        return System.getProperty(USER_DIR) + folder + fileName;
    }
}
