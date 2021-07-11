package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelMyrmexPupa;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerMyrmexItem;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMyrmexBase extends MobRenderer<EntityMyrmexBase, SegmentedModel<EntityMyrmexBase>> {

    private static final SegmentedModel<EntityMyrmexBase> LARVA_MODEL = new ModelMyrmexPupa();
    private static final SegmentedModel<EntityMyrmexBase> PUPA_MODEL = new ModelMyrmexPupa();
    private SegmentedModel<EntityMyrmexBase> adultModel;

    public RenderMyrmexBase(EntityRendererManager renderManager, SegmentedModel model, float shadowSize) {
        super(renderManager, model, shadowSize);
        this.addLayer(new LayerMyrmexItem(this));
        this.adultModel = model;
    }

    public void render(EntityMyrmexBase entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.getGrowthStage() == 0) {
            entityModel = LARVA_MODEL;
        }else if (entityIn.getGrowthStage() == 1) {
            entityModel = PUPA_MODEL;
        }else{
            entityModel = adultModel;
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }
        @Override
    protected void preRenderCallback(EntityMyrmexBase myrmex, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = myrmex.getModelScale();
        if (myrmex.getGrowthStage() == 0) {
            scale /= 2;
        }
        if (myrmex.getGrowthStage() == 1) {
            scale /= 1.5F;
        }
        matrixStackIn.scale(scale, scale, scale);
        if (myrmex.isPassenger() && myrmex.getGrowthStage() < 2) {
            matrixStackIn.rotate(new Quaternion(Vector3f.YP, 90, true));
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityMyrmexBase myrmex) {
        return myrmex.getTexture();
    }

}
