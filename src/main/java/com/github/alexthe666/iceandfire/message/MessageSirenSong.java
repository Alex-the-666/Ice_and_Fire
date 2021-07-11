package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSirenSong {

    public int sirenId;
    public boolean isSinging;

    public MessageSirenSong(int sirenId, boolean isSinging) {
        this.sirenId = sirenId;
        this.isSinging = isSinging;
    }

    public MessageSirenSong() {
    }

    public static MessageSirenSong read(PacketBuffer buf) {
        return new MessageSirenSong(buf.readInt(), buf.readBoolean());
    }

    public static void write(MessageSirenSong message, PacketBuffer buf) {
        buf.writeInt(message.sirenId);
        buf.writeBoolean(message.isSinging);
    }


    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSirenSong message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null && player.world != null) {
                Entity entity = player.world.getEntityByID(message.sirenId);
                if (entity != null && entity instanceof EntitySiren) {
                    EntitySiren siren = (EntitySiren) entity;
                    siren.setSinging(message.isSinging);
                }
            }
        }
    }

}