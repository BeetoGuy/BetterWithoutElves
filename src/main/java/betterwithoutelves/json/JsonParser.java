package betterwithoutelves.json;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JsonParser {
    public static void init(File file) {
        copyExplosionFile(file.getParentFile().toPath().resolve(findExplosionFile()));
    }

    private static String findExplosionFile() {
        return "betterwithoutelves/json/explosives.json";
    }

    private static void copyExplosionFile(Path to) {
        try {
            if (!Files.exists(to)) {
                Files.createDirectories(to.getParent());
                Files.copy(getResourceAsStream(findExplosionFile()), to, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static InputStream getResourceAsStream(String path) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + path);
        return in != null ? in : JsonParser.class.getResourceAsStream("/" + path);
    }
}
