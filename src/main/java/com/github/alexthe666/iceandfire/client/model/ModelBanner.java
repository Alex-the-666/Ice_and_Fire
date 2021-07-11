package com.github.alexthe666.iceandfire.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBanner extends SegmentedModel {
    public final ModelRenderer field_228833_a_ = getModelRender();
    public final ModelRenderer field_228834_c_ = new ModelRenderer(64, 64, 44, 0);
    public final ModelRenderer field_228835_d_;

    public ModelBanner() {
        this.textureHeight = 64;
        this.textureWidth = 64;
        this.field_228834_c_.addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
        this.field_228835_d_ = new ModelRenderer(64, 64, 0, 42);
        this.field_228835_d_.addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
    }

    public static ModelRenderer getModelRender() {
        ModelRenderer modelrenderer = new ModelRenderer(64, 64, 0, 0);
        modelrenderer.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
        return modelrenderer;
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(field_228833_a_, field_228834_c_, field_228835_d_);
    }

}
