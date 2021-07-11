package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageGetMyrmexHive {

    public CompoundNBT hive;

    public MessageGetMyrmexHive(CompoundNBT hive) {
        this.hive = hive;
    }

    public MessageGetMyrmexHive() {
    }

    public static MessageGetMyrmexHive read(PacketBuffer buf) {
        return new MessageGetMyrmexHive(buf.readCompoundTag());
    }

    public static void write(MessageGetMyrmexHive message, PacketBuffer buf) {
        buf.writeCompoundTag(message.hive);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageGetMyrmexHive message, Supplier<NetworkEvent.Context> context) {
            PlayerEntity player = context.get().getSender();
            MyrmexHive serverHive = MyrmexHive.fromNBT(message.hive);
            CompoundNBT tag = new CompoundNBT();
            serverHive.writeVillageDataToNBT(tag);
            serverHive.readVillageDataFromNBT(tag);
            IceAndFire.PROXY.setReferencedHive(serverHive);
            context.get().setPacketHandled(true);
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }else{
                if(MyrmexWorldData.get(player.world) != null){
                    MyrmexHive realHive = MyrmexWorldData.get(player.world).getHiveFromUUID(serverHive.hiveUUID);
                    realHive.readVillageDataFromNBT(serverHive.toNBT());
                }
            }

        }
    }
}