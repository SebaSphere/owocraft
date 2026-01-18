package dev.sebastianb.owocraft.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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

        // FIXME: figure out why events that always fire cause other events to flicker


        List<Guardian> guardiansWithinRange = this.level().getEntitiesOfClass(Guardian.class, new AABB(
                this.getX() - 10.0D, this.getY() - 10.0D, this.getZ() - 10.0D,
                this.getX() + 10.0D, this.getY() + 10.0D, this.getZ() + 10.0D));

        for (Guardian guardian : guardiansWithinRange) {
            if (guardian.getActiveAttackTarget() == this) {

                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "event-guardian_target",
                        new PairedVariableArgument("playerAge", tickCount)
                );
                break;
            }
        }

        if (this.isUsingItem()) {
            if (this.useItem.getItem() instanceof PotionItem) {
                boolean shouldRun = OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "event-drinking",
                        new PairedVariableArgument("playerAge", tickCount)
                );
            }
        }

        BlockPos blockpos = this.blockPosition();
        boolean isInRain = this.level().isRainingAt(blockpos) || this.level().isRainingAt(BlockPos.containing((double)blockpos.getX(), this.getBoundingBox().maxY, (double)blockpos.getZ()));
        if (isInRain) {
        // if (this.isInRain()) { // this was a AW, made it not a AW

            boolean shouldRun = OwocraftClient.getPythonRunnerManager().runPythonScript(
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
        if (getDeltaMovement().distanceTo(Vec3.ZERO) > 0.8) {
            OwocraftClient.getPythonRunnerManager().runPythonScript(
                    "minecraft", "event-speed",
                    new PairedVariableArgument("playerAge", tickCount),
                    new PairedVariableArgument("playerSpeed", getDeltaMovement().distanceTo(Vec3.ZERO))
            );
        }
        String playerDimension = this.level().dimension().location().toString();
        // check if player is in nether portal
        if (this.portalProcess != null) {
            if (this.portalProcess.isInsidePortalThisTick()) {

                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "event-in_portal",
                        new PairedVariableArgument("playerAge", tickCount),
                        new PairedVariableArgument("playerDimension", playerDimension)
                );
            } else {
                OwocraftClient.getPythonRunnerManager().runPythonScript(
                        "minecraft", "environment-in_dimension",
                        new PairedVariableArgument("playerAge", tickCount),
                        new PairedVariableArgument("playerDimension", playerDimension)
                );
            }
        } else {
            OwocraftClient.getPythonRunnerManager().runPythonScript(
                    "minecraft", "environment-in_dimension",
                    new PairedVariableArgument("playerAge", tickCount),
                    new PairedVariableArgument("playerDimension", playerDimension)
            );
        }

    }
}
