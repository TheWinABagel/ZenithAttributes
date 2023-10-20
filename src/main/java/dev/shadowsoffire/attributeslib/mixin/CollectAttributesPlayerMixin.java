package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.AttributesLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mixin(value = Player.class, priority = 10)
public class CollectAttributesPlayerMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void zenithCollectPlayerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        List<Attribute> list= new ArrayList<>(cir.getReturnValue().builder.keySet().stream().toList());
        AttributesLib.LOGGER.info("Attributes list: {}", list);
        AttributesLib.playerAttributes = list;
    }

}
