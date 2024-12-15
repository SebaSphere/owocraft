package dev.sebastianb.owocraft.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {

        if (!level().isClientSide) {
            var damageSourceNamespaceToKey = damageSource.typeHolder().getRegisteredName().split(":");

            // System.out.println(Arrays.toString(damageSourceNamespaceToKey));
        }
    }

}
