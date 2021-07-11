package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IDragonProjectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySeaSerpentBubbles extends AbstractFireballEntity implements IDragonProjectile {

    private int ticksInAir;

    public EntitySeaSerpentBubbles(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public EntitySeaSerpentBubbles(EntityType t, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }


    public EntitySeaSerpentBubbles(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(IafEntityRegistry.SEA_SERPENT_BUBBLES, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySeaSerpentBubbles(EntityType t, World worldIn, EntitySeaSerpent shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.1D;
        this.accelerationY = accelY / d0 * 0.1D;
        this.accelerationZ = accelZ / d0 * 0.1D;
    }

    protected boolean isFireballFiery() {
        return false;
    }

    public void tick() {
        super.tick();
        Entity shootingEntity = this.getShooter();
        if(this.ticksExisted > 400 ){
            this.remove();
        }
        autoTarget();
        this.accelerationX *= 0.95F;
        this.accelerationY *= 0.95F;
        this.accelerationZ *= 0.95F;
        this.addVelocity(this.accelerationX, this.accelerationY, this.accelerationZ);

        if (this.world.isRemote || (shootingEntity == null || !shootingEntity.isAlive()) && this.world.isBlockLoaded(this.getPosition())) {
            if (this.world.isRemote || (shootingEntity == null || !shootingEntity.removed) && this.world.isBlockLoaded(this.getPosition())) {
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
                    for (int i = 0; i < 3; ++i) {
                        IceAndFire.PROXY.spawnParticle("serpent_bubble", this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() - 0.5D, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, 0, 0, 0);
                    }
                }

                this.setMotion(Vector3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale((double)f));
                this.setPosition(d0, d1, d2);
            } else {
                this.remove();
            }
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
            this.setPosition(d0, d1, d2);
            this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
        }
        this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
        if(this.ticksExisted > 20 && !isWet()){
            this.remove();
        }
    }

    protected boolean func_230298_a_(Entity entityIn) {
        return super.func_230298_a_(entityIn) && !(entityIn instanceof EntityMutlipartPart) && !(entityIn instanceof EntitySeaSerpentBubbles);
    }


    public void autoTarget() {
        if(!world.isRemote){
            Entity shootingEntity = this.getShooter();
            if (shootingEntity instanceof EntitySeaSerpent && ((EntitySeaSerpent) shootingEntity).getAttackTarget() != null) {
                Entity target = ((EntitySeaSerpent) shootingEntity).getAttackTarget();
                double d2 = target.getPosX() - this.getPosX();
                double d3 = target.getPosY() - this.getPosY();
                double d4 = target.getPosZ() - this.getPosZ();
                double d0 = MathHelper.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
                this.accelerationX = d2 / d0 * 0.1D;
                this.accelerationY = d3 / d0 * 0.1D;
                this.accelerationZ = d4 / d0 * 0.1D;
            }else if(ticksExisted > 20){
                this.remove();
            }
        }
    }

    public boolean handleWaterMovement() {
        return true;
    }

    protected IParticleData getParticle() {
        return ParticleTypes.BUBBLE;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    public float getCollisionBorderSize() {
        return 0F;
    }

    @Override
    protected void onImpact(RayTraceResult movingObject) {
        boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        if (!this.world.isRemote) {
            if (movingObject.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) movingObject).getEntity();

                if (entity != null && entity instanceof EntitSlowPart) {
                    return;
                }
                Entity shootingEntity = this.getShooter();
                if (shootingEntity != null && shootingEntity instanceof EntitySeaSerpent) {
                    EntitySeaSerpent dragon = (EntitySeaSerpent) shootingEntity;
                    if (dragon.isOnSameTeam(entity) || dragon.isEntityEqual(entity)) {
                        return;
                    }
                    entity.attackEntityFrom(DamageSource.causeMobDamage(dragon), 6.0F);

                }
            }
        }
    }

}
