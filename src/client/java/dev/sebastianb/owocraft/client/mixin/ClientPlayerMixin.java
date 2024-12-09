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

import java.util.Arrays;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {

    public ClientPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }


    @Unique
    private long lastNonGenericTick = -10;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        lastNonGenericTick++;
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {

        // System.out.println(damageSource.getMsgId());
        var damageSourceNamespaceToKey = damageSource.typeHolder().getRegisteredName().split(":");

        // System.out.println(Arrays.toString(damageSourceNamespaceToKey));
        String modID = damageSourceNamespaceToKey[0];
        String key = damageSourceNamespaceToKey[1];

        if ("generic".equals(key) && lastNonGenericTick <= 20) {
            return;
        }

        if (!"generic".equals(key)) {
            // Capture the latest non-generic event tick
            lastNonGenericTick = 0;
        }
        OwocraftClient.getPythonRunnerManager().runPythonScript(modID, key);

    }

    // TODO: on death, stop all events



}
