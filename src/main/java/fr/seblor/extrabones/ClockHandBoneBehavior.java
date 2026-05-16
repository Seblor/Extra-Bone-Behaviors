package fr.seblor.extrabones;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class ClockHandBoneBehavior extends AbstractBoneBehavior<ClockHandBoneBehavior> implements ClockHand {

    private final ClockHandMode mode;
    /** Cached quaternion, mutated in place when degrees change — avoids per-tick allocation. */
    private final Quaternionf cachedQuaternion = new Quaternionf();
    /** Last value returned by {@link ClockHandMode#cacheKey()}; {@code Long.MIN_VALUE} forces the first computation. */
    private long lastCacheKey = Long.MIN_VALUE;

    public ClockHandBoneBehavior(ModelBone bone, BoneBehaviorType<ClockHandBoneBehavior> type, BoneBehaviorData data) {
        super(bone, type, data);
        ClockHandMode resolved = ClockHandMode.fromBoneName(bone.getBoneId());
        // Fallback: determine from the registered type id if the bone name alone is ambiguous.
        if (resolved == null) {
            String id = type.getId();
            if (id.startsWith("ClockHour"))        resolved = ClockHandMode.HOUR;
            else if (id.startsWith("ClockMinute")) resolved = ClockHandMode.MINUTE;
            else                                   resolved = ClockHandMode.SECOND;
        }
        this.mode = resolved;
    }

    @Override
    public void preGlobalCalculation() {
        long key = mode.cacheKey();
        if (key != lastCacheKey) {
            lastCacheKey = key;
            float rad = mode.getDegrees() * 0.017453292F;
            cachedQuaternion.identity().rotateZ(-rad);
        }
        // Modify the local transform BEFORE ModelEngine computes the global transform.
        this.bone.getLocalTransform().mutateLeftQuaternion(q -> q.premul((Quaternionfc) cachedQuaternion));
    }
}
