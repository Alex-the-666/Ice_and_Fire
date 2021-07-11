package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.model.ModelRenderer;

public abstract class ModelMyrmexBase<T extends EntityMyrmexBase> extends ModelDragonBase<T> {
    private static final ModelMyrmexLarva LARVA_MODEL = new ModelMyrmexLarva();
    private static final ModelMyrmexPupa PUPA_MODEL = new ModelMyrmexPupa();

    public void postRenderArm(float scale, MatrixStack stackIn) {
        for (ModelRenderer renderer : this.getHeadParts()) {
            renderer.translateRotate(stackIn);
        }
    }

    public abstract ModelRenderer[] getHeadParts();
}
