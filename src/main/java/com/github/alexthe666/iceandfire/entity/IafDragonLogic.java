package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.message.MessageSpawnParticleAt;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

/*
    dragon logic separation for client, server and shared sides.
 */
public class IafDragonLogic {

    private EntityDragonBase dragon;

    public IafDragonLogic(EntityDragonBase dragon) {
        this.dragon = dragon;
    }

    /*
    logic done exclusively on server.
    */
    public void updateDragonServer() {

        PlayerEntity ridingPlayer = dragon.getRidingPlayer();
        if(ridingPlayer != null){
            if (dragon.isGoingUp()) {
                if (!dragon.isFlying() && !dragon.isHovering()) {
                    dragon.spacebarTicks += 2;
                }
            } else if (dragon.isDismounting()) {
                if (dragon.isFlying() || dragon.isHovering()) {
                    dragon.setMotion(dragon.getMotion().add(0, -0.04, 0));
                    dragon.setFlying(false);
                    dragon.setHovering(false);
                }
            }
        }
        if (!dragon.isDismounting() && (dragon.isFlying() || dragon.isHovering())) {
            dragon.setMotion(dragon.getMotion().add(0, 0.01, 0));
        }
        if (dragon.isAttacking() && dragon.getControllingPassenger() != null && dragon.getDragonStage() > 1) {
            dragon.setBreathingFire(true);
            dragon.riderShootFire(dragon.getControllingPassenger());
            dragon.fireStopTicks = 10;
        }
        if (dragon.isStriking() && dragon.getControllingPassenger() != null && dragon.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(dragon, (PlayerEntity) dragon.getControllingPassenger(), dragon.getDragonStage() + (dragon.getBoundingBox().maxX - dragon.getBoundingBox().minX));
            if (dragon.getAnimation() != EntityDragonBase.ANIMATION_BITE) {
                dragon.setAnimation(EntityDragonBase.ANIMATION_BITE);
            }
            if (target != null && !DragonUtils.hasSameOwner(dragon, target)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (dragon.getControllingPassenger() != null && dragon.getControllingPassenger().isSneaking()) {
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(dragon.getControllingPassenger(), MiscEntityProperties.class);
            if (properties != null) {
                properties.hasDismountedDragon = true;
            }
            dragon.getControllingPassenger().stopRiding();
        }
        if (dragon.isFlying() && !dragon.isHovering() && dragon.getControllingPassenger() != null && !dragon.isOnGround() && Math.max(Math.abs(dragon.getMotion().getX()), Math.abs(dragon.getMotion().getZ())) < 0.1F) {
            dragon.setHovering(true);
            dragon.setFlying(false);
        }
        if (dragon.isHovering() && !dragon.isFlying() && dragon.getControllingPassenger() != null && !dragon.isOnGround() && Math.max(Math.abs(dragon.getMotion().getX()), Math.abs(dragon.getMotion().getZ())) > 0.1F) {
            dragon.setFlying(true);
            dragon.usingGroundAttack = false;
            dragon.setHovering(false);
        }
        if (dragon.spacebarTicks > 0) {
            dragon.spacebarTicks--;
        }
        if (dragon.spacebarTicks > 20 && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner()) && !dragon.isFlying() && !dragon.isHovering()) {
            dragon.setHovering(true);
        }
        if (dragon.isOverAir() && !dragon.isPassenger()) {
            double ydist = dragon.prevPosY - dragon.getPosY();//down 0.4 up -0.38
            float planeDist = (float) ((Math.abs(dragon.getMotion().x) + Math.abs(dragon.getMotion().z)) * 6F);
            if (!dragon.isHovering()) {
                dragon.incrementDragonPitch((float) (ydist) * 10);
            }
            dragon.setDragonPitch(MathHelper.clamp(dragon.getDragonPitch(), -60, 40));
            float plateau = 2;
            if (dragon.getDragonPitch() > plateau) {
                //down
                //this.motionY -= 0.2D;
                dragon.decrementDragonPitch(planeDist * Math.abs(dragon.getDragonPitch()) / 90);
            }
            if (dragon.getDragonPitch() < -plateau) {//-2
                //up
                dragon.incrementDragonPitch(planeDist * Math.abs(dragon.getDragonPitch()) / 90);
            }
            if (dragon.getDragonPitch() > 2F) {
                dragon.decrementDragonPitch(1);
            } else if (dragon.getDragonPitch() < -2F) {
                dragon.incrementDragonPitch(1);
            }
            if (dragon.getDragonPitch() < -45 && planeDist < 3) {
                if (dragon.isFlying() && !dragon.isHovering()) {
                    dragon.setHovering(true);
                }
            }
        } else {
            dragon.setDragonPitch(0);
        }
        if(dragon.lookingForRoostAIFlag && dragon.getRevengeTarget() != null || dragon.isSleeping()){
            dragon.lookingForRoostAIFlag = false;
        }
        if (IafConfig.doDragonsSleep && !dragon.isSleeping() && !dragon.isTimeToWake() && dragon.getPassengers().isEmpty() && this.dragon.getCommand() != 2) {
            if(dragon.hasHomePosition && dragon.getHomePosition() != null && dragon.getDistanceSquared(Vector3d.copyCentered(dragon.getHomePosition())) > dragon.getWidth() * 10
                    && this.dragon.getCommand() != 2 && this.dragon.getCommand() != 1){
                dragon.lookingForRoostAIFlag = true;
            }else{
                dragon.lookingForRoostAIFlag = false;
                if(!dragon.isInWater() && dragon.isOnGround() && !dragon.isFlying() && !dragon.isHovering() && dragon.getAttackTarget() == null){
                    dragon.setQueuedToSit(true);
                }
            }
        } else{
            dragon.lookingForRoostAIFlag = false;
        }
        if (dragon.isSleeping() && (dragon.isFlying() || dragon.isHovering() || dragon.isInWater() || (dragon.world.canBlockSeeSky(dragon.getPosition()) && dragon.isTimeToWake() && !dragon.isTamed() || dragon.isTimeToWake() && dragon.isTamed()) || dragon.getAttackTarget() != null || !dragon.getPassengers().isEmpty())) {
            dragon.setQueuedToSit(false);
        }
        if (dragon.isQueuedToSit() && dragon.getControllingPassenger() != null) {
            dragon.setSitting(false);
        }
        if (dragon.isBeingRidden() && !dragon.isOverAir() && dragon.isFlying() && !dragon.isHovering() && dragon.flyTicks > 40) {
            dragon.setFlying(false);
        }
        if (dragon.blockBreakCounter <= 0) {
            dragon.blockBreakCounter = IafConfig.dragonBreakBlockCooldown;
        }
        dragon.updateBurnTarget();
        if (dragon.isQueuedToSit() && (dragon.getCommand() != 1 || dragon.getControllingPassenger() != null)) {
            dragon.setSitting(false);
        }
        if (!dragon.isQueuedToSit() && dragon.getCommand() == 1 && dragon.getControllingPassenger() == null) {
            dragon.setSitting(true);
        }
        if (dragon.isQueuedToSit()) {
            dragon.getNavigator().clearPath();
        }
        if (dragon.isInLove()) {
            dragon.world.setEntityState(dragon, (byte) 18);
        }
        if ((int) dragon.prevPosX == (int) dragon.getPosX() && (int) dragon.prevPosZ == (int) dragon.getPosZ()) {
            dragon.ticksStill++;
        } else {
            dragon.ticksStill = 0;
        }
        if (dragon.isTackling() && !dragon.isFlying() && dragon.isOnGround()) {
            dragon.tacklingTicks++;
            if (dragon.tacklingTicks == 40) {
                dragon.tacklingTicks = 0;
                dragon.setTackling(false);
                dragon.setFlying(false);
            }
        }
        if (dragon.getRNG().nextInt(500) == 0 && !dragon.isModelDead() && !dragon.isSleeping()) {
            dragon.roar();
        }
        if (dragon.isFlying() && dragon.getAttackTarget() != null && dragon.airAttack == IafDragonAttacks.Air.TACKLE) {
            dragon.setTackling(true);
        }
        if (dragon.isFlying() && dragon.getAttackTarget() != null && dragon.isTackling() && dragon.getBoundingBox().expand(2.0D, 2.0D, 2.0D).intersects(dragon.getAttackTarget().getBoundingBox())) {
            dragon.usingGroundAttack = true;
            dragon.randomizeAttacks();
            dragon.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(dragon), dragon.getDragonStage() * 3);
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isTackling() && (dragon.getAttackTarget() == null || dragon.airAttack != IafDragonAttacks.Air.TACKLE)) {
            dragon.setTackling(false);
            dragon.randomizeAttacks();
        }
        if (dragon.isPassenger()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
            dragon.setQueuedToSit(false);
        }
        if (dragon.isFlying() && dragon.ticksExisted % 40 == 0 || dragon.isFlying() && dragon.isSleeping()) {
            dragon.setQueuedToSit(false);
        }
        if (!dragon.canMove()) {
            if (dragon.getAttackTarget() != null) {
                dragon.setAttackTarget(null);
            }
            dragon.getNavigator().clearPath();
        }
        if (!dragon.isTamed()) {
            dragon.updateCheckPlayer();
        }
        if (dragon.isModelDead() && (dragon.isFlying() || dragon.isHovering())) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (ridingPlayer == null) {
            if ((dragon.useFlyingPathFinder() || dragon.isHovering()) && dragon.navigatorType != 1) {
                dragon.switchNavigator(1);
            }
        } else {
            if ((dragon.useFlyingPathFinder() || dragon.isHovering()) && dragon.navigatorType != 2) {
                dragon.switchNavigator(2);
            }
        }
        if (!dragon.useFlyingPathFinder() && !dragon.isHovering() && dragon.navigatorType != 0) {
            dragon.switchNavigator(0);
        }
        if (!dragon.isOverAir() && dragon.doesWantToLand() && (dragon.isFlying() || dragon.isHovering()) && !dragon.isInWater()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isHovering() && dragon.isFlying() && dragon.flyTicks > 40) {
            dragon.setHovering(false);
            dragon.setFlying(true);
        }
        if(dragon.isHovering()){
            dragon.hoverTicks++;
        }else{
            dragon.hoverTicks = 0;
        }
        if (dragon.isHovering() && !dragon.isFlying()) {
            if (dragon.isSleeping()) {
                dragon.setHovering(false);
            }
            if (dragon.doesWantToLand() && !dragon.isOnGround() && !dragon.isInWater()) {
                dragon.setMotion(dragon.getMotion().add(0, -0.25, 0));
            } else {
                if ((dragon.getControllingPassenger() == null || dragon.getControllingPassenger() instanceof EntityDreadQueen) && !dragon.isBeyondHeight()) {
                    double up = dragon.isInWater() ? 0.12D : 0.08D;
                    dragon.setMotion(dragon.getMotion().add(0, up, 0));
                }
                if (dragon.hoverTicks > 40) {
                    dragon.setHovering(false);
                    dragon.setFlying(true);
                    dragon.flyHovering = 0;
                    dragon.hoverTicks = 0;
                    dragon.flyTicks = 0;
                }
            }
        }
        if (dragon.isSleeping()) {
            dragon.getNavigator().clearPath();
        }
        if ((dragon.isOnGround() || dragon.isInWater()) && dragon.flyTicks != 0) {
            dragon.flyTicks = 0;
        }
        if (dragon.isAllowedToTriggerFlight() && dragon.isFlying() && dragon.doesWantToLand()) {
            dragon.setFlying(false);
            dragon.setHovering(dragon.isOverAir());
            if (!dragon.isOverAir()) {
                dragon.flyTicks = 0;
                dragon.setFlying(false);
            }
        }
        if (dragon.isFlying()) {
            dragon.flyTicks++;
        }
        if ((dragon.isHovering() || dragon.isFlying()) && dragon.isSleeping()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (!dragon.isFlying() && !dragon.isHovering()) {
            if (dragon.isAllowedToTriggerFlight() || dragon.getPosY() < -1) {
                if (dragon.getRNG().nextInt(dragon.getFlightChancePerTick()) == 0 || dragon.getPosY() < -1 || dragon.getAttackTarget() != null && Math.abs(dragon.getAttackTarget().getPosY()- dragon.getPosY()) > 5 || dragon.isInWater() && !dragon.isIceInWater()) {
                    dragon.setHovering(true);
                    dragon.setQueuedToSit(false);
                    dragon.setSitting(false);
                    dragon.flyHovering = 0;
                    dragon.hoverTicks = 0;
                    dragon.flyTicks = 0;
                }
            }
        }
        if (dragon.getAttackTarget() != null) {
            if (!dragon.getPassengers().isEmpty() && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner())) {
                dragon.setAttackTarget(null);
            }
            if (!DragonUtils.isAlive(dragon.getAttackTarget())) {
                dragon.setAttackTarget(null);
            }
        }
        if (!dragon.isAgingDisabled()) {
            dragon.setAgeInTicks(dragon.getAgeInTicks() + 1);
            if (dragon.getAgeInTicks() % 24000 == 0) {
                dragon.updateAttributes();
                dragon.growDragon(0);
            }
        }
        if (dragon.ticksExisted % IafConfig.dragonHungerTickRate == 0 && IafConfig.dragonHungerTickRate > 0) {
            if (dragon.getHunger() > 0) {
                dragon.setHunger(dragon.getHunger() - 1);
            }
        }
        if ((dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && dragon.getDragonStage() < 2) {
            dragon.usingGroundAttack = true;
            dragon.randomizeAttacks();
            dragon.playSound(dragon.getBabyFireSound(), 1, 1);
        }
        if (dragon.isBreathingFire()) {
            if(dragon.isSleeping() || dragon.isModelDead()){
                dragon.setBreathingFire(false);
                dragon.randomizeAttacks();
                dragon.fireTicks = 0;
            }
            if (dragon.burningTarget == null) {
                if (dragon.fireTicks > dragon.getDragonStage() * 25 || dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner()) && dragon.fireStopTicks <= 0) {
                    dragon.setBreathingFire(false);
                    dragon.randomizeAttacks();
                    dragon.fireTicks = 0;
                }
            }

            if (dragon.fireStopTicks > 0 && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner())) {
                dragon.fireStopTicks--;
            }
        }
        if (dragon.isFlying() && dragon.getAttackTarget() != null && dragon.getBoundingBox().expand(3.0F, 3.0F, 3.0F).intersects(dragon.getAttackTarget().getBoundingBox())) {
            dragon.attackEntityAsMob(dragon.getAttackTarget());
        }
        if (dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.TACKLE && (dragon.collidedHorizontally || dragon.isOnGround())) {
            dragon.usingGroundAttack = true;
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isFlying() && dragon.usingGroundAttack) {
            dragon.airAttack = IafDragonAttacks.Air.TACKLE;
        }
        if (dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.TACKLE && dragon.getAttackTarget() != null && dragon.isTargetBlocked(dragon.getAttackTarget().getPositionVec())) {
            dragon.randomizeAttacks();
        }

    }

    /*
    logic done exclusively on client.
    */
    public void updateDragonClient() {
        if (!dragon.isModelDead()) {
            dragon.turn_buffer.calculateChainSwingBuffer(50, 0, 4, dragon);
            dragon.tail_buffer.calculateChainSwingBuffer(90, 20, 5F, dragon);
            if (!dragon.isOnGround()) {
                dragon.roll_buffer.calculateChainFlapBuffer(55, 1, 2F, 0.5F, dragon);
                dragon.pitch_buffer.calculateChainWaveBuffer(90, 10, 1F, 0.5F, dragon);
                dragon.pitch_buffer_body.calculateChainWaveBuffer(80, 10, 1, 0.5F, dragon);
            }
        }
        if (dragon.walkCycle < 39) {
            dragon.walkCycle++;
        } else {
            dragon.walkCycle = 0;
        }
        if (dragon.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST && (dragon.getAnimationTick() == 17 || dragon.getAnimationTick() == 22 || dragon.getAnimationTick() == 28)) {
            dragon.spawnGroundEffects();
        }
        dragon.legSolver.update(dragon, dragon.getRenderSize() / 3F);

        if (dragon.flightCycle == 11) {
            dragon.spawnGroundEffects();
        }
        if (dragon.isModelDead() && dragon.flightCycle != 0) {
            dragon.flightCycle = 0;
        }
    }

    /*
    logic done on server and client on parallel.
    */
    public void updateDragonCommon() {
        if (dragon.isBreathingFire()) {
            dragon.fireTicks++;
            if (dragon.burnProgress < 40) {
                dragon.burnProgress++;
            }
        } else {
            dragon.burnProgress = 0;
        }
        if (dragon.flightCycle == 2 && !dragon.isDiving() && (dragon.isFlying() || dragon.isHovering())) {
            float dragonSoundVolume = IafConfig.dragonFlapNoiseDistance;
            float dragonSoundPitch = dragon.getSoundPitch();
            dragon.playSound(IafSoundRegistry.DRAGON_FLIGHT, dragonSoundVolume, dragonSoundPitch);
        }
        if (dragon.flightCycle < 58) {
            dragon.flightCycle += 2;
        } else {
            dragon.flightCycle = 0;
        }
        boolean sitting = dragon.isQueuedToSit() && !dragon.isModelDead() && !dragon.isSleeping() && !dragon.isHovering() && !dragon.isFlying();
        if (sitting && dragon.sitProgress < 20.0F) {
            dragon.sitProgress += 0.5F;
        } else if (!sitting && dragon.sitProgress > 0.0F) {
            dragon.sitProgress -= 0.5F;
        }
        boolean sleeping = dragon.isSleeping() && !dragon.isHovering() && !dragon.isFlying();
        if (sleeping && dragon.sleepProgress < 20.0F) {
            dragon.sleepProgress += 0.5F;
        } else if (!sleeping && dragon.sleepProgress > 0.0F) {
            dragon.sleepProgress -= 0.5F;
        }
        boolean fireBreathing = dragon.isBreathingFire();
        dragon.prevFireBreathProgress = dragon.fireBreathProgress;
        if (fireBreathing && dragon.fireBreathProgress < 10.0F) {
            dragon.fireBreathProgress += 0.5F;
        } else if (!fireBreathing && dragon.fireBreathProgress > 0.0F) {
            dragon.fireBreathProgress -= 0.5F;
        }
        boolean hovering = dragon.isHovering() || dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST && dragon.getAttackTarget() != null && dragon.getDistance(dragon.getAttackTarget()) < 17F;
        if (hovering && dragon.hoverProgress < 20.0F) {
            dragon.hoverProgress += 0.5F;
        } else if (!hovering && dragon.hoverProgress > 0.0F) {
            dragon.hoverProgress -= 2F;
        }
        boolean diving = dragon.isDiving();
        if (diving && dragon.diveProgress < 10.0F) {
            dragon.diveProgress += 1F;
        } else if (!diving && dragon.diveProgress > 0.0F) {
            dragon.diveProgress -= 2F;
        }
        boolean tackling = dragon.isTackling() && dragon.isOverAir();
        if (tackling && dragon.tackleProgress < 5F) {
            dragon.tackleProgress += 0.5F;
        } else if (!tackling && dragon.tackleProgress > 0.0F) {
            dragon.tackleProgress -= 1.5F;
        }
        boolean flying = dragon.isFlying();
        if (flying && dragon.flyProgress < 20.0F) {
            dragon.flyProgress += 0.5F;
        } else if (!flying && dragon.flyProgress > 0.0F) {
            dragon.flyProgress -= 2F;
        }
        boolean modeldead = dragon.isModelDead();
        if (modeldead && dragon.modelDeadProgress < 20.0F) {
            dragon.modelDeadProgress += 0.5F;
        } else if (!modeldead && dragon.modelDeadProgress > 0.0F) {
            dragon.modelDeadProgress -= 0.5F;
        }
        boolean riding = dragon.isPassenger() && dragon.getRidingEntity() != null && dragon.getRidingEntity() instanceof PlayerEntity;
        if (riding && dragon.ridingProgress < 20.0F) {
            dragon.ridingProgress += 0.5F;
        } else if (!riding && dragon.ridingProgress > 0.0F) {
            dragon.ridingProgress -= 0.5F;
        }
        if (dragon.hasHadHornUse) {
            dragon.hasHadHornUse = false;
        }
        if ((dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && dragon.getDragonStage() < 2) {
            if (dragon.world.isRemote) {
                dragon.spawnBabyParticles();
            }
            dragon.randomizeAttacks();
        }
    }


    /*
    logic handler for the dragon's melee attacks.
    */
    public void updateDragonAttack() {
        if (dragon.isPlayingAttackAnimation() && dragon.getAttackTarget() != null && dragon.canEntityBeSeen(dragon.getAttackTarget())) {
            LivingEntity target = dragon.getAttackTarget();
            double dist = dragon.getDistance(target);
            if (dist < dragon.getRenderSize() * 0.2574 * 2 + 2) {
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_BITE) {
                    if (dragon.getAnimationTick() > 15 && dragon.getAnimationTick() < 25) {
                        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        dragon.usingGroundAttack = dragon.getRNG().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_TAILWHACK) {
                    if (dragon.getAnimationTick() > 20 && dragon.getAnimationTick() < 30) {
                        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        target.applyKnockback( dragon.getDragonStage() * 0.6F, MathHelper.sin(dragon.rotationYaw * 0.017453292F), -MathHelper.cos(dragon.rotationYaw * 0.017453292F));
                        dragon.usingGroundAttack = dragon.getRNG().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST) {
                    if ((dragon.getAnimationTick() == 15 || dragon.getAnimationTick() == 25 || dragon.getAnimationTick() == 35)) {
                        target.attackEntityFrom(DamageSource.causeMobDamage(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        target.applyKnockback( dragon.getDragonStage() * 0.6F, MathHelper.sin(dragon.rotationYaw * 0.017453292F), -MathHelper.cos(dragon.rotationYaw * 0.017453292F));
                        dragon.usingGroundAttack = dragon.getRNG().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
            }
        }
    }

    public void debug() {
        String side = dragon.world.isRemote ? "CLIENT" : "SERVER";
        String owner = dragon.getOwner() == null ? "null" : dragon.getOwner().getName().getString();
        String attackTarget = dragon.getAttackTarget() == null ? "null" : dragon.getAttackTarget().getName().getString();
        IceAndFire.LOGGER.warn("DRAGON DEBUG[" + side + "]:"
                + "\nStage: " + dragon.getDragonStage()
                + "\nAge: " + dragon.getAgeInDays()
                + "\nVariant: " + dragon.getVariantName(dragon.getVariant())
                + "\nOwner: " + owner
                + "\nAttack Target: " + attackTarget
                + "\nFlying: " + dragon.isFlying()
                + "\nHovering: " + dragon.isHovering()
                + "\nHovering Time: " + dragon.hoverTicks
                + "\nWidth: " + dragon.getWidth()
                + "\nMoveHelper: " + dragon.getMoveHelper()
                + "\nGround Attack: " + dragon.groundAttack
                + "\nAir Attack: " + dragon.airAttack
                + "\nTackling: " + dragon.isTackling()

        );
    }

    public void debugPathfinder(Path currentPath) {
        if (IceAndFire.DEBUG) {
            try {
                for (int i = 0; i < currentPath.getCurrentPathLength(); i++) {
                    PathPoint point = currentPath.getPathPointFromIndex(i);
                    int particle = 2;
                    IceAndFire.sendMSGToAll(new MessageSpawnParticleAt(point.x, point.y, point.z, particle));
                }
                if (currentPath.func_242948_g() != null) {
                    Vector3d point = Vector3d.copyCentered(currentPath.func_242948_g());
                    int particle = 1;
                    IceAndFire.sendMSGToAll(new MessageSpawnParticleAt(point.x, point.y, point.z, particle));

                }
            } catch (Exception e) {
                //Pathfinders are always unfriendly.
            }

        }
    }
}