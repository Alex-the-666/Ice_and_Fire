package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.world.DragonPosWorldData;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemSummoningCrystal extends Item {


    public ItemSummoningCrystal(String variant) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, "summoning_crystal_" + variant);

    }

    public static boolean hasDragon(ItemStack stack) {
        if (stack.getItem() instanceof ItemSummoningCrystal && stack.getTag() != null) {
            for (String tagInfo : stack.getTag().keySet()) {
                if (tagInfo.contains("Dragon")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
    }

    public ItemStack onItemUseFinish(World worldIn, LivingEntity LivingEntity) {
        return new ItemStack(this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        boolean flag = false;
        String desc = "entity.firedragon.name";
        if(stack.getItem() == IafItemRegistry.SUMMONING_CRYSTAL_ICE){
            desc = "entity.icedragon.name";
        }
        if(stack.getItem() == IafItemRegistry.SUMMONING_CRYSTAL_LIGHTNING){
            desc = "entity.lightningdragon.name";
        }
        if (stack.getTag() != null) {
            for (String tagInfo : stack.getTag().keySet()) {
                if (tagInfo.contains("Dragon")) {
                    CompoundNBT draginTag = stack.getTag().getCompound(tagInfo);
                    String dragonName = new TranslationTextComponent(desc ).getUnformattedComponentText();
                    if (!draginTag.getString("CustomName").isEmpty()) {
                        dragonName = draginTag.getString("CustomName");
                    }
                    tooltip.add(new TranslationTextComponent("item.iceandfire.summoning_crystal.bound", dragonName).mergeStyle(TextFormatting.GRAY));
                    flag = true;
                }
            }
        }
        if (!flag) {
            tooltip.add(new TranslationTextComponent("item.iceandfire.summoning_crystal.desc_0").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("item.iceandfire.summoning_crystal.desc_1").mergeStyle(TextFormatting.GRAY));

        }

    }

    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        boolean flag = false;
        BlockPos offsetPos = context.getPos().offset(context.getFace());
        float yaw = context.getPlayer().rotationYaw;
        boolean displayError = false;
        if (stack.getItem() == this && hasDragon(stack)) {
            int dragonCount = 0;
            if (stack.getTag() != null) {
                for (String tagInfo : stack.getTag().keySet()) {
                    if (tagInfo.contains("Dragon")) {
                        dragonCount++;
                        CompoundNBT dragonTag = stack.getTag().getCompound(tagInfo);
                        UUID id = dragonTag.getUniqueId("DragonUUID");
                        if (id != null) {
                            if (!context.getWorld().isRemote) {
                                try {
                                    Entity entity = context.getWorld().getServer().getWorld(context.getPlayer().world.getDimensionKey()).getEntityByUuid(id);
                                    if (entity != null) {
                                        flag = true;
                                        summonEntity(entity, context.getWorld(), offsetPos, yaw);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    displayError = true;
                                }
                                // ForgeChunkManager.Ticket ticket = null;
                                DragonPosWorldData data = DragonPosWorldData.get(context.getWorld());
                                BlockPos dragonChunkPos = null;
                                if (data != null) {
                                    dragonChunkPos = data.getDragonPos(id);
                                }
                                if (IafConfig.chunkLoadSummonCrystal) {
                                    try {
                                        boolean flag2 = false;
                                        if (!flag) {//server side but couldn't find dragon
                                            if (data != null) {
                                                if(context.getWorld().isRemote){
                                                    ServerWorld serverWorld = (ServerWorld)context.getWorld();
                                                    ChunkPos pos = new ChunkPos(dragonChunkPos);
                                                    serverWorld.forceChunk(pos.x, pos.z, true);
                                                }
                                                /*ticket = ForgeChunkManager.requestPlayerTicket(IceAndFire.INSTANCE, player.getName(), worldIn, ForgeChunkManager.Type.NORMAL);
                                                if (ticket != null) {
                                                    if (dragonChunkPos != null) {
                                                        ForgeChunkManager.forceChunk(ticket, new ChunkPos(dragonChunkPos));
                                                    } else {
                                                        displayError = true;
                                                    }
                                                    lastChunkTicket = ticket;
                                                    flag2 = true;
                                                }*/
                                            }
                                        }
                                        if (flag2) {
                                            try {
                                                Entity entity = context.getWorld().getServer().getWorld(context.getPlayer().world.getDimensionKey()).getEntityByUuid(id);
                                                if (entity != null) {
                                                    flag = true;
                                                    summonEntity(entity, context.getWorld(), offsetPos, yaw);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                       /* if (flag && lastChunkTicket != null && dragonChunkPos != null) {
                                            ForgeChunkManager.releaseTicket(lastChunkTicket);
                                            lastChunkTicket = null;
                                        }*/
                                    } catch (Exception e) {
                                        IceAndFire.LOGGER.warn("Could not load chunk when summoning dragon");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (flag) {
                context.getPlayer().playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                context.getPlayer().playSound(SoundEvents.BLOCK_GLASS_BREAK, 1, 1);
                context.getPlayer().swingArm(context.getHand());
                context.getPlayer().sendStatusMessage(new TranslationTextComponent("message.iceandfire.dragonTeleport"), true);
                stack.setTag(new CompoundNBT());
            } else if (displayError) {
                context.getPlayer().sendStatusMessage(new TranslationTextComponent("message.iceandfire.noDragonTeleport"), true);

            }
        }
        return ActionResultType.PASS;
    }

    public void summonEntity(Entity entity, World worldIn, BlockPos offsetPos, float yaw) {
        entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, yaw, 0);
        if (entity instanceof EntityDragonBase) {
            ((EntityDragonBase) entity).setCrystalBound(false);
        }
        if (IafConfig.chunkLoadSummonCrystal) {
            DragonPosWorldData data = DragonPosWorldData.get(worldIn);
            if (data != null) {
                data.removeDragon(entity.getUniqueID());
            }
        }
    }


}
