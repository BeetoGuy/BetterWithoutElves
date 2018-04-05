package betterwithoutelves.conditions;

import betterwithoutelves.api.IExplosionCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartTNT;

public class ConditionTNTCart implements IExplosionCondition<EntityMinecartTNT> {
    @Override
    public boolean isConditionFulfilled(EntityMinecartTNT entity) {
        return entity.isIgnited();
    }

    @Override
    public boolean isCompatible(Entity entity) {
        return entity.getClass().getCanonicalName().equals("net.minecraft.entity.item.EntityMinecartTNT");
    }
}
