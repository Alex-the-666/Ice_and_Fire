package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDragonEgg extends LivingRenderer<EntityDragonEgg, SegmentedModel<EntityDragonEgg>> {

    public static final ResourceLocation EGG_RED = new ResourceLocation("iceandfire:textures/models/firedragon/egg_red.png");
    public static final ResourceLocation EGG_GREEN = new ResourceLocation("iceandfire:textures/models/firedragon/egg_green.png");
    public static final ResourceLocation EGG_BRONZE = new ResourceLocation("iceandfire:textures/models/firedragon/egg_bronze.png");
    public static final ResourceLocation EGG_GREY = new ResourceLocation("iceandfire:textures/models/firedragon/egg_gray.png");
    public static final ResourceLocation EGG_BLUE = new ResourceLocation("iceandfire:textures/models/icedragon/egg_blue.png");
    public static final ResourceLocation EGG_WHITE = new ResourceLocation("iceandfire:textures/models/icedragon/egg_white.png");
    public static final ResourceLocation EGG_SAPPHIRE = new ResourceLocation("iceandfire:textures/models/icedragon/egg_sapphire.png");
    public static final ResourceLocation EGG_SILVER = new ResourceLocation("iceandfire:textures/models/icedragon/egg_silver.png");
    public static final ResourceLocation EGG_ELECTRIC = new ResourceLocation("iceandfire:textures/models/lightningdragon/egg_electric.png");
    public static final ResourceLocation EGG_AMYTHEST = new ResourceLocation("iceandfire:textures/models/lightningdragon/egg_amythest.png");
    public static final ResourceLocation EGG_BLACK = new ResourceLocation("iceandfire:textures/models/lightningdragon/egg_black.png");
    public static final ResourceLocation EGG_COPPER = new ResourceLocation("iceandfire:textures/models/lightningdragon/egg_copper.png");

    public RenderDragonEgg(EntityRendererManager renderManager) {
        super(renderManager, new ModelDragonEgg(), 0.3F);
    }

    protected boolean canRenderName(EntityDragonEgg entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }


    @Override
    public ResourceLocation getEntityTexture(EntityDragonEgg entity) {
        switch (entity.getEggType()) {
            default:
                return EGG_RED;
            case GREEN:
                return EGG_GREEN;
            case BRONZE:
                return EGG_BRONZE;
            case GRAY:
                return EGG_GREY;
            case BLUE:
                return EGG_BLUE;
            case WHITE:
                return EGG_WHITE;
            case SAPPHIRE:
                return EGG_SAPPHIRE;
            case SILVER:
                return EGG_SILVER;
            case ELECTRIC:
                return EGG_ELECTRIC;
            case AMYTHEST:
                return EGG_AMYTHEST;
            case COPPER:
                return EGG_COPPER;
            case BLACK:
                return EGG_BLACK;

        }
    }

}
