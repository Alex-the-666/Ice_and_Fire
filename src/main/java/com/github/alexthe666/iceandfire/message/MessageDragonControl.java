package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.event.ServerEvents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDragonControl {

    public int dragonId;
    public byte controlState;
    public int armor_type;
    private double posX;
    private double posY;
    private double posZ;

    public MessageDragonControl(int dragonId, byte controlState, double posX, double posY, double posZ) {
        this.dragonId = dragonId;
        this.controlState = controlState;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public MessageDragonControl() {
    }

    public static MessageDragonControl read(PacketBuffer buf) {
        return new MessageDragonControl(buf.readInt(), buf.readByte(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void write(MessageDragonControl message, PacketBuffer buf) {
        buf.writeInt(message.dragonId);
        buf.writeByte(message.controlState);
        buf.writeDouble(message.posX);
        buf.writeDouble(message.posY);
        buf.writeDouble(message.posZ);
    }

    private double getPosX() {
        return posX;
    }

    private double getPosY() {
        return posY;
    }

    private double getPosZ() {
        return posZ;
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageDragonControl message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.dragonId);
                    if (ServerEvents.isRidingOrBeingRiddenBy(entity, player)) {
                        if (entity != null && entity instanceof EntityDragonBase) {
                            EntityDragonBase dragon = (EntityDragonBase) entity;
                            if (dragon.isOwner(player)) {
                                dragon.setControlState(message.controlState);
                            }
                            dragon.setPosition(message.getPosX(), message.getPosY(), message.getPosZ());
                        } else if (entity instanceof EntityHippogryph) {
                            EntityHippogryph hippo = (EntityHippogryph) entity;
                            if (hippo.isOwner(player)) {
                                hippo.setControlState(message.controlState);
                            }
                            hippo.setPosition(message.getPosX(), message.getPosY(), message.getPosZ());
                        } else if (entity instanceof EntityHippocampus) {
                            EntityHippocampus hippo = (EntityHippocampus) entity;
                            if (hippo.isOwner(player)) {
                                hippo.setControlState(message.controlState);
                            }
                            hippo.setPosition(message.getPosX(), message.getPosY(), message.getPosZ());
                        } else if (entity instanceof EntityDeathWorm) {
                            EntityDeathWorm deathworm = (EntityDeathWorm) entity;
                            deathworm.setControlState(message.controlState);
                            deathworm.setPosition(message.getPosX(), message.getPosY(), message.getPosZ());
                        } else if (entity instanceof EntityAmphithere) {
                            EntityAmphithere amphi = (EntityAmphithere) entity;
                            if (amphi.isOwner(player)) {
                                amphi.setControlState(message.controlState);
                            }
                            amphi.setPosition(message.getPosX(), message.getPosY(), message.getPosZ());
                        }
                    }
                }
            }
        }
    }
}