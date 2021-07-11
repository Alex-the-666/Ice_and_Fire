package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.model.ModelPixieHouse;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RenderPixieHouse<T extends TileEntityPixieHouse> extends TileEntityRenderer<T> {

    private static final ModelPixieHouse MODEL = new ModelPixieHouse();
    private static ModelPixie MODEL_PIXIE;
    private static final RenderType TEXTURE_0 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_0.png"), false);
    private static final RenderType TEXTURE_1 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_1.png"), false);
    private static final RenderType TEXTURE_2 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_2.png"), false);
    private static final RenderType TEXTURE_3 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_3.png"), false);
    private static final RenderType TEXTURE_4 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_4.png"), false);
    private static final RenderType TEXTURE_5 = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/pixie/house/pixie_house_5.png"), false);
    public BlockItem metaOverride;

    public RenderPixieHouse(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int rotation = 0;
        int meta = 0;
        if(MODEL_PIXIE == null){
            MODEL_PIXIE = new ModelPixie();
        }
        if (entity != null && entity.getWorld() != null && entity.getWorld().getBlockState(entity.getPos()).getBlock() instanceof BlockPixieHouse) {
            meta = TileEntityPixieHouse.getHouseTypeFromBlock(entity.getWorld().getBlockState(entity.getPos()).getBlock());
            if (entity.getWorld().getBlockState(entity.getPos()).get(BlockPixieHouse.FACING) == Direction.NORTH) {
                rotation = 180;
            }
            if (entity.getWorld().getBlockState(entity.getPos()).get(BlockPixieHouse.FACING) == Direction.EAST) {
                rotation = -90;
            }
            if (entity.getWorld().getBlockState(entity.getPos()).get(BlockPixieHouse.FACING) == Direction.WEST) {
                rotation = 90;
            }

        }
        if(entity == null){
            meta = TileEntityPixieHouse.getHouseTypeFromBlock(metaOverride.getBlock());
        }
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 1.501F, 0.5F);
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
        if (entity != null && entity.getWorld() != null && entity.hasPixie) {
            matrixStackIn.push();
            matrixStackIn.translate(0F, 0.95F, 0F);
            matrixStackIn.scale(0.55F, 0.55F, 0.55F);
            matrixStackIn.push();
            //GL11.glRotatef(MathHelper.clampAngle(entity.ticksExisted * 3), 0, 1, 0);
            RenderType type = RenderJar.TEXTURE_0;
            RenderType type2 = RenderJar.TEXTURE_0_GLO;
            switch (entity.pixieType) {
                default:
                    type = RenderJar.TEXTURE_0;
                    type2 = RenderJar.TEXTURE_0_GLO;
                    break;
                case 1:
                    type = RenderJar.TEXTURE_1;
                    type2 = RenderJar.TEXTURE_1_GLO;
                    break;
                case 2:
                    type = RenderJar.TEXTURE_2;
                    type2 = RenderJar.TEXTURE_2_GLO;
                    break;
                case 3:
                    type = RenderJar.TEXTURE_3;
                    type2 = RenderJar.TEXTURE_3_GLO;
                    break;
                case 4:
                    type = RenderJar.TEXTURE_4;
                    type2 = RenderJar.TEXTURE_4_GLO;
                    break;
                case 5:
                    type = RenderJar.TEXTURE_5;
                    type2 = RenderJar.TEXTURE_5_GLO;
                    break;
            }
            matrixStackIn.push();
            MODEL_PIXIE.animateInHouse(entity);
            MODEL_PIXIE.render(matrixStackIn, bufferIn.getBuffer(type), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            MODEL_PIXIE.render(matrixStackIn, bufferIn.getBuffer(type2), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
        RenderType pixieType = TEXTURE_0;
        switch (meta) {
            case 0:
                pixieType = TEXTURE_0;
                break;
            case 1:
                pixieType = TEXTURE_1;
                break;
            case 2:
                pixieType = TEXTURE_2;
                break;
            case 3:
                pixieType = TEXTURE_3;
                break;
            case 4:
                pixieType = TEXTURE_4;
                break;
            case 5:
                pixieType = TEXTURE_5;
                break;
        }
        matrixStackIn.push();
        MODEL.render(matrixStackIn, bufferIn.getBuffer(pixieType), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        matrixStackIn.pop();
        matrixStackIn.pop();
    }
}
