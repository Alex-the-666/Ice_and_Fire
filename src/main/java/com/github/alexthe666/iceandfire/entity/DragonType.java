package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.BlockEggInIce;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class DragonType {

    public static final DragonType FIRE = new DragonType("fire");
    public static final DragonType ICE = new DragonType("ice").setPiscivore();
    public static final DragonType LIGHTNING = new DragonType("lightning");

    private String name;
    private boolean piscivore;

    public DragonType(String name) {
        this.name = name;
    }

    public static String getNameFromInt(int type){
        if(type == 2){
            return "lightning";
        }else if (type == 1){
            return "ice";
        }else{
            return "fire";
        }
    }

    public static int getIntFromType(DragonType type){
        if(type == LIGHTNING){
            return 2;
        }else if (type == ICE){
            return 1;
        }else{
            return 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPiscivore() {
        return piscivore;
    }

    public DragonType setPiscivore() {
        piscivore = true;
        return this;
    }

    public void updateEggCondition(EntityDragonEgg egg) {
        BlockPos pos = new BlockPos(egg.getPositionVec());
        if (this == FIRE) {
            if (egg.world.getBlockState(pos).getMaterial() == Material.FIRE) {
                egg.setDragonAge(egg.getDragonAge() + 1);
            }
            if (egg.getDragonAge() > IafConfig.dragonEggTime) {
                if (egg.world.getBlockState(pos).getMaterial() == Material.FIRE) {
                    egg.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    EntityFireDragon dragon = new EntityFireDragon(egg.world);
                    if (egg.hasCustomName()) {
                        dragon.setCustomName(egg.getCustomName());
                    }
                    dragon.setVariant(egg.getEggType().ordinal());
                    dragon.setGender(egg.getRNG().nextBoolean());
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    dragon.setHunger(50);
                    if (!egg.world.isRemote) {
                        egg.world.addEntity(dragon);
                    }
                    if (egg.hasCustomName()) {
                        dragon.setCustomName(egg.getCustomName());
                    }
                    dragon.setTamed(true);
                    dragon.setOwnerId(egg.getOwnerId());
                    egg.world.playSound(egg.getPosX(), egg.getPosY() + egg.getEyeHeight(), egg.getPosZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.world.playSound(egg.getPosX(), egg.getPosY() + egg.getEyeHeight(), egg.getPosZ(), IafSoundRegistry.EGG_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.remove();
                }

            }
        }
        if (this == ICE) {
            if (egg.world.getBlockState(pos).getMaterial() == Material.WATER && egg.getRNG().nextInt(500) == 0) {
                egg.remove();
                egg.world.setBlockState(pos, IafBlockRegistry.EGG_IN_ICE.getDefaultState());
                egg.world.playSound(egg.getPosX(), egg.getPosY() + egg.getEyeHeight(), egg.getPosZ(), SoundEvents.BLOCK_GLASS_BREAK, egg.getSoundCategory(), 2.5F, 1.0F, false);
                if (egg.world.getBlockState(pos).getBlock() instanceof BlockEggInIce) {
                    ((TileEntityEggInIce) egg.world.getTileEntity(pos)).type = egg.getEggType();
                    ((TileEntityEggInIce) egg.world.getTileEntity(pos)).ownerUUID = egg.getOwnerId();
                }
            }
        }
        if (this == LIGHTNING) {
            boolean flag;
            BlockPos.Mutable blockpos$pooledmutable = new BlockPos.Mutable(egg.getPosX(), egg.getPosY(), egg.getPosZ()) ;
            flag = egg.world.isRainingAt(blockpos$pooledmutable) || egg.world.isRainingAt(blockpos$pooledmutable.setPos(egg.getPosX(), egg.getPosY() + (double)egg.size.height, egg.getPosZ()));
            if (egg.world.canSeeSky(egg.getPosition().up()) && flag) {
                egg.setDragonAge(egg.getDragonAge() + 1);
            }
            if (egg.getDragonAge() > IafConfig.dragonEggTime) {
                EntityLightningDragon dragon = new EntityLightningDragon(egg.world);
                if (egg.hasCustomName()) {
                    dragon.setCustomName(egg.getCustomName());
                }
                dragon.setVariant(egg.getEggType().ordinal() - 8);
                dragon.setGender(egg.getRNG().nextBoolean());
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setHunger(50);
                if (!egg.world.isRemote) {
                    egg.world.addEntity(dragon);
                }
                if (egg.hasCustomName()) {
                    dragon.setCustomName(egg.getCustomName());
                }
                dragon.setTamed(true);
                dragon.setOwnerId(egg.getOwnerId());
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(egg.world);
                lightningboltentity.setPosition(egg.getPosX(), egg.getPosY(), egg.getPosZ());
                lightningboltentity.setEffectOnly(true);
                if(!egg.world.isRemote){
                    egg.world.addEntity(lightningboltentity);
                }
                egg.world.playSound(egg.getPosX(), egg.getPosY() + egg.getEyeHeight(), egg.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.world.playSound(egg.getPosX(), egg.getPosY() + egg.getEyeHeight(), egg.getPosZ(), IafSoundRegistry.EGG_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.remove();


            }
        }
    }
}
