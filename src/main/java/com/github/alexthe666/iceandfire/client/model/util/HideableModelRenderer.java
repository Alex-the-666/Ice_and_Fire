package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class HideableModelRenderer extends AdvancedModelBox {

    public boolean invisible;
    private int displayList;
    private boolean compiled;

    public HideableModelRenderer(AdvancedEntityModel model, String name) {
        super(model, name);
    }

    public HideableModelRenderer(AdvancedEntityModel model, int i, int i1) {
        super(model, i, i1);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (!invisible) {
            if (this.showModel) {
                super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }
    }
}
