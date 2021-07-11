package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageAddChainedEntity {

    public int chainedId;
    public int addedEntityId;

    public MessageAddChainedEntity(int entityId, int addedEntityId) {
        this.chainedId = entityId;
        this.addedEntityId = addedEntityId;
    }

    public MessageAddChainedEntity() {
    }

    public static MessageAddChainedEntity read(PacketBuffer buf) {
        return new MessageAddChainedEntity(buf.readInt(), buf.readInt());
    }

    public static void write(MessageAddChainedEntity message, PacketBuffer buf) {
        buf.writeInt(message.chainedId);
        buf.writeInt(message.addedEntityId);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageAddChainedEntity message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = IceAndFire.PROXY.getClientSidePlayer();
            if (player != null) {
                Entity entity = player.world.getEntityByID(message.chainedId);
                Entity toChain = player.world.getEntityByID(message.addedEntityId);
                if (entity != null && entity instanceof LivingEntity && toChain != null) {
                    ChainEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, ChainEntityProperties.class);
                    if (!properties.connectedEntities.contains(toChain)) {
                        properties.connectedEntities.add(toChain);
                    }
                }
            }
        }
    }

}