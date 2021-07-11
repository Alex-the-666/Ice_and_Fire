package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.event.ServerEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSwingArm {

    public MessageSwingArm() {

    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSwingArm message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(player != null) {
                ServerEvents.onLeftClick(player, player.getHeldItem(Hand.MAIN_HAND));
            }
        }
    }


    public static MessageSwingArm read(PacketBuffer buf) {
        return new MessageSwingArm();
    }

    public static void write(MessageSwingArm message, PacketBuffer buf) {
    }

}
