package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHippogryph extends MobRenderer<EntityHippogryph, ModelHippogryph> {

    public RenderHippogryph(EntityRendererManager renderManager) {
        super(renderManager, new ModelHippogryph(), 0.8F);
        this.layerRenderers.add(new LayerHippogriffSaddle(this));

    }

    protected void preRenderCallback(EntityHippogryph entity, MatrixStack matrix, float partialTickTime) {
        matrix.scale(1.2F, 1.2F, 1.2F);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityHippogryph entity) {
        return entity.isBlinking() ? entity.getEnumVariant().TEXTURE_BLINK : entity.getEnumVariant().TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    private class LayerHippogriffSaddle extends LayerRenderer<EntityHippogryph, ModelHippogryph> {
        private final RenderHippogryph renderer;
        private final RenderType SADDLE_TEXTURE = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippogryph/saddle.png"));
        private final RenderType BRIDLE = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippogryph/bridle.png"));
        private final RenderType CHEST = RenderType.getEntityTranslucent(new ResourceLocation("iceandfire:textures/models/hippogryph/chest.png"));
        private final RenderType TEXTURE_DIAMOND = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippogryph/armor_diamond.png"));
        private final RenderType TEXTURE_GOLD = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippogryph/armor_gold.png"));
        private final RenderType TEXTURE_IRON = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippogryph/armor_iron.png"));


        public LayerHippogriffSaddle(RenderHippogryph renderer) {
            super(renderer);
            this.renderer = renderer;
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityHippogryph hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.getArmor() != 0) {
                RenderType type = null;
                switch (hippo.getArmor()) {
                    case 1:
                        type = TEXTURE_IRON;
                        break;
                    case 2:
                        type = TEXTURE_GOLD;
                        break;
                    case 3:
                        type = TEXTURE_DIAMOND;
                        break;
                }
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(type);
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled()) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(SADDLE_TEXTURE);
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isSaddled() && hippo.getControllingPassenger() != null) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(BRIDLE);
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (hippo.isChested()) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(CHEST);
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
