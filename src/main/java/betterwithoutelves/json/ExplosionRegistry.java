package betterwithoutelves.json;

import betterwithoutelves.BetterWithoutElves;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import vazkii.botania.api.BotaniaAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExplosionRegistry {
    private static Map<String, Explosive> EXPLOSIONS = Maps.newHashMap();
    private static List<ItemStack> LOOTS = Lists.newArrayList();
    public static float PERCENTILE = 0;

    public static void jsonRegistration(File file) {
        JsonParser.init(file);
        Path path = file.getParentFile().toPath().resolve("betterwithoutelves/json/explosives.json");
        Path loot = file.getParentFile().toPath().resolve("betterwithoutelves/json/extra_loot.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(path);
            readJsonArray(JsonUtils.fromJson(gson, reader, JsonObject.class));
            reader = Files.newBufferedReader(loot);
            readLootArray(JsonUtils.fromJson(gson, reader, JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postInitRegistration() {
        BotaniaAPI.elvenTradeRecipes.forEach(t -> registerElvenTrades(t.getOutputs()));
    }

    public static ItemStack getLootStack() {
        return LOOTS.get(BetterWithoutElves.INSTANCE.rand.nextInt(LOOTS.size())).copy();
    }

    private static void registerElvenTrades(List<ItemStack> stack) {
        stack.forEach(s -> LOOTS.add(s));
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

    private static void readLootArray(JsonObject obj) {
        JsonArray array = JsonUtils.getJsonArray(obj, "extras", null);
        if (array != null && array.size() > 0) {
            for (JsonElement element : array) {
                JsonObject o = element.getAsJsonObject();
                String name = JsonUtils.getString(o, "name", "");
                if (!name.equals("example")) {
                    if (o.has("item")) {
                        ItemStack stack = ShapedRecipes.deserializeItem(JsonUtils.getJsonObject(o, "item"), true);
                        if (!stack.isEmpty()) {
                            LOOTS.add(stack);
                        }
                    }
                }
            }
        }
    }
}
