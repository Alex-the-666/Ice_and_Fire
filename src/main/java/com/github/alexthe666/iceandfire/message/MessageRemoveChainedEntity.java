package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageRemoveChainedEntity {

    public int chainedId;
    public int RemoveedEntityId;

    public MessageRemoveChainedEntity(int entityId, int RemoveedEntityId) {
        this.chainedId = entityId;
        this.RemoveedEntityId = RemoveedEntityId;
    }

    public MessageRemoveChainedEntity() {
    }

    public static MessageRemoveChainedEntity read(PacketBuffer buf) {
        return new MessageRemoveChainedEntity(buf.readInt(), buf.readInt());
    }

    public static void write(MessageRemoveChainedEntity message, PacketBuffer buf) {
        buf.writeInt(message.chainedId);
        buf.writeInt(message.RemoveedEntityId);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageRemoveChainedEntity message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world != null) {
                    Entity entity = player.world.getEntityByID(message.chainedId);
                    Entity toChain = player.world.getEntityByID(message.RemoveedEntityId);
                    if (entity != null && entity instanceof LivingEntity && toChain != null) {
                        ChainEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, ChainEntityProperties.class);
                        properties.connectedEntities.remove(toChain);
                    }
                }
            }
        }
    }
}