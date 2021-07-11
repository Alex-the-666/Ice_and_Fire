package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class RenderTrollWeapon {
    private static final ModelTrollWeapon MODEL = new ModelTrollWeapon();

    public RenderTrollWeapon() {
    }

    public void renderItem(EnumTroll.Weapon weapon, MatrixStack stackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        stackIn.push();
        stackIn.translate(0.5F, -0.75F, 0.5F);
        MODEL.render(stackIn, bufferIn.getBuffer(RenderType.getEntityCutout(weapon.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        stackIn.pop();
    }
}
