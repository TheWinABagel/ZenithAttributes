package dev.shadowsoffire.attributeslib.asm;

import java.util.List;

import dev.shadowsoffire.attributeslib.api.client.GatherEffectScreenTooltipsEvent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Contains coremod-injected hooks.
 */
public class ALHooks {

    /**
     * Injected immediately after the following line of code:
     * <code><pre>
     * List&lt;Component&gt; list = List.of(this.getEffectName(mobeffectinstance), MobEffectUtil.formatDuration(mobeffectinstance, 1.0F));
     * </pre></code>
     * This overrides the value of the list to the event-modified tooltip lines.
     * 
     * @param screen     The screen rendering the tooltip.
     * @param effectInst The effect instance whose tooltip is being rendered.
     * @param tooltip    The existing tooltip lines, which consist of the name and the duration.
     * @return The new tooltip lines, modified by the event.
     *///TODO redo as mixin?
    public static List<Component> getEffectTooltip(EffectRenderingInventoryScreen<?> screen, MobEffectInstance effectInst, List<Component> tooltip) {
        //MinecraftForge.EVENT_BUS.post(event);

        //    return event.getTooltip();
        return null;
    }

}
