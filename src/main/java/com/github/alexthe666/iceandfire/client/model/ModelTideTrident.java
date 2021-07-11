package com.github.alexthe666.iceandfire.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTideTrident extends SegmentedModel {
    public ModelRenderer shaft;
    public ModelRenderer base;
    public ModelRenderer blade_B;
    public ModelRenderer blade_C;
    public ModelRenderer blade_A;
    public ModelRenderer fins;
    public ModelRenderer blade_C_2;
    public ModelRenderer blade_A_2;

    public ModelTideTrident() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.fins = new ModelRenderer(this, 5, 12);
        this.fins.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.fins.addBox(-5.5F, -1.0F, 0.0F, 11, 7, 0, 0.0F);
        this.blade_A_2 = new ModelRenderer(this, 28, 0);
        this.blade_A_2.mirror = true;
        this.blade_A_2.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.blade_A_2.addBox(0.5F, -2.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(blade_A_2, 0.0F, 0.0F, 0.03490658503988659F);
        this.shaft = new ModelRenderer(this, 0, 0);
        this.shaft.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.shaft.addBox(-0.5F, -11.0F, -0.5F, 1, 24, 1, 0.0F);
        this.blade_A = new ModelRenderer(this, 17, 0);
        this.blade_A.setRotationPoint(-1.4F, 0.0F, 0.0F);
        this.blade_A.addBox(-0.5F, -5.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(blade_A, 0.0F, 0.0F, -0.3839724354387525F);
        this.base = new ModelRenderer(this, 5, 5);
        this.base.setRotationPoint(0.0F, -12.0F, 0.0F);
        this.base.addBox(-1.5F, -2.0F, -0.5F, 3, 3, 1, 0.0F);
        this.setRotateAngle(base, 0.0F, 1.5707963267948966F, 0.0F);
        this.blade_B = new ModelRenderer(this, 23, 0);
        this.blade_B.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.blade_B.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1, 0.0F);
        this.blade_C = new ModelRenderer(this, 17, 0);
        this.blade_C.mirror = true;
        this.blade_C.setRotationPoint(1.4F, 0.0F, 0.0F);
        this.blade_C.addBox(-0.5F, -5.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(blade_C, 0.0F, 0.0F, 0.3839724354387525F);
        this.blade_C_2 = new ModelRenderer(this, 28, 0);
        this.blade_C_2.mirror = true;
        this.blade_C_2.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.blade_C_2.addBox(-1.5F, -2.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(blade_C_2, 0.0F, 0.0F, 0.03490658503988659F);
        this.base.addChild(this.fins);
        this.blade_A.addChild(this.blade_A_2);
        this.base.addChild(this.blade_A);
        this.shaft.addChild(this.base);
        this.base.addChild(this.blade_B);
        this.base.addChild(this.blade_C);
        this.blade_C.addChild(this.blade_C_2);
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(shaft);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
