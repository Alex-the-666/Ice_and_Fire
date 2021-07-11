package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHydra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessagePlayerHitMultipart {
    public int creatureID;
    public int extraData;

    public MessagePlayerHitMultipart(int creatureID) {
        this.creatureID = creatureID;
        this.extraData = 0;
    }

    public MessagePlayerHitMultipart(int creatureID, int extraData) {
        this.creatureID = creatureID;
        this.extraData = extraData;
    }

    public MessagePlayerHitMultipart() {
    }

    public static MessagePlayerHitMultipart read(PacketBuffer buf) {
        return new MessagePlayerHitMultipart(buf.readInt(), buf.readInt());
    }

    public static void write(MessagePlayerHitMultipart message, PacketBuffer buf) {
        buf.writeInt(message.creatureID);
        buf.writeInt(message.extraData);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessagePlayerHitMultipart message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.creatureID);
                    if (entity != null && entity instanceof LivingEntity) {
                        double dist = player.getDistance(entity);
                        LivingEntity mob = (LivingEntity) entity;
                        if (dist < 100) {
                            player.attackTargetEntityWithCurrentItem(mob);
                            if (mob instanceof EntityHydra) {
                                ((EntityHydra) mob).triggerHeadFlags(message.extraData);
                            }
                        }
                    }
                }
            }
        }
    }
}
