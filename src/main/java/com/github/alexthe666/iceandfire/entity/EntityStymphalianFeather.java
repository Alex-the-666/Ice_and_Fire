package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityStymphalianFeather extends AbstractArrowEntity {

    public EntityStymphalianFeather(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public EntityStymphalianFeather(EntityType t, World worldIn, LivingEntity shooter) {
        super(t, shooter, worldIn);
        this.setDamage(IafConfig.stymphalianBirdFeatherAttackStength);
    }

    public EntityStymphalianFeather(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(IafEntityRegistry.STYMPHALIAN_FEATHER, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void remove() {
        super.remove();
        if (IafConfig.stymphalianBirdFeatherDropChance > 0) {
            if (!world.isRemote && this.rand.nextInt(IafConfig.stymphalianBirdFeatherDropChance) == 0) {
                this.entityDropItem(getArrowStack(), 0.1F);
            }
        }

    }

    public void tick() {
        super.tick();
        if (this.ticksExisted > 100) {
            this.remove();
        }
    }

    protected void onEntityHit(EntityRayTraceResult entityHit) {
        Entity shootingEntity = this.getShooter();
        if (shootingEntity instanceof EntityStymphalianBird && entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
            return;
        } else {
            super.onEntityHit(entityHit);
            if (entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
                LivingEntity LivingEntity = (LivingEntity) entityHit.getEntity();
                LivingEntity.setArrowCountInEntity(LivingEntity.getArrowCountInEntity() - 1);
                ItemStack itemstack1 = LivingEntity.isHandActive() ? LivingEntity.getActiveItemStack() : ItemStack.EMPTY;
                if (itemstack1.getItem().isShield(itemstack1, LivingEntity)) {
                    damageShield(LivingEntity, 1.0F);
                }
            }

        }
    }

    protected void damageShield(LivingEntity entity, float damage) {
        if (damage >= 3.0F && entity.getActiveItemStack().getItem().isShield(entity.getActiveItemStack(), entity)) {
            ItemStack copyBeforeUse = entity.getActiveItemStack().copy();
            int i = 1 + MathHelper.floor(damage);
            Hand Hand = entity.getActiveHand();
            copyBeforeUse.damageItem(i, entity, (player1) -> {
                player1.sendBreakAnimation(Hand);
            });
            if (entity.getActiveItemStack().isEmpty()) {
                if (entity instanceof PlayerEntity) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((PlayerEntity) entity, copyBeforeUse, Hand);
                }

                if (Hand == net.minecraft.util.Hand.MAIN_HAND) {
                    this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                }
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER);
    }
}
