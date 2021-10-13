package lib;

import java.nio.file.Files;
import java.nio.file.Paths;

public class IO {
    /*
     * Reads file as byte array
     */
    public static byte[] read(String filename) {
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch (Exception e) {
            return null;
        }
    }
}