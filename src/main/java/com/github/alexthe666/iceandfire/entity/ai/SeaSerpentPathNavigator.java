package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class SeaSerpentPathNavigator  extends PathNavigator {

    public SeaSerpentPathNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new SwimNodeProcessor(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }

    protected boolean canNavigate() {
        return true;
    }

    protected Vector3d getEntityPosition() {
        return new Vector3d(this.entity.getPosX(), this.entity.getPosYHeight(0.5D), this.entity.getPosZ());
    }

    public void tick() {
        ++this.totalTicks;
        if (this.tryUpdatePath) {
            this.updatePath();
        }

        if (!this.noPath()) {
            Vector3d lvt_1_2_;
            if (this.canNavigate()) {
                this.pathFollow();
            } else if (this.currentPath != null && !this.currentPath.isFinished()) {
                lvt_1_2_ = this.currentPath.getPosition(this.entity);
                if (MathHelper.floor(this.entity.getPosX()) == MathHelper.floor(lvt_1_2_.x) && MathHelper.floor(this.entity.getPosY()) == MathHelper.floor(lvt_1_2_.y) && MathHelper.floor(this.entity.getPosZ()) == MathHelper.floor(lvt_1_2_.z)) {
                    this.currentPath.incrementPathIndex();
                }
            }

            DebugPacketSender.sendPath(this.world, this.entity, this.currentPath, this.maxDistanceToWaypoint);
            if (!this.noPath()) {
                lvt_1_2_ = this.currentPath.getPosition(this.entity);
                this.entity.getMoveHelper().setMoveTo(lvt_1_2_.x, lvt_1_2_.y, lvt_1_2_.z, this.speed);
            }
        }
    }

    protected void pathFollow() {
        if (this.currentPath != null) {
            Vector3d lvt_1_1_ = this.getEntityPosition();
            float lvt_2_1_ = this.entity.getWidth();
            float lvt_3_1_ = lvt_2_1_ > 0.75F ? lvt_2_1_ / 2.0F : 0.75F - lvt_2_1_ / 2.0F;
            Vector3d lvt_4_1_ = this.entity.getMotion();
            if (Math.abs(lvt_4_1_.x) > 0.2D || Math.abs(lvt_4_1_.z) > 0.2D) {
                lvt_3_1_ = (float)((double)lvt_3_1_ * lvt_4_1_.length() * 6.0D);
            }
            Vector3d lvt_6_1_ = Vector3d.copyCentered(this.currentPath.func_242948_g());
            if (Math.abs(this.entity.getPosX() - lvt_6_1_.x) < (double)lvt_3_1_ && Math.abs(this.entity.getPosZ() - lvt_6_1_.z) < (double)lvt_3_1_ && Math.abs(this.entity.getPosY() - lvt_6_1_.y) < (double)(lvt_3_1_ * 2.0F)) {
                this.currentPath.incrementPathIndex();
            }

            for(int lvt_7_1_ = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); lvt_7_1_ > this.currentPath.getCurrentPathIndex(); --lvt_7_1_) {
                lvt_6_1_ = this.currentPath.getVectorFromIndex(this.entity, lvt_7_1_);
                if (lvt_6_1_.squareDistanceTo(lvt_1_1_) <= 36.0D && this.isDirectPathBetweenPoints(lvt_1_1_, lvt_6_1_, 0, 0, 0)) {
                    this.currentPath.setCurrentPathIndex(lvt_7_1_);
                    break;
                }
            }

            this.checkForStuck(lvt_1_1_);
        }
    }

    protected void checkForStuck(Vector3d positionVec3) {
        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25D) {
                this.clearPath();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVec3;
        }

        if (this.currentPath != null && !this.currentPath.isFinished()) {
            Vector3i lvt_2_1_ = this.currentPath.func_242948_g();
            if (lvt_2_1_.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += Util.milliTime() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = lvt_2_1_;
                double lvt_3_1_ = positionVec3.distanceTo(Vector3d.copyCentered(this.timeoutCachedNode));
                this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? lvt_3_1_ / (double)this.entity.getAIMoveSpeed() * 100.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
                this.timeoutCachedNode = Vector3i.NULL_VECTOR;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.clearPath();
            }

            this.lastTimeoutCheck = Util.milliTime();
        }

    }

    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
        Vector3d lvt_6_1_ = new Vector3d(posVec32.x, posVec32.y + (double)this.entity.getHeight() * 0.5D, posVec32.z);
        return this.world.rayTraceBlocks(new RayTraceContext(posVec31, lvt_6_1_, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType() == RayTraceResult.Type.MISS;
    }

    public boolean canEntityStandOnPos(BlockPos pos) {
        return !this.world.getBlockState(pos).isOpaqueCube(this.world, pos);
    }

    public void setCanSwim(boolean canSwim) {
    }
}
