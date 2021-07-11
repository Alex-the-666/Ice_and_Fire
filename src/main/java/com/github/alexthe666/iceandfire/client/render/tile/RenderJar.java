package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RenderJar<T extends TileEntityJar> extends TileEntityRenderer<T> {

    public static final RenderType TEXTURE_0 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_0, false);
    public static final RenderType TEXTURE_1 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_1, false);
    public static final RenderType TEXTURE_2 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_2, false);
    public static final RenderType TEXTURE_3 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_3, false);
    public static final RenderType TEXTURE_4 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_4, false);
    public static final RenderType TEXTURE_5 = RenderType.getEntityCutoutNoCull(RenderPixie.TEXTURE_5, false);
    public static final RenderType TEXTURE_0_GLO = RenderType.getEyes(RenderPixie.TEXTURE_0);
    public static final RenderType TEXTURE_1_GLO = RenderType.getEyes(RenderPixie.TEXTURE_1);
    public static final RenderType TEXTURE_2_GLO = RenderType.getEyes(RenderPixie.TEXTURE_2);
    public static final RenderType TEXTURE_3_GLO = RenderType.getEyes(RenderPixie.TEXTURE_3);
    public static final RenderType TEXTURE_4_GLO = RenderType.getEyes(RenderPixie.TEXTURE_4);
    public static final RenderType TEXTURE_5_GLO = RenderType.getEyes(RenderPixie.TEXTURE_5);
    private static ModelPixie MODEL_PIXIE;

    public RenderJar(TileEntityRendererDispatcher p_i226016_1_) {
        super(p_i226016_1_);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int meta = 0;
        boolean hasPixie = false;
        if(MODEL_PIXIE == null){
            MODEL_PIXIE = new ModelPixie();
        }
        if (entity != null && entity.getWorld() != null) {
            meta = entity.pixieType;
            hasPixie = entity.hasPixie;
        }
        if (hasPixie) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5F, 1.501F, 0.5F);
            matrixStackIn.rotate(new Quaternion(Vector3f.XP, 180, true));
            matrixStackIn.push();
            RenderType type = TEXTURE_0;
            RenderType typeGlow = TEXTURE_0_GLO;
            switch (meta) {
                default:
                    type = TEXTURE_0;
                    typeGlow = TEXTURE_0_GLO;
                    break;
                case 1:
                    type = TEXTURE_1;
                    typeGlow = TEXTURE_1_GLO;
                    break;
                case 2:
                    type = TEXTURE_2;
                    typeGlow = TEXTURE_2_GLO;
                    break;
                case 3:
                    type = TEXTURE_3;
                    typeGlow = TEXTURE_3_GLO;
                    break;
                case 4:
                    type = TEXTURE_4;
                    typeGlow = TEXTURE_4_GLO;
                    break;
            }
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(type);
            if (entity != null && entity.getWorld() != null) {

                if (entity.hasProduced) {
                    matrixStackIn.translate(0F, 0.90F, 0F);
                } else {
                    matrixStackIn.translate(0F, 0.60F, 0F);
                }
                matrixStackIn.rotate(new Quaternion(Vector3f.YP, this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), true));
                matrixStackIn.scale(0.50F, 0.50F, 0.50F);
                MODEL_PIXIE.animateInJar(entity.hasProduced, entity, 0);
                MODEL_PIXIE.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                MODEL_PIXIE.render(matrixStackIn, bufferIn.getBuffer(typeGlow), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f;

        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }


}
