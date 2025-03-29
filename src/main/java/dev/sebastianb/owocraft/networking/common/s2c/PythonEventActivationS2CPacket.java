package dev.sebastianb.owocraft.networking.common.s2c;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import dev.sebastianb.owocraft.networking.common.IPacket;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketReceiver;
import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.play.ClientPlayContext;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.CauldronBlock;
import org.jetbrains.annotations.NotNull;
import org.python.antlr.ast.Str;

import java.util.Objects;

public class PythonEventActivationS2CPacket implements IPacket {


    @Override
    public String packetName() {
        return "python_event_activation";
    }

    @Override
    public PacketReceiver<ClientPlayContext, FriendlyByteBuf> receiver() {
        return (ClientPlayContext handler, FriendlyByteBuf buf) -> {
            activatePythonEvent(
                    handler.client(), handler, buf
            );
        };
    }

    private static void activatePythonEvent(@NotNull Minecraft client, @NotNull ClientPlayContext handler, @NotNull FriendlyByteBuf buf) {
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
                    new PairedVariableArgument("damageFromEntityType", damageEntity),
                    new PairedVariableArgument("weaponType", weaponType)
            );
        });

    }

    public static void sendPacketToServer(DamageSource damageSource, ServerPlayer serverPlayer, float damage, boolean isEnderpearl) {
        String damageSourceKey = damageSource.typeHolder().getRegisteredName();
        System.out.println(damageSourceKey);

        String damageEntity = damageSource.getEntity() != null ? Objects.requireNonNull(damageSource.getEntity().getType().toShortString()) : "null";

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        if (isEnderpearl) {
            buf.writeUtf("minecraft:ender_pearl_thrown");
        } else {
            buf.writeUtf(damageSourceKey);
        }
        buf.writeUtf(damageEntity);

        if (damageSource.getWeaponItem() != null) {
            if (damageSource.getWeaponItem().getItem() instanceof SwordItem) {
                buf.writeUtf("sword");
            } else if (damageSource.getWeaponItem().getItem() instanceof AxeItem) {
                buf.writeUtf("axe");
            } else {
                buf.writeUtf("hand");
            }
        } else {
            buf.writeUtf("hand");
        }
        buf.writeFloat(damage);




        PacketSender.s2c(serverPlayer).send(Owocraft.id("python_event_activation"), buf);
    }


}
