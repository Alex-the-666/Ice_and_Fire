package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerPixieGlow;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerPixieItem;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPixie extends MobRenderer<EntityPixie, ModelPixie> {

    public static final ResourceLocation TEXTURE_0 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_0.png");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_1.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_2.png");
    public static final ResourceLocation TEXTURE_3 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_3.png");
    public static final ResourceLocation TEXTURE_4 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_4.png");
    public static final ResourceLocation TEXTURE_5 = new ResourceLocation("iceandfire:textures/models/pixie/pixie_5.png");

    public RenderPixie(EntityRendererManager renderManager) {
        super(renderManager, new ModelPixie(), 0.2F);
        this.layerRenderers.add(new LayerPixieItem(this));
        this.layerRenderers.add(new LayerPixieGlow(this));

    }

    @Override
    public void preRenderCallback(EntityPixie LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(0.55F, 0.55F, 0.55F);
        if (LivingEntityIn.isQueuedToSit()) {
            stack.translate(0F, 0.5F, 0F);

        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPixie pixie) {
        switch (pixie.getColor()) {
            default:
                return TEXTURE_0;
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case 3:
                return TEXTURE_3;
            case 4:
                return TEXTURE_4;
            case 5:
                return TEXTURE_5;
        }
    }

}
