package fr.seblor.extrabones;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class CustomBoneBehaviorTypes extends BoneBehaviorType {

    /** Handles all {@code hour_}, {@code hour_s_}, and {@code hour_ss_} bones. */
    public static BoneBehaviorType<ClockHandBoneBehavior> HOUR;
    /** Handles all {@code minute_}, {@code minute_s_}, and {@code minute_ss_} bones. */
    public static BoneBehaviorType<ClockHandBoneBehavior> MINUTE;
    /** Handles all {@code second_}, {@code second_s_}, and {@code second_ss_} bones. */
    public static BoneBehaviorType<ClockHandBoneBehavior> SECOND;

    CustomBoneBehaviorTypes(BehaviorProvider behaviorProvider, BehaviorManagerProvider behaviorManagerProvider, String id, Map requiredArguments, Map optionalArguments, Map dataDeserializer, IRenderType renderType, Set set, Predicate predicate, BehaviorProvider forcedBehaviorProvider, boolean ignoreCubes, boolean pivot) {
        super(behaviorProvider, behaviorManagerProvider, id, requiredArguments, optionalArguments, dataDeserializer, renderType, set, predicate, forcedBehaviorProvider, ignoreCubes, pivot);
    }
}
