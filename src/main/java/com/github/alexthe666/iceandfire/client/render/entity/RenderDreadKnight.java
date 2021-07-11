package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.client.model.ModelDreadKnight;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadKnight;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class RenderDreadKnight extends MobRenderer<EntityDreadKnight, ModelDreadKnight> {
    public static final ResourceLocation TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_eyes.png");
    public static final ResourceLocation TEXTURE_0 = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_1.png");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_2.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_3.png");

    public RenderDreadKnight(EntityRendererManager renderManager) {
        super(renderManager, new ModelDreadKnight(0.0F, false), 0.6F);
        this.addLayer(new LayerGenericGlowing(this, TEXTURE_EYES));
        this.addLayer(new HeldItemLayer<EntityDreadKnight, ModelDreadKnight>(this));
    }

    @Override
    protected void preRenderCallback(EntityDreadKnight entity, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityDreadKnight entity) {
        switch (entity.getArmorVariant()) {
            case 0:
                return TEXTURE_0;
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            default:
                return TEXTURE_0;
        }
    }
}
