package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdatePixieHouse {

    public long blockPos;
    public boolean hasPixie;
    public int pixieType;

    public MessageUpdatePixieHouse(long blockPos, boolean hasPixie, int pixieType) {
        this.blockPos = blockPos;
        this.hasPixie = hasPixie;
        this.pixieType = pixieType;

    }

    public MessageUpdatePixieHouse() {
    }

    public static MessageUpdatePixieHouse read(PacketBuffer buf) {
        return new MessageUpdatePixieHouse(buf.readLong(), buf.readBoolean(), buf.readInt());
    }

    public static void write(MessageUpdatePixieHouse message, PacketBuffer buf) {
        buf.writeLong(message.blockPos);
        buf.writeBoolean(message.hasPixie);
        buf.writeInt(message.pixieType);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdatePixieHouse message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    BlockPos pos = BlockPos.fromLong(message.blockPos);
                    if (player.world.getTileEntity(pos) != null && player.world.getTileEntity(pos) instanceof TileEntityPixieHouse) {
                        TileEntityPixieHouse house = (TileEntityPixieHouse) player.world.getTileEntity(pos);
                        house.hasPixie = message.hasPixie;
                        house.pixieType = message.pixieType;
                    } else if (player.world.getTileEntity(pos) != null && player.world.getTileEntity(pos) instanceof TileEntityJar) {
                        TileEntityJar jar = (TileEntityJar) player.world.getTileEntity(pos);
                        jar.hasPixie = message.hasPixie;
                        jar.pixieType = message.pixieType;
                    }
                }
            }
        }
    }

}