package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
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

public class ItemCockatriceScepter extends Item {

    public ItemCockatriceScepter() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxDamage(700));
        this.setRegistryName(IceAndFire.MODID, "cockatrice_scepter");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.cockatrice_scepter.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.cockatrice_scepter.desc_1").mergeStyle(TextFormatting.GRAY));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity LivingEntity, int timeLeft) {
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, MiscEntityProperties.class);
        if (properties != null && properties.specialWeaponDmg > 0) {
            stack.damageItem(properties.specialWeaponDmg, LivingEntity, (player) -> {
                player.sendBreakAnimation(LivingEntity.getActiveHand());
            });
            properties.specialWeaponDmg = 0;
            for (Entity e : properties.entitiesWeAreGlaringAt) {
                MiscEntityProperties theirProp = EntityPropertiesHandler.INSTANCE.getProperties(e, MiscEntityProperties.class);
                theirProp.glarers.remove(LivingEntity);
            }
            properties.entitiesWeAreGlaringAt.clear();
        }
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
        if (properties != null) {
            if (player instanceof PlayerEntity) {
                double dist = 32;
                Vector3d Vector3d = player.getEyePosition(1.0F);
                Vector3d Vector3d1 = player.getLook(1.0F);
                Vector3d Vector3d2 = Vector3d.add(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist);
                double d1 = dist;
                Entity pointedEntity = null;
                List<Entity> list = player.world.getEntitiesInAABBexcluding(player, player.getBoundingBox().expand(Vector3d1.x * dist, Vector3d1.y * dist, Vector3d1.z * dist).grow(1.0D, 1.0D, 1.0D), new Predicate<Entity>() {
                    @Override
                    public boolean test(Entity entity) {
                        boolean blindness = entity instanceof LivingEntity && ((LivingEntity) entity).isPotionActive(Effects.BLINDNESS) || (entity instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) entity).canBeTurnedToStone());
                        return entity != null && entity.canBeCollidedWith() && !blindness && (entity instanceof PlayerEntity || (entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)));
                    }
                });
                double d2 = d1;
                for (int j = 0; j < list.size(); ++j) {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
                    Optional<Vector3d> optional = axisalignedbb.rayTrace(Vector3d, Vector3d2);

                    if (axisalignedbb.contains(Vector3d)) {
                        if (d2 >= 0.0D) {
                            pointedEntity = entity1;
                            d2 = 0.0D;
                        }
                    } else if (optional.isPresent()) {
                        double d3 = Vector3d.distanceTo(optional.get());

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1.getLowestRidingEntity() == player.getLowestRidingEntity() && !player.canRiderInteract()) {
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
                        MiscEntityProperties theirProperties = EntityPropertiesHandler.INSTANCE.getProperties(pointedEntity, MiscEntityProperties.class);
                        theirProperties.isBeingGlaredAt = true;
                        if (!theirProperties.glarers.contains(player) && !properties.entitiesWeAreGlaringAt.contains(pointedEntity)) {
                            theirProperties.glarers.add(player);
                            properties.entitiesWeAreGlaringAt.add(pointedEntity);
                        }
                    }
                }
            }
        }
    }

}
