package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSetMyrmexHiveNull {

    public MessageSetMyrmexHiveNull() {
    }

    public static MessageSetMyrmexHiveNull read(PacketBuffer buf) {
        return new MessageSetMyrmexHiveNull();
    }

    public static void write(MessageSetMyrmexHiveNull message, PacketBuffer buf) {
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSetMyrmexHiveNull message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if (player != null) {
                IceAndFire.PROXY.setReferencedHive(null);
            }
        }
    }
}