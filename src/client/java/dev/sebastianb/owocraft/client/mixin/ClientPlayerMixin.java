package dev.sebastianb.owocraft.client.mixin;

import com.mojang.authlib.GameProfile;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {

    public ClientPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }


    @Unique
    boolean hasFallen = false;
    @Unique
    int fallTicks = 10 * 3;
    @Unique
    int MAX_FALL_TICKS = 10 * 3;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {

        if (tickCount % 2 == 0) {

            // Check fall state and handle it
            if (hasFallen) {
                fallTicks--;
                System.out.println(OwocraftClient.getConnectionStateManager().getState());
                System.out.println(fallTicks + " " + MAX_FALL_TICKS);

                double percentage = ((double) fallTicks / MAX_FALL_TICKS) * 50;
                String intensity = String.valueOf(percentage);

                boolean sensationRan = OwocraftClient.getSensationManager()
                        .runSensation("100,0.1," + intensity  + ",0,0,0,Impact");
                System.out.println(intensity);
                if (fallTicks == 0) { // timer completes
                    hasFallen = false; // reset falling state
                    fallTicks = MAX_FALL_TICKS; // reset timer
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource == damageSources().fall()) {
            System.out.println("AUCH");
            hasFallen = true;
            fallTicks = MAX_FALL_TICKS; // reset timer
        }

    }



}
