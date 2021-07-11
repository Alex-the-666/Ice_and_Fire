package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.ISyncMount;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageStartRidingMob {

    public int dragonId;
    public boolean ride;
    public boolean baby;

    public MessageStartRidingMob(int dragonId, boolean ride, boolean baby) {
        this.dragonId = dragonId;
        this.ride = ride;
        this.baby = baby;
    }

    public MessageStartRidingMob() {
    }

    public static MessageStartRidingMob read(PacketBuffer buf) {
        return new MessageStartRidingMob(buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void write(MessageStartRidingMob message, PacketBuffer buf) {
        buf.writeInt(message.dragonId);
        buf.writeBoolean(message.ride);
        buf.writeBoolean(message.baby);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageStartRidingMob message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.dragonId);
                    if (entity != null && entity instanceof ISyncMount && entity instanceof TameableEntity) {
                        TameableEntity dragon = (TameableEntity) entity;
                        if (dragon.isOwner(player) && dragon.getDistance(player) < 14) {
                            if (message.ride) {
                                if (message.baby) {
                                    dragon.startRiding(player, true);
                                } else {
                                    player.startRiding(dragon, true);
                                }
                            } else {
                                if (message.baby) {
                                    dragon.stopRiding();
                                } else {
                                    player.stopRiding();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}