package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.client.model.ModelHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHippocampus extends MobRenderer<EntityHippocampus, ModelHippocampus> {

    private static final ResourceLocation VARIANT_0 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_0.png");
    private static final ResourceLocation VARIANT_0_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_0_blinking.png");
    private static final ResourceLocation VARIANT_1 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_1.png");
    private static final ResourceLocation VARIANT_1_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_1_blinking.png");
    private static final ResourceLocation VARIANT_2 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_2.png");
    private static final ResourceLocation VARIANT_2_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_2_blinking.png");
    private static final ResourceLocation VARIANT_3 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_3.png");
    private static final ResourceLocation VARIANT_3_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_3_blinking.png");
    private static final ResourceLocation VARIANT_4 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_4.png");
    private static final ResourceLocation VARIANT_4_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_4_blinking.png");
    private static final ResourceLocation VARIANT_5 = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_5.png");
    private static final ResourceLocation VARIANT_5_BLINK = new ResourceLocation("iceandfire:textures/models/hippocampus/hippocampus_5_blinking.png");


    public RenderHippocampus(EntityRendererManager renderManager) {
        super(renderManager, new ModelHippocampus(), 0.8F);
        this.layerRenderers.add(new RenderHippocampus.LayerHippocampusRainbow(this));
        this.layerRenderers.add(new RenderHippocampus.LayerHippocampusSaddle(this));
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityHippocampus entity) {
        switch (entity.getVariant()) {
            default:
                return entity.isBlinking() ? VARIANT_0_BLINK : VARIANT_0;
            case 1:
                return entity.isBlinking() ? VARIANT_1_BLINK : VARIANT_1;
            case 2:
                return entity.isBlinking() ? VARIANT_2_BLINK : VARIANT_2;
            case 3:
                return entity.isBlinking() ? VARIANT_3_BLINK : VARIANT_3;
            case 4:
                return entity.isBlinking() ? VARIANT_4_BLINK : VARIANT_4;
            case 5:
                return entity.isBlinking() ? VARIANT_5_BLINK : VARIANT_5;

        }
    }

    @OnlyIn(Dist.CLIENT)
    private class LayerHippocampusSaddle extends LayerRenderer<EntityHippocampus, ModelHippocampus> {
        private final RenderHippocampus renderer;
        private final RenderType SADDLE_TEXTURE = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippocampus/saddle.png"));
        private final RenderType BRIDLE = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippocampus/bridle.png"));
        private final RenderType CHEST = RenderType.getEntityTranslucent(new ResourceLocation("iceandfire:textures/models/hippocampus/chest.png"));
        private final RenderType TEXTURE_DIAMOND = RenderType.getEntityCutout(new ResourceLocation("iceandfire:textures/models/hippocampus/armor_diamond.png"));
        private final RenderType TEXTURE_GOLD = RenderType.getEntityCutout(new ResourceLocation("iceandfire:textures/models/hippocampus/armor_gold.png"));
        private final RenderType TEXTURE_IRON = RenderType.getEntityCutout(new ResourceLocation("iceandfire:textures/models/hippocampus/armor_iron.png"));

        public LayerHippocampusSaddle(RenderHippocampus renderer) {
            super(renderer);
            this.renderer = renderer;
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityHippocampus hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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
        }
    }

    private class LayerHippocampusRainbow extends LayerRenderer<EntityHippocampus, ModelHippocampus> {
        private final RenderHippocampus renderer;
        private final RenderType TEXTURE = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippocampus/rainbow.png"));
        private final RenderType TEXTURE_BLINK = RenderType.getEntityNoOutline(new ResourceLocation("iceandfire:textures/models/hippocampus/rainbow_blink.png"));

        public LayerHippocampusRainbow(RenderHippocampus renderer) {
            super(renderer);
            this.renderer = renderer;
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityHippocampus hippo, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (hippo.hasCustomName() && hippo.getCustomName().toString().toLowerCase().contains("rainbow")) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(hippo.isBlinking() ? TEXTURE_BLINK : TEXTURE);
                int i1 = 25;
                int i = hippo.ticksExisted / 25 + hippo.getEntityId();
                int j = DyeColor.values().length;
                int k = i % j;
                int l = (i + 1) % j;
                float f = ((float) (hippo.ticksExisted % 25) + partialTicks) / 25.0F;
                float[] afloat1 = SheepEntity.getDyeRgb(DyeColor.byId(k));
                float[] afloat2 = SheepEntity.getDyeRgb(DyeColor.byId(l));
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(hippo, 0.0F), afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f, 1.0F);
            }
        }
    }
}
