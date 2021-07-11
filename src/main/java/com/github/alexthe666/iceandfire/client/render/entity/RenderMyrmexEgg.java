package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMyrmexEgg extends LivingRenderer<EntityMyrmexEgg, SegmentedModel<EntityMyrmexEgg>> {

    public static final ResourceLocation EGG_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_egg.png");
    public static final ResourceLocation EGG_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_egg.png");

    public RenderMyrmexEgg(EntityRendererManager renderManager) {
        super(renderManager, new ModelDragonEgg(), 0.3F);
    }

    protected boolean canRenderName(EntityMyrmexEgg entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityMyrmexEgg entity) {
        return entity.isJungle() ? EGG_JUNGLE : EGG_DESERT;
    }

}
