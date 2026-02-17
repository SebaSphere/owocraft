package dev.sebastianb.owocraft.networking.common.s2c;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import dev.sebastianb.owocraft.networking.common.IPacket;
import dev.sebastianb.owocraft.services.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;

import java.util.Objects;
import java.util.function.Consumer;

public class PythonEventActivationS2CPacket implements IPacket {


    @Override
    public String packetName() {
        return "python_event_activation";
    }

    @Override
    public Consumer<FriendlyByteBuf> receiver() {
        return (FriendlyByteBuf buf) -> {
            activatePythonEvent(
                    Minecraft.getInstance(), buf
            );
        };
    }

    private static void activatePythonEvent(Minecraft client, FriendlyByteBuf buf) {
        String damageSourceKey = buf.readUtf();
        String damageEntity = buf.readUtf();
        String weaponType = buf.readUtf();
        float damage = buf.readFloat();

        var damageSourceNamespaceToKey = damageSourceKey.split(":");

        String modID = damageSourceNamespaceToKey[0];
        String key = damageSourceNamespaceToKey[1];

        client.execute(() -> {
            OwocraftClient.getPythonRunnerManager().runPythonScript(modID, key,
                    new PairedVariableArgument("damage", damage),
                    new PairedVariableArgument("damageEntity", damageEntity),
                    new PairedVariableArgument("weaponType", weaponType)
            );
        });

    }

    public static void sendPacketToServer(DamageSource damageSource, ServerPlayer serverPlayer, float damage, boolean isEnderpearl) {
        String damageSourceKey = damageSource.getMsgId();



        String damageEntity = damageSource.getEntity() != null ? Objects.requireNonNull(damageSource.getEntity().getType().toString()) : "null";

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());


        if (isEnderpearl) {
            buf.writeUtf("minecraft:ender_pearl_thrown");
        } else {
            buf.writeUtf(damageSourceKey);
        }
        buf.writeUtf(damageEntity);

        if (damageSource.getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity living) {
            if (living.getMainHandItem().getItem() instanceof SwordItem) {
                buf.writeUtf("sword");
            } else if (living.getMainHandItem().getItem() instanceof AxeItem) {
                buf.writeUtf("axe");
            } else {
                buf.writeUtf("hand");
            }
        } else {
            buf.writeUtf("hand");
        }
        buf.writeFloat(damage);


        Services.NETWORKING.sendS2CPacket(serverPlayer, CommonOwocraft.id("python_event_activation"), buf);
    }


}
