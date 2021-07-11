package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IFlyingMount;
import com.github.alexthe666.iceandfire.util.IAFMath;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.controller.MovementController.Action;

public class IafDragonFlightManager {
    private EntityDragonBase dragon;
    private Vector3d target;
    private IafDragonAttacks.Air prevAirAttack;
    private Vector3d startAttackVec;
    private Vector3d startPreyVec;
    private boolean hasStartedToScorch = false;
    private LivingEntity prevAttackTarget = null;

    public IafDragonFlightManager(EntityDragonBase dragon) {
        this.dragon = dragon;
    }

    public static float approach(float number, float max, float min) {
        min = Math.abs(min);
        return number < max ? MathHelper.clamp(number + min, number, max) : MathHelper.clamp(number - min, max, number);
    }

    public static float approachDegrees(float number, float max, float min) {
        float add = MathHelper.wrapDegrees(max - number);
        return approach(number, number + add, min);
    }

    public static float degreesDifferenceAbs(float f1, float f2) {
        return Math.abs(MathHelper.wrapDegrees(f2 - f1));
    }

    public void update() {

        if (dragon.getAttackTarget() != null && dragon.getAttackTarget().isAlive() ) {
            if (dragon instanceof EntityIceDragon && dragon.isInWater()) {
                if (dragon.getAttackTarget() == null) {
                    dragon.airAttack = IafDragonAttacks.Air.SCORCH_STREAM;
                } else {
                    dragon.airAttack = IafDragonAttacks.Air.TACKLE;
                }
            }
            LivingEntity entity = dragon.getAttackTarget();
            if (dragon.airAttack == IafDragonAttacks.Air.TACKLE) {
                target = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight(), entity.getPosZ());
            }
            if (dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST) {
                float distY = 5 + dragon.getDragonStage() * 2;
                int randomDist = 20;
                if (dragon.getDistanceSq(entity.getPosX(), dragon.getPosY(), entity.getPosZ()) < 16 || dragon.getDistanceSq(entity.getPosX(), dragon.getPosY(), entity.getPosZ()) > 900) {
                    target = new Vector3d(entity.getPosX() + dragon.getRNG().nextInt(randomDist) - randomDist / 2, entity.getPosY() + distY, entity.getPosZ() + dragon.getRNG().nextInt(randomDist) - randomDist / 2);
                }
                dragon.stimulateFire(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 3);
            }
            if (dragon.airAttack == IafDragonAttacks.Air.SCORCH_STREAM && startPreyVec != null && startAttackVec != null) {
                float distX = (float) (startPreyVec.x - startAttackVec.x);
                float distY = 5 + dragon.getDragonStage() * 2;
                float distZ = (float) (startPreyVec.z - startAttackVec.z);
                target = new Vector3d(entity.getPosX() + distX, entity.getPosY() + distY, entity.getPosZ() + distZ);
                dragon.tryScorchTarget();
                hasStartedToScorch = true;
                if (target != null && dragon.getDistanceSq(target.x, target.y, target.z) < 100) {
                    target = new Vector3d(entity.getPosX() - distX, entity.getPosY() + distY, entity.getPosZ() - distZ);
                }
            }

        } else if (target == null || dragon.getDistanceSq(target.x, target.y, target.z) < 4 || !dragon.world.isAirBlock(new BlockPos(target)) && (dragon.isHovering() || dragon.isFlying()) || dragon.getCommand() == 2 && dragon.shouldTPtoOwner()) {
            BlockPos viewBlock = null;

            if (dragon instanceof EntityIceDragon && dragon.isInWater()) {
                viewBlock = DragonUtils.getWaterBlockInView(dragon);
            }
            if (dragon.getCommand() == 2 && dragon.isFlying()) {
                viewBlock = DragonUtils.getBlockInViewEscort(dragon);
            }else if(dragon.lookingForRoostAIFlag){
                double xDist = Math.abs(dragon.getPosX() - dragon.getHomePosition().getX() - 0.5F);
                double zDist = Math.abs(dragon.getPosZ() - dragon.getHomePosition().getZ() - 0.5F);
                double xzDist = Math.sqrt(xDist * xDist + zDist * zDist);
                BlockPos upPos = dragon.getHomePosition();
                if(dragon.getDistanceSquared(Vector3d.copyCentered(dragon.getHomePosition())) > 200){
                    upPos = upPos.up(30);
                }
                viewBlock = upPos;

            }else if(viewBlock == null){
                viewBlock = DragonUtils.getBlockInView(dragon);
            }
            if (viewBlock != null) {
                target = new Vector3d(viewBlock.getX() + 0.5, viewBlock.getY() + 0.5, viewBlock.getZ() + 0.5);
            }
        }
        if (target != null) {
            if (target.y > IafConfig.maxDragonFlight) {
                target = new Vector3d(target.x, IafConfig.maxDragonFlight, target.z);
            }
            if (target.y >= dragon.getPosY() && !dragon.isModelDead()) {
                dragon.setMotion(dragon.getMotion().add(0, 0.1D, 0));

            }
        }

