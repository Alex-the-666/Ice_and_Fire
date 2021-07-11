package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IafConfig;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundEvent;

public class DragonsteelArmorMaterial extends IafArmorMaterial {

    public DragonsteelArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
        super(name, durability, damageReduction, encantability, sound, toughness);
    }

    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
        int[] damageReduction = new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5};
        return damageReduction[slotIn.getIndex()];
    }

    public float getKnockbackResistance(){
        return 0;
    }

    public int getDurability(EquipmentSlotType slotIn) {
        return (int) (MAX_DAMAGE_ARRAY[slotIn.getIndex()] * 0.02D * IafConfig.dragonsteelBaseDurabilityEquipment);
    }
}
