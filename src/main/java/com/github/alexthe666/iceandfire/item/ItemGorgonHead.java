package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemGorgonHead extends Item implements IUsesTEISR, ICustomRendered {

    public ItemGorgonHead() {
        super(IceAndFire.PROXY.setupISTER(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxDamage(1)));
        this.setRegistryName(IceAndFire.MODID, "gorgon_head");
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entity, int timeLeft) {
        double dist = 32;
        Vector3d Vector3d = entity.getEyePosition(1.0F);
        Vector3d Vector3d1 = entity.getLook(1.0F);
        Vector3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
        double d1 = dist;
        Entity pointedEntity = null;
        List<Entity> list = worldIn.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().expand(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).grow(1.0D, 1.0D, 1.0D), new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
                boolean blindness = entity instanceof LivingEntity && ((LivingEntity) entity).isPotionActive(Effects.BLINDNESS) || (entity instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) entity).canBeTurnedToStone());
                return entity != null && entity.canBeCollidedWith() && !blindness && (entity instanceof PlayerEntity || (entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity)entity)));
            }
        });
        double d2 = d1;
        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(Vector3d, Vector3d2);

            if (axisalignedbb.contains(Vector3d)) {
                if (d2 >= 0.0D) {
                    //pointedEntity = entity1;
                    d2 = 0.0D;
                }
            } else if (optional.isPresent()) {
                double d3 = Vector3d.distanceTo(optional.get());

                if (d3 < d2 || d2 == 0.0D) {
                    if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity1;
                        }
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
                }
            }
        }
        if (pointedEntity != null) {
            if (pointedEntity instanceof LivingEntity) {
                pointedEntity.playSound(IafSoundRegistry.TURN_STONE, 1, 1);
                EntityStoneStatue statue = EntityStoneStatue.buildStatueEntity((LivingEntity) pointedEntity);
                if(pointedEntity instanceof PlayerEntity){
                    pointedEntity.attackEntityFrom(IafDamageRegistry.GORGON_DMG, Integer.MAX_VALUE);
                }else{
                    pointedEntity.remove();
                }
                statue.setPositionAndRotation(pointedEntity.getPosX(), pointedEntity.getPosY(), pointedEntity.getPosZ(), pointedEntity.rotationYaw, pointedEntity.rotationPitch);
                statue.renderYawOffset = pointedEntity.rotationYaw;
                if (!worldIn.isRemote) {
                    worldIn.addEntity(statue);
                }
                if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative()) {
                    stack.shrink(1);
                }

            }
        }
        stack.getTag().putBoolean("Active", false);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        playerIn.setActiveHand(hand);
        itemStackIn.getTag().putBoolean("Active", true);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStackIn);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
    }
}
