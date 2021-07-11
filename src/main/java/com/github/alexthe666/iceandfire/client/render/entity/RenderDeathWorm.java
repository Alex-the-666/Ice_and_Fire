package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.client.model.ModelDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDeathWorm extends MobRenderer<EntityDeathWorm, ModelDeathWorm> {
    public static final ResourceLocation TEXTURE_RED = new ResourceLocation("iceandfire:textures/models/deathworm/deathworm_red.png");
    public static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("iceandfire:textures/models/deathworm/deathworm_white.png");
    public static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation("iceandfire:textures/models/deathworm/deathworm_yellow.png");

    public RenderDeathWorm(EntityRendererManager renderManager) {
        super(renderManager, new ModelDeathWorm(), 0);
    }

    @Override
    protected void preRenderCallback(EntityDeathWorm entity, MatrixStack matrixStackIn, float partialTickTime) {
        this.shadowSize = entity.getRenderScale() / 3;
        matrixStackIn.scale(entity.getRenderScale(), entity.getRenderScale(), entity.getRenderScale());
    }


    protected int getBlockLight(EntityDeathWorm entityIn, BlockPos partialTicks) {
        return entityIn.isBurning() ? 15 : entityIn.getWormBrightness(false);
    }

    protected int getSkyLight(EntityDeathWorm entity, BlockPos pos) {
        return entity.getWormBrightness(true);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(EntityDeathWorm entity) {
        return entity.getVariant() == 2 ? TEXTURE_WHITE : entity.getVariant() == 1 ? TEXTURE_RED : TEXTURE_YELLOW;
    }
}
