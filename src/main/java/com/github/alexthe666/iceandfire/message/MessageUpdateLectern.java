package com.github.alexthe666.iceandfire.message;

import java.util.function.Supplier;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateLectern {

    public long blockPos;
    public int selectedPages1;
    public int selectedPages2;
    public int selectedPages3;
    public boolean updateStack;
    public int pageOrdinal;

    public MessageUpdateLectern(long blockPos, int selectedPages1, int selectedPages2, int selectedPages3, boolean updateStack, int pageOrdinal) {
        this.blockPos = blockPos;
        this.selectedPages1 = selectedPages1;
        this.selectedPages2 = selectedPages2;
        this.selectedPages3 = selectedPages3;
        this.updateStack = updateStack;
        this.pageOrdinal = pageOrdinal;

    }

    public MessageUpdateLectern() {
    }

    public static MessageUpdateLectern read(PacketBuffer buf) {
        return new MessageUpdateLectern(buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readInt());
    }

    public static void write(MessageUpdateLectern message, PacketBuffer buf) {
        buf.writeLong(message.blockPos);
        buf.writeInt(message.selectedPages1);
        buf.writeInt(message.selectedPages2);
        buf.writeInt(message.selectedPages3);
        buf.writeBoolean(message.updateStack);
        buf.writeInt(message.pageOrdinal);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateLectern message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    BlockPos pos = BlockPos.fromLong(message.blockPos);
                    if (player.world.getTileEntity(pos) != null) {
                        if (player.world.getTileEntity(pos) instanceof TileEntityLectern) {
                            TileEntityLectern lectern = (TileEntityLectern) player.world.getTileEntity(pos);
                            if(message.updateStack){
                                ItemStack bookStack = lectern.getStackInSlot(0);
                                if(bookStack.getItem() == IafItemRegistry.BESTIARY){
                                    EnumBestiaryPages.addPage(EnumBestiaryPages.fromInt(message.pageOrdinal), bookStack);
                                }
                                lectern.randomizePages(bookStack, lectern.getStackInSlot(1));
                            }else{
                                lectern.selectedPages[0] = EnumBestiaryPages.fromInt(message.selectedPages1);
                                lectern.selectedPages[1] = EnumBestiaryPages.fromInt(message.selectedPages2);
                                lectern.selectedPages[2] = EnumBestiaryPages.fromInt(message.selectedPages3);
                            }

                        }
                    }
                }
            }
        }
    }

}