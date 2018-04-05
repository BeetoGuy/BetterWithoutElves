package betterwithoutelves.api;

import net.minecraft.entity.Entity;

public interface IExplosionCondition<E extends Entity> {
    public boolean isConditionFulfilled(E entity);

    public boolean isCompatible(Entity entity);
}
