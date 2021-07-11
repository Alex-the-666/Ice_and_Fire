package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.client.model.ModelGorgon;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGorgon;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerGorgonEyes extends LayerRenderer<EntityGorgon, ModelGorgon> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/gorgon/gorgon_eyes.png");
    private final RenderGorgon render;

    public LayerGorgonEyes(RenderGorgon renderIn) {
        super(renderIn);
        this.render = renderIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityGorgon entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getAnimation() == EntityGorgon.ANIMATION_SCARE || entity.getAnimation() == EntityGorgon.ANIMATION_HIT) {
            RenderType eyes = RenderType.getEyes(TEXTURE);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(eyes);
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}