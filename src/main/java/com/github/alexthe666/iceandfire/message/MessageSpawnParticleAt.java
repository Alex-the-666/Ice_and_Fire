package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSpawnParticleAt {
    private double x;
    private double y;
    private double z;
    private int particleType;

    public MessageSpawnParticleAt() {
    }

    public MessageSpawnParticleAt(double x, double y, double z, int particleType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleType = particleType;
    }

    public static MessageSpawnParticleAt read(PacketBuffer buf) {
        return new MessageSpawnParticleAt(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt());
    }

    public static void write(MessageSpawnParticleAt message, PacketBuffer buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
        buf.writeInt(message.particleType);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSpawnParticleAt message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == IafItemRegistry.DRAGON_DEBUG_STICK) {
                    player.world.addParticle(ParticleTypes.SMOKE, message.x, message.y, message.z, 0, 0, 0);
                }
            }
        }
    }
}
