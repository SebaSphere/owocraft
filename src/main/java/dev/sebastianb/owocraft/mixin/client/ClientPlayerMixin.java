package dev.sebastianb.owocraft.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {


    @Shadow public abstract boolean isUnderWater();

    @Shadow protected abstract boolean isMoving();

    public ClientPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "setExperienceValues", at = @At("HEAD"), cancellable = true)
    public void giveExperiencePoints(float f, int i, int j, CallbackInfo ci) {

        OwocraftClient.getPythonRunnerManager().runPythonScript(
                "minecraft", "event-gained_experience",
                new PairedVariableArgument("playerAge", tickCount)
        );

    }


    // inject into tick
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {

        if (tickCount % 2 == 0) {

            if (this.isInRain()) {

                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "environment-raining",
                        new PairedVariableArgument("playerAge", tickCount)
                );
            }
            if (this.isUnderWater()) {
                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "environment-underwater",
                        new PairedVariableArgument("playerAge", tickCount)
                );
            }
            if (this.isMoving()) {

                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "event-speed",
                        new PairedVariableArgument("playerAge", tickCount),
                        new PairedVariableArgument("playerSpeed", getDeltaMovement().distanceTo(Vec3.ZERO))
                );
            }

            String playerDimension = this.level().dimension().location().toString();
            OwocraftClient.getPythonRunnerManager().runPythonScript(
                    "minecraft", "environment-in_dimension",
                    new PairedVariableArgument("playerAge", tickCount),
                    new PairedVariableArgument("playerDimension", playerDimension)
            );

            // check if player is in nether portal
            if (this.portalProcess != null) {
                if (this.portalProcess.isInsidePortalThisTick()) {

                    OwocraftClient.getPythonRunnerManager().runPythonScript(
                            "minecraft", "event-in_portal",
                            new PairedVariableArgument("playerAge", tickCount)
                    );
                }
            }

        }
    }
}
