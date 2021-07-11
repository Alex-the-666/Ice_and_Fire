package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class LayerSeaSerpentAncient extends LayerRenderer<EntitySeaSerpent, SegmentedModel<EntitySeaSerpent>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/seaserpent/ancient_overlay.png");
    private static final ResourceLocation TEXTURE_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/ancient_overlay_blink.png");
    private MobRenderer<EntitySeaSerpent, SegmentedModel<EntitySeaSerpent>> renderer;

    public LayerSeaSerpentAncient(MobRenderer<EntitySeaSerpent, SegmentedModel<EntitySeaSerpent>> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntitySeaSerpent serpent, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (serpent.isAncient()) {
            RenderType tex = RenderType.getEntityNoOutline(serpent.isBlinking() ? TEXTURE_BLINK : TEXTURE);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(tex);
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        }
    }
}
