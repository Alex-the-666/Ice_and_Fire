package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadHorse;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.util.ResourceLocation;

public class RenderDreadHorse extends MobRenderer<EntityDreadHorse, HorseModel<EntityDreadHorse>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_horse.png");
    public static final ResourceLocation TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_horse_eyes.png");
    private final float scale;

    public RenderDreadHorse(EntityRendererManager manager) {
        this(manager, 1.0F);
    }

    public RenderDreadHorse(EntityRendererManager renderManagerIn, float scaleIn) {
        super(renderManagerIn, new HorseModel<>(0), 0.75F);
        this.addLayer(new LayerGenericGlowing(this, TEXTURE_EYES));
        this.scale = scaleIn;
    }

    public ResourceLocation getEntityTexture(EntityDreadHorse entity) {
        return TEXTURE;
    }
}
