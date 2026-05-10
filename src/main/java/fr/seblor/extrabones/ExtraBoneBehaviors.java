package fr.seblor.extrabones;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import com.ticxo.modelengine.api.events.RegisterBehaviorParserEvent;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class ExtraBoneBehaviors extends JavaPlugin {

    private static ExtraBoneBehaviors instance;

    // Held so we can unregister precisely in onDisable()
    private final Listener parserListenerHandle = new Listener() {};
    private final Listener registrationListenerHandle = new Listener() {};

    public static ExtraBoneBehaviors getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        CustomBoneBehaviorTypes.HOUR   = BoneBehaviorType.Builder.of(ClockHandBoneBehavior::new, null, "ClockHour").build();
        CustomBoneBehaviorTypes.MINUTE = BoneBehaviorType.Builder.of(ClockHandBoneBehavior::new, null, "ClockMinute").build();
        CustomBoneBehaviorTypes.SECOND = BoneBehaviorType.Builder.of(ClockHandBoneBehavior::new, null, "ClockSecond").build();

        // SimplePluginManager.fireEvent() skips listeners where plugin.isEnabled() == false.
        // This plugin isn't enabled yet when ModelEngine fires these events (its onEnable()
        // runs before ours due to depending on ModelEngine).
        // Fix: using ModelEngine's Plugin instance as the owner — it IS enabled when it
        // fires RegisterBehaviorParserEvent / ModelRegistrationEvent from its own onEnable().
        Plugin me = Bukkit.getPluginManager().getPlugin("ModelEngine");

        RegisterBehaviorParserEvent.getHandlerList().register(new RegisteredListener(
                parserListenerHandle,
                (listener, event) -> {
                    if (event instanceof RegisterBehaviorParserEvent e) {
                        e.register(new CustomBehaviorParser());
                    }
                },
                EventPriority.NORMAL,
                me,
                false
        ));

        // ModelRegistrationEvent(PRE_IMPORT) fires right before bones are parsed,
        // after the BoneBehaviorRegistry has been set up, the ideal moment to register our types.
        // ModelRegistrationEvent(POST_IMPORT) fires once models are fully re-parsed; we use it
        // to refresh any active models that have clock-hand bones so stale display entities are
        // replaced with ones built from the updated blueprint.
        ModelRegistrationEvent.getHandlerList().register(new RegisteredListener(
                registrationListenerHandle,
                (listener, event) -> {
                    if (event instanceof ModelRegistrationEvent e) {
                        if (e.getPhase() == ModelGenerator.Phase.PRE_IMPORT) {
                            var registry = ModelEngineAPI.getAPI().getBoneBehaviorRegistry();
                            registry.register(CustomBoneBehaviorTypes.HOUR);
                            registry.register(CustomBoneBehaviorTypes.MINUTE);
                            registry.register(CustomBoneBehaviorTypes.SECOND);
                        } else if (e.getPhase() == ModelGenerator.Phase.POST_IMPORT && isEnabled()) {
                            // POST_IMPORT fires on an async thread; schedule the actual entity
                            // manipulation on the main server thread.
                            Bukkit.getScheduler().runTask(ExtraBoneBehaviors.this,
                                    ExtraBoneBehaviors.this::refreshActiveClockModels);
                        }
                    }
                },
                EventPriority.NORMAL,
                me,
                false
        ));
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        RegisterBehaviorParserEvent.getHandlerList().unregister(parserListenerHandle);
        ModelRegistrationEvent.getHandlerList().unregister(registrationListenerHandle);
    }

    /**
     * For every loaded entity that has an active model containing at least one clock-hand bone,
     * swap it out for a freshly-created model from the updated blueprint.
     * {@link ModeledEntity#addModel} internally calls removeModel for the same blueprint name and
     * returns the old model; we call destroy() on it so ModelEngine despawns its display entities.
     * Must run on the main thread.
     */
    private void refreshActiveClockModels() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
                if (modeledEntity == null) continue;
                // getModels() returns ImmutableMap.copyOf — safe to iterate while we mutate below
                for (Map.Entry<String, ActiveModel> entry : modeledEntity.getModels().entrySet()) {
                    ActiveModel activeModel = entry.getValue();
                    boolean hasClockBones = activeModel.getBones().values().stream().anyMatch(bone ->
                            bone.getBoneBehavior(CustomBoneBehaviorTypes.HOUR).isPresent()
                         || bone.getBoneBehavior(CustomBoneBehaviorTypes.MINUTE).isPresent()
                         || bone.getBoneBehavior(CustomBoneBehaviorTypes.SECOND).isPresent()
                    );
                    if (!hasClockBones) continue;
                    try {
                        ActiveModel fresh = ModelEngineAPI.createActiveModel(entry.getKey());
                        // addModel replaces any existing model with the same blueprint name;
                        // destroy() on the returned old model despawns its display entities.
                        modeledEntity.addModel(fresh, activeModel.isMainHitbox())
                                     .ifPresent(ActiveModel::destroy);
                    } catch (RuntimeException ignored) {
                        // Blueprint was removed or renamed during the reload; nothing to do.
                    }
                }
            }
        }
    }
}
