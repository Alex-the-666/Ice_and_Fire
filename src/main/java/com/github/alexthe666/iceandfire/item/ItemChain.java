package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityChainTie;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;

import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemChain extends Item {

    private boolean sticky;

    public ItemChain(boolean sticky) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.sticky = sticky;
        this.setRegistryName(IceAndFire.MODID, sticky ? "chain_sticky" : "chain");
    }

    public static boolean attachToFence(PlayerEntity player, World worldIn, BlockPos fence) {
        EntityChainTie entityleashknot = EntityChainTie.getKnotForPosition(worldIn, fence);
        boolean flag = false;
        double d0 = 30.0D;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();

        for (LivingEntity LivingEntity : worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0))) {
            ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
            if (chainProperties != null && chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity, player)) {
                if (entityleashknot == null) {
                    entityleashknot = EntityChainTie.createKnot(worldIn, fence);
                }
                chainProperties.addChain(LivingEntity, entityleashknot);
                chainProperties.removeChain(LivingEntity, player);
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.chain.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.chain.desc_1").mergeStyle(TextFormatting.GRAY));
        if (sticky) {
            tooltip.add(new TranslationTextComponent("item.iceandfire.chain_sticky.desc_2").mergeStyle(TextFormatting.GREEN));
            tooltip.add(new TranslationTextComponent("item.iceandfire.chain_sticky.desc_3").mergeStyle(TextFormatting.GREEN));
        }
    }

    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(target, ChainEntityProperties.class);
        if (chainProperties != null) {
            if (chainProperties.isConnectedToEntity(target, playerIn) || chainProperties.wasJustDisconnected) {
                chainProperties.wasJustDisconnected = false;
                return ActionResultType.SUCCESS;
            } else {
                if (sticky) {//Old comment: for some reason, this is false for sticky ones
                    double d0 = 60.0D;
                    double i = playerIn.getPosX();
                    double j = playerIn.getPosY();
                    double k = playerIn.getPosZ();
                    boolean flag = false;
                    for (LivingEntity LivingEntity : playerIn.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(i - d0, j - d0, k - d0, i + d0, j + d0, k + d0))) {
                        ChainEntityProperties otherChainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
                        if (otherChainProperties != null && otherChainProperties.isChained() && otherChainProperties.isConnectedToEntity(LivingEntity, playerIn)) {
                            chainProperties.addChain(target, LivingEntity);
                            chainProperties.removeChain(target, playerIn);
                            otherChainProperties.removeChain(target, playerIn);
                            flag = true;
                        }
                    }
                    if (!flag) {
                        chainProperties.addChain(target, playerIn);
                    }
                } else {
                    chainProperties.addChain(target, playerIn);
                }
                if (!playerIn.isCreative()) {
                    stack.shrink(1);
                }
            }
            chainProperties.updateConnectedEntities(target);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        Block block = context.getWorld().getBlockState(context.getPos()).getBlock();

        if (!(block instanceof WallBlock)) {
            return ActionResultType.PASS;
        } else {
            if (!context.getWorld().isRemote) {
                attachToFence(context.getPlayer(), context.getWorld(), context.getPos());
            }
            return ActionResultType.SUCCESS;
        }
    }
}
