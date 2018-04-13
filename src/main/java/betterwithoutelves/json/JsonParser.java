package betterwithoutelves.json;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JsonParser {
    public static void init(File file) {
        copyFile(findExplosionFile(), file.getParentFile().toPath().resolve(findExplosionFile()));
        copyFile(findLootFile(), file.getParentFile().toPath().resolve(findLootFile()));
    }

    private static String findExplosionFile() {
        return "betterwithoutelves/json/explosives.json";
    }

    private static String findLootFile() {
        return "betterwithoutelves/json/extra_loot.json";
    }

    private static void copyFile(String from, Path to) {
        try {
            if (!Files.exists(to)) {
                Files.createDirectories(to.getParent());
                Files.copy(getResourceAsStream(from), to, StandardCopyOption.REPLACE_EXISTING);
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
