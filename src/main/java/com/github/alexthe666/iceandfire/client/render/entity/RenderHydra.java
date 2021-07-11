package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerHydraHead;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderHydra extends MobRenderer<EntityHydra, ModelHydraBody> {

    public static final ResourceLocation TEXUTURE_0 = new ResourceLocation("iceandfire:textures/models/hydra/hydra_0.png");
    public static final ResourceLocation TEXUTURE_1 = new ResourceLocation("iceandfire:textures/models/hydra/hydra_1.png");
    public static final ResourceLocation TEXUTURE_2 = new ResourceLocation("iceandfire:textures/models/hydra/hydra_2.png");
    public static final ResourceLocation TEXUTURE_EYES = new ResourceLocation("iceandfire:textures/models/hydra/hydra_eyes.png");

    public RenderHydra(EntityRendererManager renderManager) {
        super(renderManager, new ModelHydraBody(), 1.2F);
        this.addLayer(new LayerHydraHead(this));
        this.addLayer(new LayerGenericGlowing(this, TEXUTURE_EYES));
    }

    @Override
    public void preRenderCallback(EntityHydra LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityHydra gorgon) {
        switch (gorgon.getVariant()) {
            default:
                return TEXUTURE_0;
            case 1:
                return TEXUTURE_1;
            case 2:
                return TEXUTURE_2;
        }
    }

}
