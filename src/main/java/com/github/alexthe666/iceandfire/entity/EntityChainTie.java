package com.github.alexthe666.iceandfire.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.block.WallBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityChainTie extends HangingEntity {

    public EntityChainTie(EntityType type, World worldIn) {
        super(type, worldIn);
        this.facingDirection = Direction.NORTH;
    }

    public EntityChainTie(EntityType type, World worldIn, BlockPos hangingPositionIn) {
        super(type, worldIn, hangingPositionIn);
        this.setPosition((double) hangingPositionIn.getX() + 0.5D, hangingPositionIn.getY(), (double) hangingPositionIn.getZ() + 0.5D);
        this.forceSpawn = true;
    }

    public static EntityChainTie createKnot(World worldIn, BlockPos fence) {
        EntityChainTie entityleashknot = new EntityChainTie(IafEntityRegistry.CHAIN_TIE, worldIn, fence);
        worldIn.addEntity(entityleashknot);
        entityleashknot.playPlaceSound();
        return entityleashknot;
    }

    @Nullable
    public static EntityChainTie getKnotForPosition(World worldIn, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (EntityChainTie entityleashknot : worldIn.getEntitiesWithinAABB(EntityChainTie.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D))) {
            if (entityleashknot != null && entityleashknot.getHangingPosition() != null && entityleashknot.getHangingPosition().equals(pos)) {
                return entityleashknot;
            }
        }

        return null;
    }

    public void setPosition(double x, double y, double z) {
        super.setPosition((double) MathHelper.floor(x) + 0.5D, (double) MathHelper.floor(y) + 0.5D, (double) MathHelper.floor(z) + 0.5D);
    }

    protected void updateBoundingBox() {
        super.updateBoundingBox();
        this.setRawPosition((double) this.hangingPosition.getX() + 0.5D, (double) this.hangingPosition.getY() + 0.5D, (double) this.hangingPosition.getZ() + 0.5D);
        if (this.isAddedToWorld() && this.world instanceof net.minecraft.world.server.ServerWorld)
            ((net.minecraft.world.server.ServerWorld) this.world).chunkCheck(this); // Forge - Process chunk registration after moving.
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            return super.attackEntityFrom(source, amount);
        }
        return false;
    }

    public int getWidthPixels() {
        return 0;
    }

    public int getHeightPixels() {
        return 0;
    }

    public void writeAdditional(CompoundNBT compound) {
        BlockPos blockpos = this.getHangingPosition();
        compound.putInt("TileX", blockpos.getX());
        compound.putInt("TileY", blockpos.getY());
        compound.putInt("TileZ", blockpos.getZ());
    }

    public void readAdditional(CompoundNBT compound) {
        this.hangingPosition = new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 1024.0D;
    }

    public void onBroken(@Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    public void remove(boolean keepData) {
        this.removed = true;
        double d0 = 30D;
        List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.getPosX() - d0, this.getPosY() - d0, this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + d0, this.getPosZ() + d0));
        for (LivingEntity LivingEntity : list) {
            ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
            if (chainProperties != null && chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity, this)) {
                chainProperties.removeChain(LivingEntity, this);
                ItemEntity entityitem = new ItemEntity(this.world, this.getPosX(), this.getPosY() + (double) 1, this.getPosZ(), new ItemStack(IafItemRegistry.CHAIN));
                entityitem.setDefaultPickupDelay();
                this.world.addEntity(entityitem);
            }
        }
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        if (this.world.isRemote) {
            return getEntityInteractionResult(player, hand);
        } else {
            boolean flag = false;
            double d0 = 30D;
            List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.getPosX() - d0, this.getPosY() - d0, this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + d0, this.getPosZ() + d0));

            for (LivingEntity LivingEntity : list) {
                ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, ChainEntityProperties.class);
                if (chainProperties != null && chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity, player)) {
                    chainProperties.addChain(LivingEntity, this);
                    chainProperties.removeChain(LivingEntity, player);
                    flag = true;
                }
            }

            if (!flag) {
                this.remove();

                if (player.isCreative()) {
                    for (LivingEntity LivingEntity1 : list) {
                        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity1, ChainEntityProperties.class);
                        if (chainProperties.isChained() && chainProperties.isConnectedToEntity(LivingEntity1, this)) {
                            chainProperties.removeChain(LivingEntity1, this);
                            ItemEntity entityitem = new ItemEntity(this.world, this.getPosX(), this.getPosY() + (double) 1, this.getPosZ(), new ItemStack(IafItemRegistry.CHAIN));
                            entityitem.setDefaultPickupDelay();
                            this.world.addEntity(entityitem);
                        }
                    }
                }
            }

            return ActionResultType.SUCCESS;
        }
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean onValidSurface() {
        return this.world.getBlockState(this.hangingPosition).getBlock() instanceof WallBlock;
    }

    public void playPlaceSound() {
        this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }
}
