package dev.shadowsoffire.attributeslib.api.client;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * This event is called when a {@link EffectRenderingInventoryScreen} draws the tooltip lines for a hovered {@link MobEffectInstance}.<br>
 * It can be used to modify the tooltip.
 */
public class GatherEffectScreenTooltipsEvent {

    public static final Event<GatherTooltips> GATHER_TOOLTIPS = EventFactory.createArrayBacked(GatherTooltips.class, callbacks -> (event) -> {
        for (GatherTooltips callback : callbacks) {
            callback.gatherTooltips(event);
        }
    });

    @FunctionalInterface
    public interface GatherTooltips {
        public void gatherTooltips(GatherEffectTooltipsEvent event);
    }

    public static class GatherEffectTooltipsEvent {

        protected final EffectRenderingInventoryScreen<?> screen;
        protected final MobEffectInstance effectInst;
        protected final List<Component> tooltip;

        public GatherEffectTooltipsEvent(EffectRenderingInventoryScreen<?> screen, MobEffectInstance effectInst, List<Component> tooltip) {
            this.screen = screen;
            this.effectInst = effectInst;
            this.tooltip = new ArrayList<>(tooltip);
        }

        /**
         * @return The screen which will be rendering the tooltip lines.
         */
        public EffectRenderingInventoryScreen<?> getScreen() {
            return this.screen;
        }

        /**
         * @return The effect whose tooltip is being drawn.
         */
        public MobEffectInstance getEffectInstance() {
            return this.effectInst;
        }

        /**
         * @return A mutable list of tooltip lines.
         */
        public List<Component> getTooltip() {
            return this.tooltip;
        }
    }
}
