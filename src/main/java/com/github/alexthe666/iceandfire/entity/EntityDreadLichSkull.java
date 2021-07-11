package com.github.alexthe666.iceandfire.entity;

import java.util.List;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityDreadLichSkull extends AbstractArrowEntity {


    public EntityDreadLichSkull(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setDamage(6F);
    }

    public EntityDreadLichSkull(EntityType type, World worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPosition(x, y, z);
        this.setDamage(6F);
    }

    public EntityDreadLichSkull(EntityType type, World worldIn, LivingEntity shooter, double x, double y, double z) {
        super(type, shooter, worldIn);
        this.setDamage(6);
    }

    public EntityDreadLichSkull(EntityType type, World worldIn, LivingEntity shooter, double dmg) {
        super(type, shooter, worldIn);
        this.setDamage(dmg);
    }

    public EntityDreadLichSkull(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.DREAD_LICH_SKULL, worldIn);
    }

    public boolean isInWater() {
        return false;
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    public void tick() {
        float sqrt = MathHelper.sqrt(this.getMotion().x * this.getMotion().x + this.getMotion().z * this.getMotion().z);
        boolean flag = true;
        Entity shootingEntity = this.getShooter();
        if (shootingEntity != null && shootingEntity instanceof MobEntity && ((MobEntity) shootingEntity).getAttackTarget() != null) {
            LivingEntity target = ((MobEntity) shootingEntity).getAttackTarget();
            double minusX = target.getPosX() - this.getPosX();
            double minusY = target.getPosY() - this.getPosY();
            double minusZ = target.getPosZ() - this.getPosZ();
            double speed = 0.15D;
            this.setMotion(this.getMotion().add(minusX * speed * 0.1D, minusY * speed * 0.1D, minusZ * speed * 0.1D));
        }
        if (shootingEntity instanceof PlayerEntity) {
            LivingEntity target = ((PlayerEntity) shootingEntity).getAttackingEntity();
            if (target == null || !target.isAlive()) {
                double d0 = 10;
                List<Entity> list = world.getEntitiesInAABBexcluding(shootingEntity, (new AxisAlignedBB(this.getPosX(), this.getPosY(), this.getPosZ(), this.getPosX() + 1.0D, this.getPosY() + 1.0D, this.getPosZ() + 1.0D)).grow(d0, 10.0D, d0), EntityPredicates.IS_ALIVE);
                if (!list.isEmpty()) {
                    for(Entity e : list){
                        if(e instanceof LivingEntity && !e.getUniqueID().equals(shootingEntity.getUniqueID())){
                            target = (LivingEntity) e;
                        }
                    }
                }
            }
            if (target != null && target.isAlive()) {
                double minusX = target.getPosX() - this.getPosX();
                double minusY = target.getPosY() + target.getEyeHeight() - this.getPosY();
                double minusZ = target.getPosZ() - this.getPosZ();
                double speed = 0.25D * Math.min(this.getDistance(target), 10D) / 10D;
                this.setMotion(this.getMotion().add((Math.signum(minusX) * 0.5D - this.getMotion().x) * 0.10000000149011612D, (Math.signum(minusY) * 0.5D - this.getMotion().y) * 0.10000000149011612D, (Math.signum(minusZ) * 0.5D - this.getMotion().z) * 0.10000000149011612D));
                this.rotationYaw = (float) (MathHelper.atan2(this.getMotion().x, this.getMotion().z) * (180D / Math.PI));
                this.rotationPitch = (float) (MathHelper.atan2(this.getMotion().y, sqrt) * (180D / Math.PI));
                flag = false;
            }
        }
        if ((sqrt < 0.1F || this.collidedHorizontally || this.collidedVertically || this.inGround) && this.ticksExisted > 5 && flag) {
            this.remove();
        }
        double d0 = 0;
        double d1 = 0.01D;
        double d2 = 0D;
        double x = this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth();
        double y = this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()) - (double) this.getHeight();
        double z = this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth();
        float f = (this.getWidth() + this.getHeight() + this.getWidth()) * 0.333F + 0.5F;
        if (particleDistSq(x, y, z) < f * f) {
            IceAndFire.PROXY.spawnParticle("dread_torch", x, y + 0.5D, z, d0, d1, d2);
        }
        super.tick();
    }

    public double particleDistSq(double toX, double toY, double toZ) {
        double d0 = getPosX() - toX;
        double d1 = getPosY() - toY;
        double d2 = getPosZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (!this.isSilent() && soundIn != SoundEvents.ENTITY_ARROW_HIT && soundIn != SoundEvents.ENTITY_ARROW_HIT_PLAYER) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch);
        }
    }

    protected void onEntityHit(EntityRayTraceResult raytraceResultIn) {
        if (raytraceResultIn.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) raytraceResultIn).getEntity();
            Entity shootingEntity = this.getShooter();
            if (entity != null) {
                if (shootingEntity != null && entity.isOnSameTeam(shootingEntity)) {
                    return;
                }
            }
        }
        super.onEntityHit(raytraceResultIn);
    }

    protected void arrowHit(LivingEntity living) {
        super.arrowHit(living);
        Entity shootingEntity = this.getShooter();
        if (living != null && (shootingEntity == null || !living.isEntityEqual(shootingEntity))) {
            if (living instanceof PlayerEntity) {
                this.damageShield((PlayerEntity) living, (float) this.getDamage());
            }
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

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
