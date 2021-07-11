package com.github.alexthe666.iceandfire.client.render.entity.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.alexthe666.iceandfire.client.texture.ArrayLayeredTexture;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

public class LayerDragonArmor extends LayerRenderer<EntityDragonBase, SegmentedModel<EntityDragonBase>> {
    private static final Map<String, ResourceLocation> LAYERED_ARMOR_CACHE = Maps.newHashMap();
    private static EquipmentSlotType[] ARMOR_SLOTS = {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
    private final MobRenderer render;

    public LayerDragonArmor(MobRenderer renderIn, int type) {
        super(renderIn);
        this.render = renderIn;
    }

    public static void clearCache(String str) {
        LAYERED_ARMOR_CACHE.remove(str);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityDragonBase dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int armorHead = dragon.getArmorOrdinal(dragon.getItemStackFromSlot(EquipmentSlotType.HEAD));
        int armorNeck = dragon.getArmorOrdinal(dragon.getItemStackFromSlot(EquipmentSlotType.CHEST));
        int armorLegs = dragon.getArmorOrdinal(dragon.getItemStackFromSlot(EquipmentSlotType.LEGS));
        int armorFeet = dragon.getArmorOrdinal(dragon.getItemStackFromSlot(EquipmentSlotType.FEET));
        String armorTexture = dragon.dragonType.getName() + "_" + armorHead + "_" + armorNeck + "_" + armorLegs + "_" + armorFeet;
        if (!armorTexture.equals(dragon.dragonType.getName() + "_0_0_0_0")) {
            ResourceLocation resourcelocation = LAYERED_ARMOR_CACHE.get(armorTexture);
            if (resourcelocation == null) {
                resourcelocation = new ResourceLocation("iceandfire" + "dragon_armor_" + armorTexture);
                List<String> tex = new ArrayList<String>();
                for (EquipmentSlotType slot : ARMOR_SLOTS) {
                    if (dragon.dragonType == DragonType.FIRE) {
                        tex.add(EnumDragonTextures.Armor.getArmorForDragon(dragon, slot).FIRETEXTURE.toString());
                    } else  if (dragon.dragonType == DragonType.ICE) {
                        tex.add(EnumDragonTextures.Armor.getArmorForDragon(dragon, slot).ICETEXTURE.toString());
                    } else{
                        tex.add(EnumDragonTextures.Armor.getArmorForDragon(dragon, slot).LIGHTNINGTEXTURE.toString());
                    }
                }
                ArrayLayeredTexture layeredBase = new ArrayLayeredTexture(tex);
                Minecraft.getInstance().getTextureManager().loadTexture(resourcelocation, layeredBase);
                LAYERED_ARMOR_CACHE.put(armorTexture, resourcelocation);
            }
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(resourcelocation));
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}