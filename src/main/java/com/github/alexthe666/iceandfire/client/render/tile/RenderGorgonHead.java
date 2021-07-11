package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelGorgonHead;
import com.github.alexthe666.iceandfire.client.model.ModelGorgonHeadActive;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.ResourceLocation;

public class RenderGorgonHead {

    private static final RenderType ACTIVE_TEXTURE = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/gorgon/head_active.png"), false);
    private static final RenderType INACTIVE_TEXTURE = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/gorgon/head_inactive.png"), false);
    private static final SegmentedModel ACTIVE_MODEL = new ModelGorgonHeadActive();
    private static final SegmentedModel INACTIVE_MODEL = new ModelGorgonHead();
    private final boolean active;

    public RenderGorgonHead(boolean alive) {
        this.active = alive;
    }

    public void render(MatrixStack stackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        SegmentedModel model = active ? ACTIVE_MODEL : INACTIVE_MODEL;
        stackIn.push();
        stackIn.translate(0.5F, active ?  1.5F : 1.25F, 0.5F);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(active ? ACTIVE_TEXTURE : INACTIVE_TEXTURE);
        model.render(stackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        stackIn.pop();
    }

}
