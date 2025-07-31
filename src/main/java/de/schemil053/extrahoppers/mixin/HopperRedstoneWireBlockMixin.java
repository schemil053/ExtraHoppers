package de.schemil053.extrahoppers.mixin;

import de.schemil053.extrahoppers.Extrahoppers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedStoneWireBlock.class)
public class HopperRedstoneWireBlockMixin {
    @Redirect(
            method = "canSurviveOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")
    )
    private boolean canSurviveOn(BlockState state, Block block) {
        return state.is(block) || state.is(Extrahoppers.HOPPERS);
    }
}
