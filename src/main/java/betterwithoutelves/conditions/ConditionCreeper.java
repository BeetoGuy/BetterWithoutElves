package betterwithoutelves.conditions;

import betterwithoutelves.api.IExplosionCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;

public class ConditionCreeper implements IExplosionCondition<EntityCreeper> {
    @Override
    public boolean isConditionFulfilled(EntityCreeper entity) {
        return entity.getPowered();
    }

    @Override
    public boolean isCompatible(Entity entity) {
        return entity.getClass().getCanonicalName().equals("net.minecraft.entity.monster.EntityCreeper");
    }
}
