package com.github.alexthe666.iceandfire.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTrollWeapon extends SegmentedModel {
    public ModelRenderer log1;
    public ModelRenderer log2;
    public ModelRenderer handle;
    public ModelRenderer column;
    public ModelRenderer blade1;
    public ModelRenderer blade2;
    public ModelRenderer blade2_1;
    public ModelRenderer block;
    public ModelRenderer blade2_2;
    public ModelRenderer bottom;
    public ModelRenderer top;

    public ModelTrollWeapon() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.block = new ModelRenderer(this, 182, 19);
        this.block.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.block.addBox(-2.0F, 11.0F, -8.0F, 4, 10, 15, 0.0F);
        this.bottom = new ModelRenderer(this, 177, 0);
        this.bottom.setRotationPoint(-1.0F, 0.0F, 0.0F);
        this.bottom.addBox(-1.5F, -21.0F, -3.5F, 10, 4, 10, 0.0F);
        this.blade2 = new ModelRenderer(this, 186, 66);
        this.blade2.setRotationPoint(0.0F, 7.0F, -1.0F);
        this.blade2.addBox(-1.0F, 1.0F, -1.0F, 2, 2, 9, 0.0F);
        this.column = new ModelRenderer(this, 220, 28);
        this.column.setRotationPoint(-2.0F, 0.0F, -3.0F);
        this.column.addBox(-1.5F, -20.0F, -2.5F, 8, 42, 8, 0.0F);
        this.blade1 = new ModelRenderer(this, 186, 84);
        this.blade1.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.blade1.addBox(-1.0F, 12.0F, 0.0F, 2, 10, 10, 0.0F);
        this.handle = new ModelRenderer(this, 232, 80);
        this.handle.setRotationPoint(0.3F, 0.0F, 1.0F);
        this.handle.addBox(-1.5F, -20.0F, -1.5F, 3, 42, 3, 0.0F);
        this.blade2_1 = new ModelRenderer(this, 189, 69);
        this.blade2_1.setRotationPoint(0.0F, 2.0F, -1.0F);
        this.blade2_1.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 6, 0.0F);
        this.top = new ModelRenderer(this, 177, 0);
        this.top.setRotationPoint(-1.0F, 0.0F, 0.0F);
        this.top.addBox(-1.5F, 20.0F, -3.5F, 10, 4, 10, 0.0F);
        this.log1 = new ModelRenderer(this, 10, 100);
        this.log1.setRotationPoint(-0.5F, 8.0F, -0.4F);
        this.log1.addBox(-1.5F, -17.9F, -3.5F, 6, 20, 6, 0.0F);
        this.setRotateAngle(log1, 3.141592653589793F, 0.0F, 0.0F);
        this.log2 = new ModelRenderer(this, 10, 70);
        this.log2.setRotationPoint(0.0F, 9.0F, -1.0F);
        this.log2.addBox(-2.0F, -6.9F, -3.0F, 7, 24, 6, 0.0F);
        this.setRotateAngle(log2, -0.045553093477052F, 0.0F, 0.0F);
        this.blade2_2 = new ModelRenderer(this, 161, 99);
        this.blade2_2.setRotationPoint(0.0F, 17.0F, 9.0F);
        this.blade2_2.addBox(-1.0F, -5.0F, -6.0F, 2, 5, 5, 0.0F);
        this.setRotateAngle(blade2_2, 3.141592653589793F, 0.0F, 0.0F);
        this.handle.addChild(this.block);
        this.column.addChild(this.bottom);
        this.handle.addChild(this.blade2);
        this.log1.addChild(this.column);
        this.handle.addChild(this.blade1);
        this.log1.addChild(this.handle);
        this.handle.addChild(this.blade2_1);
        this.column.addChild(this.top);
        this.log1.addChild(this.log2);
        this.blade1.addChild(this.blade2_2);
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(log1);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
