package betterwithoutelves;

import betterwithmods.common.entity.EntityDynamite;
import betterwithoutelves.json.ExplosionRegistry;
import betterwithoutelves.json.Explosive;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;
import vazkii.botania.common.block.tile.TileAlfPortal;

import java.util.List;
import java.util.Random;

@Mod(modid = "betterwithoutelves", name = "Better Without Elves", version = "${version}", dependencies = "required-after:betterwithmods;required-after:botania")
public class BetterWithoutElves {
    @GameRegistry.ObjectHolder("betterwithmods:mystery_meat")
    public static final Item MYSTERY_MEAT = null;
    private static final Random rand = new Random();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(this);
        ExplosionRegistry.jsonRegistration(evt.getSuggestedConfigurationFile());
    }

    @SubscribeEvent
    public void explodeAlfheim(ElvenPortalUpdateEvent evt) {
        if (evt.portalTile instanceof TileAlfPortal) {
            TileAlfPortal portal = (TileAlfPortal)evt.portalTile;
            List<Entity> entities = portal.getWorld().getEntitiesWithinAABB(Entity.class, evt.aabb, this::isExplosiveEntity);
            if (!entities.isEmpty()) {
                entities.forEach(e -> tickTimer(portal, e));
            }
        }
    }

    private void tickTimer(TileAlfPortal portal, Entity entity) {
        if (entity.getEntityData().hasKey("timer")) {
            byte time = entity.getEntityData().getByte("timer");
            if (time >= 60) {
                Explosive exp = ExplosionRegistry.getExplosion(entity.getClass().getCanonicalName());
                entity.setDead();
                if (exp != null) {
                    float force = exp.getForce(entity);
                    List<ItemStack> stacks = Lists.newArrayList();
                    if (MYSTERY_MEAT != null)
                        stacks.add(new ItemStack(MYSTERY_MEAT, MathHelper.floor(force) + 1));
                    if (force * ExplosionRegistry.PERCENTILE > rand.nextFloat()) {
                        ItemStack treasure = getRandomAlfTreasure();
                        if (!treasure.isEmpty()) stacks.add(treasure);
                    }
                    if (!stacks.isEmpty()) {
                        if (portal.consumeMana(null, 500 * stacks.size(), false)) {
                            for (ItemStack stack : stacks) {
                                EntityItem item = new EntityItem(portal.getWorld(), portal.getPos().getX() + 0.5F, portal.getPos().getY() + 1.5F, portal.getPos().getZ() + 0.5F, stack);
                                item.getEntityData().setBoolean("_elvenPortal", true);
                                portal.getWorld().spawnEntity(item);
                            }
                        }
                        portal.getWorld().playSound(null, portal.getPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 0.25F, 0.5F);
                    }
                }
            } else entity.getEntityData().setByte("timer", (byte)(time + 1));
        } else entity.getEntityData().setByte("timer", (byte)1);
    }

    private ItemStack getRandomAlfTreasure() {
        List<ItemStack> stacks = BotaniaAPI.elvenTradeRecipes.get(rand.nextInt(BotaniaAPI.elvenTradeRecipes.size())).getOutputs();
        return !stacks.isEmpty() ? stacks.get(rand.nextInt(stacks.size())).copy() : ItemStack.EMPTY;
    }

    private boolean isExplosiveEntity(Entity entity) {
        Explosive exp = ExplosionRegistry.getExplosion(entity.getClass().getCanonicalName());
        return exp != null && exp.getForce(entity) > 0;
    }
}
