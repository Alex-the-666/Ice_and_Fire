package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerTrollEyes;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerTrollWeapon;
import com.github.alexthe666.iceandfire.entity.EntityTroll;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTroll extends MobRenderer<EntityTroll, ModelTroll> {

    public RenderTroll(EntityRendererManager renderManager) {
        super(renderManager, new ModelTroll(), 0.9F);
        this.layerRenderers.add(new LayerTrollWeapon(this));
        this.layerRenderers.add(new LayerTrollEyes(this));
    }

    @Override
    public ResourceLocation getEntityTexture(EntityTroll troll) {
        return troll.getTrollType().TEXTURE;
    }
}
