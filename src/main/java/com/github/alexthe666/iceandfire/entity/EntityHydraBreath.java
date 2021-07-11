package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IDragonProjectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityHydraBreath extends AbstractFireballEntity implements IDragonProjectile {

    private int ticksInAir;

    public EntityHydraBreath(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public EntityHydraBreath(EntityType t, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
    }

    public EntityHydraBreath(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.HYDRA_BREATH, worldIn);
    }

    public EntityHydraBreath(EntityType t, World worldIn, EntityHydra shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.02D;
        this.accelerationY = accelY / d0 * 0.02D;
        this.accelerationZ = accelZ / d0 * 0.02D;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    public float getCollisionBorderSize() {
        return 0F;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }


    public void tick() {
        this.extinguish();
        if(this.ticksExisted > 30){
            this.remove();
        }
        Entity shootingEntity = this.getShooter();
        if (this.world.isRemote || (shootingEntity == null || shootingEntity.isAlive()) && this.world.isBlockLoaded(this.getPosition())) {
            if (this.world.isRemote || (shootingEntity == null || !shootingEntity.removed) && this.world.isBlockLoaded(this.getPosition())) {
                if (this.isFireballFiery()) {
                    this.setFire(1);
                }

                ++this.ticksInAir;
                RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
                if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onImpact(raytraceresult);
                }

                Vector3d Vector3d = this.getMotion();
                double d0 = this.getPosX() + Vector3d.x;
                double d1 = this.getPosY() + Vector3d.y;
                double d2 = this.getPosZ() + Vector3d.z;
                ProjectileHelper.rotateTowardsMovement(this, 0.2F);
                float f = this.getMotionFactor();
                if (this.world.isRemote) {
                    for (int i = 0; i < 15; ++i) {
                        IceAndFire.PROXY.spawnParticle("hydra", this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() - 0.5D, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, 0.1D, 1.0D, 0.1D);
                    }
                }

                this.setMotion(Vector3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale((double)f));
                this.setPosition(d0, d1, d2);
            } else {
                this.remove();
            }
            this.accelerationX *= 0.95F;
            this.accelerationY *= 0.95F;
            this.accelerationZ *= 0.95F;
            this.addVelocity(this.accelerationX, this.accelerationY, this.accelerationZ);
            ++this.ticksInAir;
            Vector3d Vector3d = this.getMotion();
            RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);

            if (raytraceresult != null) {
                this.onImpact(raytraceresult);
            }

            double d0 = this.getPosX() + Vector3d.x;
            double d1 = this.getPosY() + Vector3d.y;
            double d2 = this.getPosZ() + Vector3d.z;
            float f = MathHelper.sqrt(horizontalMag(Vector3d));
            this.rotationYaw = (float) (MathHelper.atan2(Vector3d.x, Vector3d.z) * (double) (180F / (float) Math.PI));
            for (this.rotationPitch = (float) (MathHelper.atan2(Vector3d.y, f) * (double) (180F / (float) Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
            this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
            float f1 = 0.99F;
            float f2 = 0.06F;


            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() - this.getMotion().x * 0.25D, this.getPosY() - this.getMotion().y * 0.25D, this.getPosZ() - this.getMotion().z * 0.25D, this.getMotion().x, this.getMotion().y, this.getMotion().z);
                }

                f = 0.8F;
            }
            this.setPosition(d0, d1, d2);
            this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
        }
    }


    public boolean handleWaterMovement() {
        return true;
    }

    @Override
    protected void onImpact(RayTraceResult movingObject) {
        boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        Entity shootingEntity = this.getShooter();
        if (!this.world.isRemote) {
            if (movingObject.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) movingObject).getEntity();

                if (entity != null && entity instanceof EntityHydraHead) {
                    return;
                }
                if (shootingEntity != null && shootingEntity instanceof EntityHydra) {
                    EntityHydra dragon = (EntityHydra) shootingEntity;
                    if (dragon.isOnSameTeam(entity) || dragon.isEntityEqual(entity)) {
                        return;
                    }
                    entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), 2.0F);
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.POISON, 60, 0));
                    }

                }
            }
        }
    }
}

