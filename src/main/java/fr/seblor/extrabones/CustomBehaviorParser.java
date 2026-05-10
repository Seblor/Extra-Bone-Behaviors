package fr.seblor.extrabones;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;

import java.util.HashMap;

public class CustomBehaviorParser implements BlockbenchBehaviorParser {

    @Override
    public void processModel(ErrorCollector collector, BlockbenchModel model, ModelBlueprint blueprint) {
    }

    @Override
    public void processBone(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone) {
        BoneBehaviorType<ClockHandBoneBehavior> type = typeForBone(bone.getName());
        if (type != null) {
            bone.getBehaviors().put(type.getId(), new HashMap<>());
        }
    }

    /**
     * Maps a bone name to one of the three registered types (HOUR, MINUTE, SECOND).
     * The smoothing level ({@code _s_} / {@code _ss_}) is resolved later by the
     * behavior itself from the bone name, so all smooth variants share the same type.
     */
    private static BoneBehaviorType<ClockHandBoneBehavior> typeForBone(String name) {
        if (name.startsWith("hour_"))   return CustomBoneBehaviorTypes.HOUR;
        if (name.startsWith("minute_")) return CustomBoneBehaviorTypes.MINUTE;
        if (name.startsWith("second_")) return CustomBoneBehaviorTypes.SECOND;
        return null;
    }
}
