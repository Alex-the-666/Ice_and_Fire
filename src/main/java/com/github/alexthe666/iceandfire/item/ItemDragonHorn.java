package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemDragonHorn extends Item {

    public ItemDragonHorn() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, "dragon_horn");
    }

    public static int getDragonType(ItemStack stack) {
        if (stack.getTag() != null) {
            String id = stack.getTag().getString("DragonHornEntityID");
            if (EntityType.byKey(id).isPresent()) {
                EntityType entityType = EntityType.byKey(id).get();
                if (entityType == IafEntityRegistry.FIRE_DRAGON) {
                    return 1;
                }
                if (entityType == IafEntityRegistry.ICE_DRAGON) {
                    return 2;
                }
                if (entityType == IafEntityRegistry.LIGHTNING_DRAGON) {
                    return 3;
                }
            }
        }
        return 0;
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        ItemStack trueStack = playerIn.getHeldItem(hand);
        if(!playerIn.world.isRemote){
            if (target instanceof EntityDragonBase && ((EntityDragonBase) target).isOwner(playerIn) && (trueStack.getTag() == null || trueStack.getTag() != null && trueStack.getTag().getCompound("EntityTag").isEmpty())) {
                CompoundNBT entityTag = new CompoundNBT();
                target.writeAdditional(entityTag);
                CompoundNBT newTag = new CompoundNBT();
                newTag.putString("DragonHornEntityID", Registry.ENTITY_TYPE.getKey(target.getType()).toString());
                newTag.put("EntityTag", entityTag);
                newTag.putUniqueId("EntityUUID", target.getUniqueID());
                trueStack.setTag(newTag);
                playerIn.swingArm(hand);
                playerIn.world.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 3, 0.75F);
                target.remove();
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getFace() != Direction.UP) {
            return ActionResultType.FAIL;
        } else {
            ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
            if (stack.getTag() != null && !stack.getTag().getString("DragonHornEntityID").isEmpty()) {
                World world = context.getWorld();
                String id = stack.getTag().getString("DragonHornEntityID");
                EntityType type = EntityType.byKey(id).orElse(null);
                if (type != null) {
                    Entity entity = type.create(world);
                    if (entity instanceof EntityDragonBase) {
                        EntityDragonBase dragon = (EntityDragonBase) entity;
                        dragon.readAdditional(stack.getTag().getCompound("EntityTag"));
                    }
                    if(stack.getTag().hasUniqueId("EntityUUID")){
                        entity.setUniqueId(stack.getTag().getUniqueId("EntityUUID"));
                    }
                    entity.setLocationAndAngles(context.getPos().getX() + 0.5, context.getPos().getY() + 1, context.getPos().getZ() + 0.5, context.getPlayer().rotationYaw, 0);
                    if (world.addEntity(entity)) {
                        stack.setTag(new CompoundNBT());
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getTag() != null) {
            CompoundNBT entityTag = stack.getTag().getCompound("EntityTag");
            if(!entityTag.isEmpty()) {
                String id = stack.getTag().getString("DragonHornEntityID");
                if(EntityType.byKey(id).isPresent()){
                    EntityType type = EntityType.byKey(id).get();
                    tooltip.add(new TranslationTextComponent(type.getTranslationKey()).mergeStyle(getTextColorForEntityType(type)));
                    String name = new TranslationTextComponent("dragon.unnamed").getString();
                    if(!entityTag.getString("CustomName").isEmpty()){
                        IFormattableTextComponent component = ITextComponent.Serializer.getComponentFromJson(entityTag.getString("CustomName"));
                        if(component != null){
                            name = component.getString();
                        }
                    }
                    tooltip.add(new StringTextComponent(name).mergeStyle(TextFormatting.GRAY));
                    String gender = new TranslationTextComponent("dragon.gender").getString() + " " + new TranslationTextComponent((entityTag.getBoolean("Gender") ? "dragon.gender.male" : "dragon.gender.female")).getString();
                    tooltip.add(new StringTextComponent(gender).mergeStyle(TextFormatting.GRAY));
                    int stagenumber = entityTag.getInt("AgeTicks") / 24000;
                    int stage1 = 0;
                    if (stagenumber >= 100) {
                        stage1 = 5;
                    } else if (stagenumber >= 75) {
                        stage1 = 4;
                    } else if (stagenumber >= 50) {
                        stage1 = 3;
                    } else if (stagenumber >= 25) {
                        stage1 = 2;
                    } else {
                        stage1 = 1;
                    }
                    String stage = new TranslationTextComponent("dragon.stage").getString() + " " + stage1 + " " + new TranslationTextComponent("dragon.days.front").getString() + stagenumber + " " + new TranslationTextComponent("dragon.days.back").getString();
                    tooltip.add(new StringTextComponent(stage).mergeStyle(TextFormatting.GRAY));
                }
            }

        }
    }

    private TextFormatting getTextColorForEntityType(EntityType type) {
        if(type == IafEntityRegistry.FIRE_DRAGON){
            return TextFormatting.DARK_RED;
        }
        if(type == IafEntityRegistry.ICE_DRAGON){
            return TextFormatting.BLUE;
        }
        if(type == IafEntityRegistry.LIGHTNING_DRAGON){
            return TextFormatting.DARK_PURPLE;
        }
        return TextFormatting.GRAY;
    }
}
