package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemDeathwormGauntlet extends Item implements ICustomRendered {

    public ItemDeathwormGauntlet(String color) {
        super(IceAndFire.PROXY.setupISTER(new Item.Properties().maxDamage(500).group(IceAndFire.TAB_ITEMS)));
        this.setRegistryName(IceAndFire.MODID, "deathworm_gauntlet_" + color);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        playerIn.setActiveHand(hand);
        return new ActionResult<ItemStack>(ActionResultType.PASS, itemStackIn);
    }

    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(player, MiscEntityProperties.class);
        if (stack.getTag() != null && properties != null) {
            if (properties.deathwormReceded || properties.deathwormLaunched) {
                return;
            } else {
                if (player instanceof PlayerEntity) {
                    if (stack.getTag().getInt("HolderID") != player.getEntityId()) {
                        stack.getTag().putInt("HolderID", player.getEntityId());
                    }
                    if (((PlayerEntity) player).getCooldownTracker().getCooldown(this, 0.0F) == 0) {
                        ((PlayerEntity) player).getCooldownTracker().setCooldown(this, 10);
                        player.playSound(IafSoundRegistry.DEATHWORM_ATTACK, 1F, 1F);
                        properties.deathwormReceded = false;
                        properties.deathwormLaunched = true;
                    }
                }
            }
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity LivingEntity, int timeLeft) {
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, MiscEntityProperties.class);
        if (properties != null && properties.specialWeaponDmg > 0) {
            stack.damageItem(properties.specialWeaponDmg, LivingEntity, (player) -> {
                player.sendBreakAnimation(LivingEntity.getActiveHand());
            });
            properties.specialWeaponDmg = 0;
        }
        if (stack.getTag().getInt("HolderID") != -1) {
            stack.getTag().putInt("HolderID", -1);
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        boolean hitMob = false;
        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
        } else {
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, MiscEntityProperties.class);
            if (properties != null) {
                if (properties.deathwormReceded) {
                    if (properties.deathwormLungeTicks > 0) {
                        properties.deathwormLungeTicks = properties.deathwormLungeTicks - 4;
                    }
                    if (properties.deathwormLungeTicks <= 0) {
                        properties.deathwormLungeTicks = 0;
                        properties.deathwormReceded = false;
                        properties.deathwormLaunched = false;
                    }
                } else if (properties.deathwormLaunched) {
                    properties.deathwormLungeTicks = 4 + properties.deathwormLungeTicks;
                    if (properties.deathwormLungeTicks > 20 && !properties.deathwormReceded) {
                        properties.deathwormReceded = true;
                    }
                }

                if (properties.prevDeathwormLungeTicks == 20) {
                    if (entity instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) entity;
                        Vector3d Vector3d = player.getLook(1.0F).normalize();
                        double range = 5;
                        for (MobEntity LivingEntity : world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(player.getPosX() - range, player.getPosY() - range, player.getPosZ() - range, player.getPosX() + range, player.getPosY() + range, player.getPosZ() + range))) {
                            Vector3d Vector3d1 = new Vector3d(LivingEntity.getPosX() - player.getPosX(), LivingEntity.getPosY() - player.getPosY(), LivingEntity.getPosZ() - player.getPosZ());
                            double d0 = Vector3d1.length();
                            Vector3d1 = Vector3d1.normalize();
                            double d1 = Vector3d.dotProduct(Vector3d1);
                            boolean canSee = d1 > 1.0D - 0.5D / d0 && player.canEntityBeSeen(LivingEntity);
                            if (canSee) {
                                properties.specialWeaponDmg++;
                                LivingEntity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) entity), 3F);
                                LivingEntity.applyKnockback( 0.5F, LivingEntity.getPosX() - player.getPosX(), LivingEntity.getPosZ() - player.getPosZ());
                            }
                        }
                    }
                }
                properties.prevDeathwormLungeTicks = properties.deathwormLungeTicks;
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.deathworm_gauntlet.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.deathworm_gauntlet.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}
