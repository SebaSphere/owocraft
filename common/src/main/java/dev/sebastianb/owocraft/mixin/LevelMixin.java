package dev.sebastianb.owocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {


    @org.spongepowered.asm.mixin.injection.Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            at = @org.spongepowered.asm.mixin.injection.At("HEAD")
    )
    private void onSetBlock(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
        // your code here
    }
}
