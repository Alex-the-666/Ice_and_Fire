package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityPixieCharge extends AbstractFireballEntity {

    public int ticksInAir;
    private float[] rgb;

    public EntityPixieCharge(EntityType t, World worldIn) {
        super(t, worldIn);
        rgb = EntityPixie.PARTICLE_RGB[rand.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }


    public EntityPixieCharge(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.PIXIE_CHARGE, worldIn);
    }

    public EntityPixieCharge(EntityType t, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
        double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.07D;
        this.accelerationY = accelY / d0 * 0.07D;
        this.accelerationZ = accelZ / d0 * 0.07D;
        rgb = EntityPixie.PARTICLE_RGB[rand.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    public EntityPixieCharge(EntityType t, World worldIn, PlayerEntity shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.07D;
        this.accelerationY = accelY / d0 * 0.07D;
        this.accelerationZ = accelZ / d0 * 0.07D;
        rgb = EntityPixie.PARTICLE_RGB[rand.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public void tick() {
        Entity shootingEntity = this.getShooter();
        if (this.world.isRemote) {
            for (int i = 0; i < 5; ++i) {
                IceAndFire.PROXY.spawnParticle("if_pixie", this.getPosX() + this.rand.nextDouble() * 0.15F * (this.rand.nextBoolean() ? -1 : 1), this.getPosY() + this.rand.nextDouble() * 0.15F * (this.rand.nextBoolean() ? -1 : 1), this.getPosZ() + this.rand.nextDouble() * 0.15F * (this.rand.nextBoolean() ? -1 : 1), rgb[0], rgb[1], rgb[2]);
            }
        }
        this.extinguish();
        if (this.ticksExisted > 30) {
            this.remove();
        }
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


                this.setMotion(Vector3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale(f));
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

    @Override
    protected void onImpact(RayTraceResult movingObject) {
        boolean flag = false;
        Entity shootingEntity = this.getShooter();
        if (!this.world.isRemote) {
            if (movingObject.getType() == RayTraceResult.Type.ENTITY && !((EntityRayTraceResult) movingObject).getEntity().isEntityEqual(shootingEntity)) {
                Entity entity = ((EntityRayTraceResult) movingObject).getEntity();
                if (shootingEntity != null && shootingEntity.equals(entity)) {
                    flag = true;
                } else {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.LEVITATION, 100, 0));
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.GLOWING, 100, 0));
                        entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(shootingEntity, null), 5.0F);
                    }
                    if (this.world.isRemote) {
                        for (int i = 0; i < 20; ++i) {
                            IceAndFire.PROXY.spawnParticle("if_pixie", this.getPosX() + this.rand.nextDouble() * 1F * (this.rand.nextBoolean() ? -1 : 1), this.getPosY() + this.rand.nextDouble() * 1F * (this.rand.nextBoolean() ? -1 : 1), this.getPosZ() + this.rand.nextDouble() * 1F * (this.rand.nextBoolean() ? -1 : 1), rgb[0], rgb[1], rgb[2]);
                        }
                    }
                    if (shootingEntity == null || !(shootingEntity instanceof PlayerEntity) || !((PlayerEntity) shootingEntity).isCreative()) {
                        if (rand.nextInt(3) == 0) {
                            this.entityDropItem(new ItemStack(IafItemRegistry.PIXIE_DUST, 1), 0.45F);
                        }
                    }
                }
                if (!flag && this.ticksExisted > 4) {
                    this.remove();
                }
            }

        }
    }
}