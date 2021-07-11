package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerSeaSerpentAncient;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.ResourceLocation;

public class RenderSeaSerpent extends MobRenderer<EntitySeaSerpent, SegmentedModel<EntitySeaSerpent>> {

    public static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_blue.png");
    public static final ResourceLocation TEXTURE_BLUE_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_blue_blink.png");
    public static final ResourceLocation TEXTURE_BRONZE = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_bronze.png");
    public static final ResourceLocation TEXTURE_BRONZE_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_bronze_blink.png");
    public static final ResourceLocation TEXTURE_DARKBLUE = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_darkblue.png");
    public static final ResourceLocation TEXTURE_DARKBLUE_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_darkblue_blink.png");
    public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_green.png");
    public static final ResourceLocation TEXTURE_GREEN_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_green_blink.png");
    public static final ResourceLocation TEXTURE_PURPLE = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_purple.png");
    public static final ResourceLocation TEXTURE_PURPLE_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_purple_blink.png");
    public static final ResourceLocation TEXTURE_RED = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_red.png");
    public static final ResourceLocation TEXTURE_RED_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_red_blink.png");
    public static final ResourceLocation TEXTURE_TEAL = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_teal.png");
    public static final ResourceLocation TEXTURE_TEAL_BLINK = new ResourceLocation("iceandfire:textures/models/seaserpent/seaserpent_teal_blink.png");

    public RenderSeaSerpent(EntityRendererManager renderManager, SegmentedModel model) {
        super(renderManager, model, 1.6F);
        this.layerRenderers.add(new LayerSeaSerpentAncient(this));

    }

    @Override
    protected void preRenderCallback(EntitySeaSerpent entity, MatrixStack matrixStackIn, float partialTickTime) {
        this.shadowSize = entity.getSeaSerpentScale();
        matrixStackIn.scale(shadowSize, shadowSize, shadowSize);
    }

    @Override
    public ResourceLocation getEntityTexture(EntitySeaSerpent serpent) {
        switch (serpent.getVariant()) {
            case 0:
                if (serpent.isBlinking()) {
                    return TEXTURE_BLUE_BLINK;
                } else {
                    return TEXTURE_BLUE;
                }
            case 1:
                if (serpent.isBlinking()) {
                    return TEXTURE_BRONZE_BLINK;
                } else {
                    return TEXTURE_BRONZE;
                }
            case 2:
                if (serpent.isBlinking()) {
                    return TEXTURE_DARKBLUE_BLINK;
                } else {
                    return TEXTURE_DARKBLUE;
                }
            case 3:
                if (serpent.isBlinking()) {
                    return TEXTURE_GREEN_BLINK;
                } else {
                    return TEXTURE_GREEN;
                }
            case 4:
                if (serpent.isBlinking()) {
                    return TEXTURE_PURPLE_BLINK;
                } else {
                    return TEXTURE_PURPLE;
                }
            case 5:
                if (serpent.isBlinking()) {
                    return TEXTURE_RED_BLINK;
                } else {
                    return TEXTURE_RED;
                }
            case 6:
                if (serpent.isBlinking()) {
                    return TEXTURE_TEAL_BLINK;
                } else {
                    return TEXTURE_TEAL;
                }
        }
        return TEXTURE_BLUE;
    }

}
