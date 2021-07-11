package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDragonsteelArmor extends ArmorItem implements IProtectAgainstDragonItem {

    private IArmorMaterial material;
    private Multimap<Attribute, AttributeModifier> attributeModifierMultimap;
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public ItemDragonsteelArmor(IArmorMaterial material, int renderIndex, EquipmentSlotType slot, String gameName, String name) {
        super(material, slot, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.material = material;
        this.setRegistryName(IceAndFire.MODID, gameName);
        this.attributeModifierMultimap = createAttributeMap();

    }

    //Workaround for armor attributes being registered before the config gets loaded
    private Multimap<Attribute, AttributeModifier> createAttributeMap(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", material.getDamageReductionAmount(slot), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", material.getToughness(), AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }

    private Multimap<Attribute, AttributeModifier> getOrUpdateAttributeMap(){
        //If the armor values have changed recreate the map
        //There might be a prettier way of accomplishing this but it works
        if (this.attributeModifierMultimap.containsKey(Attributes.ARMOR)
            && !this.attributeModifierMultimap.get(Attributes.ARMOR).isEmpty()
            && this.attributeModifierMultimap.get(Attributes.ARMOR).toArray()[0] instanceof AttributeModifier
            && ((AttributeModifier)this.attributeModifierMultimap.get(Attributes.ARMOR).toArray()[0]).getAmount() != getDamageReduceAmount()
        )
        {
            this.attributeModifierMultimap = createAttributeMap();
        }
        return attributeModifierMultimap;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (this.slot !=null) {
            return (this.getArmorMaterial()).getDurability(this.slot);
        }
        return super.getMaxDamage(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        int legs = 11;
        int armor = 10;
        if (material == IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
            legs = 13;
            armor = 12;
        }
        if (material == IafItemRegistry.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL) {
            legs = 21;
            armor = 20;
        }
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlotType.LEGS ? legs : armor);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.dragonscales_armor.desc").mergeStyle(TextFormatting.GRAY));
    }
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == this.slot ? getOrUpdateAttributeMap() : super.getAttributeModifiers(equipmentSlot);
    }
    @Override
    public int getDamageReduceAmount() {
        if (this.material != null)
            return this.material.getDamageReductionAmount(this.getEquipmentSlot());
        return super.getDamageReduceAmount();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if (material == IafItemRegistry.DRAGONSTEEL_FIRE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_fire" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        } else if (material == IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_ice" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_dragonsteel_lightning" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        }
    }
}
