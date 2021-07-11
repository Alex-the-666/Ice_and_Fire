package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySeaSerpentArrow extends AbstractArrowEntity {

    public EntitySeaSerpentArrow(EntityType t, World worldIn) {
        super(t, worldIn);
        this.setDamage(3F);
    }

    public EntitySeaSerpentArrow(EntityType t, World worldIn, double x, double y, double z) {
        this(t, worldIn);
        this.setPosition(x, y, z);
        this.setDamage(3F);
    }

    public EntitySeaSerpentArrow(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(IafEntityRegistry.SEA_SERPENT_ARROW, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    public EntitySeaSerpentArrow(EntityType t, World worldIn, LivingEntity shooter) {
        super(t, shooter, worldIn);
        this.setDamage(3F);
    }

    public void tick() {
        super.tick();
        if (world.isRemote && !this.inGround) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            double xRatio = this.getMotion().x * this.getHeight();
            double zRatio = this.getMotion().z * this.getHeight();
            this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + xRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d0 * 10.0D, this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()) - d1 * 10.0D, this.getPosZ() + zRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d2 * 10.0D, d0, d1, d2);
            this.world.addParticle(ParticleTypes.SPLASH, this.getPosX() + xRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d0 * 10.0D, this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()) - d1 * 10.0D, this.getPosZ() + zRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d2 * 10.0D, d0, d1, d2);

        }
    }

    public boolean isInWater() {
        return false;
    }

    protected void damageShield(PlayerEntity player, float damage) {
        if (damage >= 3.0F && player.getActiveItemStack().getItem().isShield(player.getActiveItemStack(), player)) {
            ItemStack copyBeforeUse = player.getActiveItemStack().copy();
            int i = 1 + MathHelper.floor(damage);
            player.getActiveItemStack().damageItem(i, player, (entity) -> {
                entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
            });

            if (player.getActiveItemStack().isEmpty()) {
                Hand Hand = player.getActiveHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, Hand);

                if (Hand == net.minecraft.util.Hand.MAIN_HAND) {
                    this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                }
                player.resetActiveHand();
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(IafItemRegistry.SEA_SERPENT_ARROW);
    }
}
