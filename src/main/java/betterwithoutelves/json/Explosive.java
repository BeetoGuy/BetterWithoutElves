package betterwithoutelves.json;

import betterwithoutelves.api.IExplosionCondition;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;

public class Explosive {
    private IExplosionCondition condition;
    private float minForce;
    private float maxForce;

    public Explosive(Class<? extends IExplosionCondition> condition, float force) {
        this(condition, force, force);
    }

    public Explosive(Class<? extends IExplosionCondition> condition, float minForce, float maxForce) {
        this.condition = getCondition(condition);
        this.minForce = minForce;
        this.maxForce = maxForce;
    }

    private IExplosionCondition getCondition(Class<? extends IExplosionCondition> condition) {
        if (condition != null) {
            try {
                return condition.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Explosive fromJson(JsonObject obj) {
        String condName = JsonUtils.getString(obj, "condition", "");
        Class<? extends IExplosionCondition> condition = null;
        if (!condName.equals("")) {
            try {
                condition = (Class<? extends IExplosionCondition>)Class.forName(condName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        float force = JsonUtils.getFloat(obj, "force", -1);
        if (force > 0) {
            return new Explosive(condition, force);
        } else {
            float min = JsonUtils.getFloat(obj, "min_force", 0);
            float max = JsonUtils.getFloat(obj, "max_force", 0);
            return new Explosive(condition, min, max);
        }
    }

    @SuppressWarnings("unchecked")
    public float getForce(Entity entity) {
        if (condition != null && condition.isCompatible(entity)) {
            return condition.isConditionFulfilled(entity) ? maxForce : minForce;
        }
        return minForce;
    }

    public boolean isExplosive(Entity entity) {
        return getForce(entity) > 0;
    }
}
