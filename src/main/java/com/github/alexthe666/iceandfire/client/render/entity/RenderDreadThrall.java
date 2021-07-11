package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.client.model.ModelDreadThrall;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadThrall;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class RenderDreadThrall extends MobRenderer<EntityDreadThrall, ModelDreadThrall> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/dread/dread_thrall.png");
    public static final ResourceLocation TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/dread/dread_thrall_eyes.png");
    public static final ResourceLocation TEXTURE_LEG_ARMOR = new ResourceLocation("iceandfire:textures/models/dread/thrall_legs.png");
    public static final ResourceLocation TEXTURE_ARMOR_0 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_1.png");
    public static final ResourceLocation TEXTURE_ARMOR_1 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_2.png");
    public static final ResourceLocation TEXTURE_ARMOR_2 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_3.png");
    public static final ResourceLocation TEXTURE_ARMOR_3 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_4.png");
    public static final ResourceLocation TEXTURE_ARMOR_4 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_5.png");
    public static final ResourceLocation TEXTURE_ARMOR_5 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_6.png");
    public static final ResourceLocation TEXTURE_ARMOR_6 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_7.png");
    public static final ResourceLocation TEXTURE_ARMOR_7 = new ResourceLocation("iceandfire:textures/models/dread/thrall_chest_8.png");

    public RenderDreadThrall(EntityRendererManager renderManager) {
        super(renderManager, new ModelDreadThrall(0.0F, false), 0.6F);
        this.addLayer(new LayerGenericGlowing(this, TEXTURE_EYES));
        this.addLayer(new HeldItemLayer<EntityDreadThrall, ModelDreadThrall>(this));
    }

    private static ResourceLocation getArmorTexture(int index) {
        switch (index) {
            case 0:
                return TEXTURE_ARMOR_0;
            case 1:
                return TEXTURE_ARMOR_1;
            case 2:
                return TEXTURE_ARMOR_2;
            case 3:
                return TEXTURE_ARMOR_3;
            case 4:
                return TEXTURE_ARMOR_4;
            case 5:
                return TEXTURE_ARMOR_5;
            case 6:
                return TEXTURE_ARMOR_6;
            case 7:
                return TEXTURE_ARMOR_7;
        }
        return TEXTURE_ARMOR_0;
    }

    @Override
    public void preRenderCallback(EntityDreadThrall LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(0.95F, 0.95F, 0.95F);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityDreadThrall entity) {
        return TEXTURE;
    }
}
