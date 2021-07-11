package com.github.alexthe666.iceandfire.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class ItemDragonFlesh extends ItemGenericFood {

    int dragonType;

    public ItemDragonFlesh(int dragonType) {
        super(8, 0.8F, true, false, false, getNameForType(dragonType));
        this.dragonType = dragonType;
    }

    private static String getNameForType(int dragonType) {
        switch (dragonType){
            case 0:
                return "fire_dragon_flesh";
            case 1:
                return "ice_dragon_flesh";
            case 2:
                return "lightning_dragon_flesh";
        }
        return "fire_dragon_flesh";
    }

    public void onFoodEaten(ItemStack stack, World worldIn, LivingEntity livingEntity) {
        if (!worldIn.isRemote) {
            if (dragonType == 0) {
                livingEntity.setFire(5);
            } else if(dragonType == 1){
                livingEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
            }else{
                if(!livingEntity.world.isRemote){
                    LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(livingEntity.world);
                    lightningboltentity.moveForced(livingEntity.getPositionVec());
                    if(!livingEntity.world.isRemote){
                        livingEntity.world.addEntity(lightningboltentity);
                    }
                }
            }
        }
    }
}
