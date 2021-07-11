package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
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
public class LayerPixieGlow extends LayerRenderer<EntityPixie, ModelPixie> {

    private final RenderPixie render;

    public LayerPixieGlow(RenderPixie renderIn) {
        super(renderIn);
        this.render = renderIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityPixie pixie, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = RenderPixie.TEXTURE_0;
        switch (pixie.getColor()) {
            default:
                texture = RenderPixie.TEXTURE_0;
                break;
            case 1:
                texture = RenderPixie.TEXTURE_1;
                break;
            case 2:
                texture = RenderPixie.TEXTURE_2;
                break;
            case 3:
                texture = RenderPixie.TEXTURE_3;
                break;
            case 4:
                texture = RenderPixie.TEXTURE_4;
                break;
            case 5:
                texture = RenderPixie.TEXTURE_5;
                break;
        }
        RenderType eyes = RenderType.getEyes(texture);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(eyes);
        this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}