package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.iceandfire.client.render.TabulaModelAccessor;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class LayerDragonEyes extends LayerRenderer<EntityDragonBase, SegmentedModel<EntityDragonBase>> {
    private final MobRenderer render;
    private TabulaModel fireHead;
    private TabulaModel iceHead;
    private TabulaModel lightningHead;
    public LayerDragonEyes(MobRenderer renderIn) {
        super(renderIn);
        this.render = renderIn;
        try{
            fireHead = onlyKeepCubes(new TabulaModelAccessor(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/firedragon/dragonFireGround"),null),
                    Collections.singletonList("HeadFront"));
            iceHead = onlyKeepCubes(new TabulaModelAccessor(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/icedragon/dragonIceGround"),null),
                    Collections.singletonList("HeadFront"));
            lightningHead = onlyKeepCubes(new TabulaModelAccessor(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/lightningdragon/dragonLightningGround"),null),
                    Collections.singletonList("HeadFront"));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityDragonBase dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (dragon.shouldRenderEyes()) {
            RenderType eyes = RenderType.getEyes(EnumDragonTextures.getEyeTextureFromDragon(dragon));
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(eyes);
            if (dragon instanceof EntityLightningDragon && lightningHead !=null){
                copyPositions(lightningHead,(TabulaModel)this.getEntityModel());
                lightningHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            else if(dragon instanceof EntityIceDragon && iceHead != null){
                copyPositions(iceHead,(TabulaModel)this.getEntityModel());
                iceHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            else if(fireHead != null){
                copyPositions(fireHead,(TabulaModel)this.getEntityModel());
                fireHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            //Fallback method
            else {
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDragonBase entityIn) {
        return null;
    }

    //Removes all cubes except the cube names specified by the string list and their parents
    //We need to keep the parents to correctly render the head position
    private TabulaModel onlyKeepCubes(TabulaModelAccessor model, List<String> strings){
        List<AdvancedModelBox> keepCubes = new ArrayList<>();
        for (String str : strings){
            AdvancedModelBox cube = model.getCube(str);
            keepCubes.add(cube);
            while (cube.getParent() != null){
                keepCubes.add(cube.getParent());
                cube = cube.getParent();
            }
        }
        removeChildren(model,keepCubes);
        model.getCubes().values().removeIf(advancedModelBox -> !keepCubes.contains(advancedModelBox));
        return model;
    }

    private void removeChildren(TabulaModelAccessor model, List<AdvancedModelBox> keepCubes){
        model.getRootBox().forEach(modelRenderer -> {
            modelRenderer.childModels.removeIf(child -> !keepCubes.contains(child));
            modelRenderer.childModels.forEach(childModel ->{
                removeChildren((AdvancedModelBox) childModel,keepCubes);
            });
        });
    }

    private void removeChildren(AdvancedModelBox modelBox, List<AdvancedModelBox> keepCubes){
        modelBox.childModels.removeIf(modelRenderer -> !keepCubes.contains(modelRenderer));
        modelBox.childModels.forEach(modelRenderer -> {
            removeChildren((AdvancedModelBox)modelRenderer,keepCubes);
        });
    }

    public boolean isAngleEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose != null && pose.rotateAngleX == original.rotateAngleX && pose.rotateAngleY == original.rotateAngleY && pose.rotateAngleZ == original.rotateAngleZ;
    }
    public boolean isPositionEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose.rotationPointX == original.rotationPointX && pose.rotationPointY == original.rotationPointY && pose.rotationPointZ == original.rotationPointZ;
    }

    public void copyPositions(TabulaModel model, TabulaModel modelTo) {
        for (AdvancedModelBox cube : model.getCubes().values()) {
            AdvancedModelBox modelToCube = modelTo.getCube(cube.boxName);
            if (!isAngleEqual(cube,modelToCube)) {
                cube.rotateAngleX = modelToCube.rotateAngleX;
                cube.rotateAngleY = modelToCube.rotateAngleY;
                cube.rotateAngleZ = modelToCube.rotateAngleZ;
            }
            if (!isPositionEqual(cube,modelToCube)) {
                cube.rotationPointX = modelToCube.rotationPointX;
                cube.rotationPointY = modelToCube.rotationPointY;
                cube.rotationPointZ= modelToCube.rotationPointZ;
            }

        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}