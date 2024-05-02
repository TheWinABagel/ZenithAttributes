package dev.shadowsoffire.attributeslib.mixin.compat;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockStateExtensions;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateExtensionMixin implements BlockStateExtensions {
}