        this.prevAirAttack = dragon.airAttack;
    }

    public Vector3d getFlightTarget() {
        return target == null ? Vector3d.ZERO : target;
    }

    public void setFlightTarget(Vector3d target){
        this.target = target;
    }

    private float getDistanceXZ(double x, double z) {
        float f = (float) (dragon.getPosX() - x);
        float f2 = (float) (dragon.getPosZ() - z);
        return f * f + f2 * f2;
    }

    public void onSetAttackTarget(@Nullable LivingEntity LivingEntityIn) {
        if (prevAttackTarget != LivingEntityIn) {
            if (LivingEntityIn != null) {
                startPreyVec = new Vector3d(LivingEntityIn.getPosX(), LivingEntityIn.getPosY(), LivingEntityIn.getPosZ());
            } else {
                startPreyVec = new Vector3d(dragon.getPosX(), dragon.getPosY(), dragon.getPosZ());
            }
            startAttackVec = new Vector3d(dragon.getPosX(), dragon.getPosY(), dragon.getPosZ());
        }
        prevAttackTarget = LivingEntityIn;
    }

    protected static class GroundMoveHelper extends MovementController {
        public GroundMoveHelper(MobEntity LivingEntityIn) {
            super(LivingEntityIn);
        }

        public float distance(float rotateAngleFrom, float rotateAngleTo) {
            return (float) IAFMath.atan2_accurate(MathHelper.sin(rotateAngleTo - rotateAngleFrom), MathHelper.cos(rotateAngleTo - rotateAngleFrom));
        }

        public void tick() {
            if (this.action == Action.STRAFE) {
                float f = (float) this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
                float f1 = (float) this.speed * f;
                float f2 = this.moveForward;
                float f3 = this.moveStrafe;
                float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }

                f4 = f1 / f4;
                f2 = f2 * f4;
                f3 = f3 * f4;
                float f5 = MathHelper.sin(this.mob.rotationYaw * 0.017453292F);
                float f6 = MathHelper.cos(this.mob.rotationYaw * 0.017453292F);
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                PathNavigator pathnavigate = this.mob.getNavigator();
                if (pathnavigate != null) {
                    NodeProcessor nodeprocessor = pathnavigate.getNodeProcessor();
                    if (nodeprocessor != null && nodeprocessor.getFloorNodeType(this.mob.world, MathHelper.floor(this.mob.getPosX() + (double) f7), MathHelper.floor(this.mob.getPosY()), MathHelper.floor(this.mob.getPosZ() + (double) f8)) != PathNodeType.WALKABLE) {
                        this.moveForward = 1.0F;
                        this.moveStrafe = 0.0F;
                        f1 = f;
                    }
                }
                this.mob.setAIMoveSpeed(f1);
                this.mob.setMoveForward(this.moveForward);
                this.mob.setMoveStrafing(this.moveStrafe);
                this.action = Action.WAIT;
            } else if (this.action == Action.MOVE_TO) {
                this.action = Action.WAIT;
                EntityDragonBase dragonBase = (EntityDragonBase) mob;
                double d0 = this.getX() - this.mob.getPosX();
                double d1 = this.getZ() - this.mob.getPosZ();
                double d2 = this.getY() - this.mob.getPosY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;

                if (d3 < 2.500000277905201E-7D) {
                    this.mob.setMoveForward(0.0F);
                    return;
                }
                float targetDegree = (float) (MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
                float changeRange = 70F;
                if (Math.ceil(dragonBase.getWidth()) > 2F) {
                    float ageMod = 1F - Math.min(dragonBase.getAgeInDays(), 125) / 125F;
                    changeRange = 5 + ageMod * 10;
                }
                this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, targetDegree, changeRange);
                this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
                if (d2 > (double) this.mob.stepHeight && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getWidth() / 2)) {
                    this.mob.getJumpController().setJumping();
                    this.action = Action.JUMPING;
                }
            } else if (this.action == Action.JUMPING) {
                this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));

                if (this.mob.isOnGround()) {
                    this.action = Action.WAIT;
                }
            } else {
                this.mob.setMoveForward(0.0F);
            }
        }

    }

    protected static class FlightMoveHelper extends MovementController {

        private EntityDragonBase dragon;

        protected FlightMoveHelper(EntityDragonBase dragonBase) {
            super(dragonBase);
            this.dragon = dragonBase;
        }

        public void tick() {
            if (dragon.collidedHorizontally) {
                dragon.rotationYaw += 180.0F;
                this.speed = 0.1F;
                dragon.flightManager.target = null;
                return;
            }
            float distX = (float) (dragon.flightManager.getFlightTarget().x - dragon.getPosX());
            float distY = (float) (dragon.flightManager.getFlightTarget().y - dragon.getPosY());
            float distZ = (float) (dragon.flightManager.getFlightTarget().z - dragon.getPosZ());
            double planeDist = MathHelper.sqrt(distX * distX + distZ * distZ);
            double yDistMod = 1.0D - (double) MathHelper.abs(distY * 0.7F) / planeDist;
            distX = (float) ((double) distX * yDistMod);
            distZ = (float) ((double) distZ * yDistMod);
            planeDist = MathHelper.sqrt(distX * distX + distZ * distZ);
            double dist = MathHelper.sqrt(distX * distX + distZ * distZ + distY * distY);
            if (dist > 1.0F) {
                float yawCopy = dragon.rotationYaw;
                float atan = (float) MathHelper.atan2(distZ, distX);
                float yawTurn = MathHelper.wrapDegrees(dragon.rotationYaw + 90);
                float yawTurnAtan = MathHelper.wrapDegrees(atan * 57.295776F);
                dragon.rotationYaw = IafDragonFlightManager.approachDegrees(yawTurn, yawTurnAtan, dragon.airAttack == IafDragonAttacks.Air.TACKLE && dragon.getAttackTarget() != null ? 10 : 4.0F) - 90.0F;
                dragon.renderYawOffset = dragon.rotationYaw;
                if (IafDragonFlightManager.degreesDifferenceAbs(yawCopy, dragon.rotationYaw) < 3.0F) {
                    speed = IafDragonFlightManager.approach((float) speed, 1.8F, 0.005F * (1.8F / (float) speed));
                } else {
                    speed = IafDragonFlightManager.approach((float) speed, 0.2F, 0.025F);
                    if (dist < 100D && dragon.getAttackTarget() != null) {
                        speed = speed * (dist / 100D);
                    }
                }
                float finPitch = (float) (-(MathHelper.atan2(-distY, planeDist) * 57.2957763671875D));
                dragon.rotationPitch = finPitch;
                float yawTurnHead = dragon.rotationYaw + 90.0F;
                speed *= dragon.getFlightSpeedModifier();
                speed *= Math.min(1,dist/50+ 0.3);//Make the dragon fly slower when close to target
                double lvt_16_1_ = speed * MathHelper.cos(yawTurnHead * 0.017453292F) * Math.abs((double) distX / dist);
                double lvt_18_1_ = speed * MathHelper.sin(yawTurnHead * 0.017453292F) * Math.abs((double) distZ / dist);
                double lvt_20_1_ = speed * MathHelper.sin(finPitch * 0.017453292F) * Math.abs((double) distY / dist);
                double motionCap = 0.2D;
                dragon.setMotion(dragon.getMotion().add(Math.min(lvt_16_1_ * 0.2D, motionCap), Math.min(lvt_20_1_ * 0.2D, motionCap), Math.min(lvt_18_1_ * 0.2D, motionCap)));
            }
        }


    }

    protected static class PlayerFlightMoveHelper<T extends MobEntity & IFlyingMount> extends MovementController {

        private T dragon;

        public PlayerFlightMoveHelper(T dragon) {
            super(dragon);
            this.dragon = dragon;
        }

        @Override
        public void tick() {
            double flySpeed = speed * speedMod();
            Vector3d dragonVec = dragon.getPositionVec();
            Vector3d moveVec = new Vector3d(posX, posY, posZ);
            Vector3d normalized = moveVec.subtract(dragonVec).normalize();
            double dist = dragonVec.distanceTo(moveVec);
            dragon.setMotion(normalized.x * flySpeed, normalized.y * flySpeed, normalized.z * flySpeed);
            if (dist > 2.5E-7) {
                float yaw = (float) Math.toDegrees(Math.PI * 2 - Math.atan2(normalized.x, normalized.y));
                dragon.rotationYaw = limitAngle(dragon.rotationYaw, yaw, 5);
                dragon.setAIMoveSpeed((float) (speed));
            }
            dragon.move(MoverType.SELF, dragon.getMotion());
        }

        public double speedMod() {
            return (dragon instanceof EntityAmphithere ? 0.75D : 0.5D) * IafConfig.dragonFlightSpeedMod;
        }
    }
}
