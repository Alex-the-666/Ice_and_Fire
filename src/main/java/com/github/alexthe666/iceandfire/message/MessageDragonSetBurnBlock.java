package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDragonSetBurnBlock {

    public int dragonId;
    public boolean breathingFire;
    public int posX;
    public int posY;
    public int posZ;

    public MessageDragonSetBurnBlock(int dragonId, boolean breathingFire, BlockPos pos) {
        this.dragonId = dragonId;
        this.breathingFire = breathingFire;
        posX = pos.getX();
        posY = pos.getY();
        posZ = pos.getZ();
    }

    public MessageDragonSetBurnBlock() {
    }


    public static MessageDragonSetBurnBlock read(PacketBuffer buf) {
        return new MessageDragonSetBurnBlock(buf.readInt(), buf.readBoolean(), new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public static void write(MessageDragonSetBurnBlock message, PacketBuffer buf) {
        buf.writeInt(message.dragonId);
        buf.writeBoolean(message.breathingFire);
        buf.writeInt(message.posX);
        buf.writeInt(message.posY);
        buf.writeInt(message.posZ);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageDragonSetBurnBlock message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    if (player.world != null) {
                        Entity entity = player.world.getEntityByID(message.dragonId);
                        if (entity != null && entity instanceof EntityDragonBase) {
                            EntityDragonBase dragon = (EntityDragonBase) entity;
                            dragon.setBreathingFire(message.breathingFire);
                            dragon.burningTarget = new BlockPos(message.posX, message.posY, message.posZ);
                        }
                    }
                }
            }
        }
    }
}