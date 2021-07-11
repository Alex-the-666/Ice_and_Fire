package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityAmphithereArrow extends AbstractArrowEntity {

    public EntityAmphithereArrow(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setDamage(2.5F);
    }

    public EntityAmphithereArrow(EntityType type, World worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPosition(x, y, z);
        this.setDamage(2.5F);
    }

    public EntityAmphithereArrow(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(IafEntityRegistry.AMPHITHERE_ARROW, world);
    }

    public EntityAmphithereArrow(EntityType type, LivingEntity shooter, World worldIn) {
        super(type, shooter, worldIn);
        this.setDamage(2.5F);
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if ((ticksExisted == 1 || this.ticksExisted % 70 == 0) && !this.inGround && !this.onGround) {
            this.playSound(IafSoundRegistry.AMPHITHERE_GUST, 1, 1);
        }
        if (world.isRemote && !this.inGround) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            double xRatio = this.getMotion().x * this.getWidth();
            double zRatio = this.getMotion().z * this.getWidth();
            this.world.addParticle(ParticleTypes.CLOUD, this.getPosX() + xRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d0 * 10.0D, this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()) - d1 * 10.0D, this.getPosZ() + zRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d2 * 10.0D, d0, d1, d2);

        }
    }

    protected void arrowHit(LivingEntity living) {
        if (living instanceof PlayerEntity) {
            this.damageShield((PlayerEntity) living, (float) this.getDamage());
        }
        living.isAirBorne = true;
        double xRatio = this.getMotion().x;
        double zRatio = this.getMotion().z;
        float strength = -1.4F;
        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
        living.setMotion(living.getMotion().mul(0.5D, 1, 0.5D).subtract(xRatio / (double) f * (double) strength, 0, zRatio / (double) f * (double) strength).add(0, 0.6, 0));
        spawnExplosionParticle();
    }

    public void spawnExplosionParticle() {
        if (this.world.isRemote) {
            for (int height = 0; height < 1 + rand.nextInt(2); height++) {
                for (int i = 0; i < 20; ++i) {
                    double d0 = this.rand.nextGaussian() * 0.02D;
                    double d1 = this.rand.nextGaussian() * 0.02D;
                    double d2 = this.rand.nextGaussian() * 0.02D;
                    double d3 = 10.0D;
                    double xRatio = this.getMotion().x * this.getWidth();
                    double zRatio = this.getMotion().z * this.getWidth();
                    this.world.addParticle(ParticleTypes.CLOUD, this.getPosX() + xRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d0 * 10.0D, this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()) - d1 * 10.0D, this.getPosZ() + zRatio + (double) (this.rand.nextFloat() * this.getWidth() * 1.0F) - (double) this.getWidth() - d2 * 10.0D, d0, d1, d2);
                }
            }
        } else {
            this.world.setEntityState(this, (byte) 20);
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 20) {
            this.spawnExplosionParticle();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    protected void damageShield(PlayerEntity player, float damage) {
        if (damage >= 3.0F && player.getActiveItemStack().getItem().isShield(player.getActiveItemStack(), player)) {
            ItemStack copyBeforeUse = player.getActiveItemStack().copy();
            int i = 1 + MathHelper.floor(damage);
            player.getActiveItemStack().damageItem(i, player, (playerSheild) -> {
                playerSheild.sendBreakAnimation(playerSheild.getActiveHand());
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
        return new ItemStack(IafItemRegistry.AMPHITHERE_ARROW);
    }
}
