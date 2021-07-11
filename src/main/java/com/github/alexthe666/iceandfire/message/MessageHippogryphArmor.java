package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageHippogryphArmor {

    public int dragonId;
    public int slot_index;
    public int armor_type;

    public MessageHippogryphArmor(int dragonId, int slot_index, int armor_type) {
        this.dragonId = dragonId;
        this.slot_index = slot_index;
        this.armor_type = armor_type;
    }

    public MessageHippogryphArmor() {
    }

    public static MessageHippogryphArmor read(PacketBuffer buf) {
        return new MessageHippogryphArmor(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(MessageHippogryphArmor message, PacketBuffer buf) {
        buf.writeInt(message.dragonId);
        buf.writeInt(message.slot_index);
        buf.writeInt(message.armor_type);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHippogryphArmor message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.dragonId);
                    if (entity != null && entity instanceof EntityHippogryph) {
                        EntityHippogryph hippo = (EntityHippogryph) entity;
                        if (message.slot_index == 0) {
                            hippo.setSaddled(message.armor_type == 1);
                        }
                        if (message.slot_index == 1) {
                            hippo.setChested(message.armor_type == 1);
                        }
                        if (message.slot_index == 2) {
                            hippo.setArmor(message.armor_type);
                        }
                    }
                    if (entity != null && entity instanceof EntityHippocampus) {
                        EntityHippocampus hippo = (EntityHippocampus) entity;
                        if (message.slot_index == 0) {
                            hippo.setSaddled(message.armor_type == 1);
                        }
                        if (message.slot_index == 1) {
                            hippo.setChested(message.armor_type == 1);
                        }
                        if (message.slot_index == 2) {
                            hippo.setArmor(message.armor_type);
                        }
                    }
                }
            }
        }
    }
}