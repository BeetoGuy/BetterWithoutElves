package betterwithoutelves.json;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ExplosionRegistry {
    private static Map<String, Explosive> EXPLOSIONS = Maps.newHashMap();
    public static float PERCENTILE = 0;

    public static void jsonRegistration(File file) {
        JsonParser.init(file);
        Path path = file.getParentFile().toPath().resolve("betterwithoutelves/json/explosives.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path);
            readJsonArray(JsonUtils.fromJson(gson, reader, JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean explosiveExists(String string) {
        return EXPLOSIONS.keySet().contains(string);
    }

    public static Explosive getExplosion(String string) {
        return explosiveExists(string) ? EXPLOSIONS.get(string) : null;
    }

    private static void readJsonArray(JsonObject obj) {
        PERCENTILE = JsonUtils.getFloat(obj, "percentile", 0.01F);
        JsonArray array = JsonUtils.getJsonArray(obj, "entities", null);
        if (array != null && array.size() > 0) {
            for (JsonElement element : array) {
                JsonObject o = element.getAsJsonObject();
                String entity = JsonUtils.getString(o, "entity", "");
                if (!entity.equals("")) {
                    EXPLOSIONS.put(entity, Explosive.fromJson(o));
                }
            }
        }
    }
}
