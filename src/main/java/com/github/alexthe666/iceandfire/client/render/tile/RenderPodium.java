package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RenderPodium<T extends TileEntityPodium> extends TileEntityRenderer<T> {

    public RenderPodium(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    protected static RenderType getEggTexture(EnumDragonEgg type) {
        switch (type) {
            default:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_RED);
            case GREEN:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_GREEN);
            case BRONZE:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_BRONZE);
            case GRAY:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_GREY);
            case BLUE:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_BLUE);
            case WHITE:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_WHITE);
            case SAPPHIRE:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_SAPPHIRE);
            case SILVER:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_SILVER);
            case ELECTRIC:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_ELECTRIC);
            case AMYTHEST:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_AMYTHEST);
            case COPPER:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_COPPER);
            case BLACK:
                return RenderType.getEntityCutout(RenderDragonEgg.EGG_BLACK);
        }
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ModelDragonEgg model = new ModelDragonEgg();
        TileEntityPodium podium = entity;

        if (!podium.getStackInSlot(0).isEmpty()) {
            if (podium.getStackInSlot(0).getItem() instanceof ItemDragonEgg) {
                ItemDragonEgg item = (ItemDragonEgg) podium.getStackInSlot(0).getItem();
                matrixStackIn.push();
                matrixStackIn.translate(0.5F, 0.475F, 0.5F);
                matrixStackIn.push();
                matrixStackIn.push();
                model.renderPodium();
                model.render(matrixStackIn, bufferIn.getBuffer(RenderPodium.getEggTexture(item.type)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.pop();
                matrixStackIn.pop();
                matrixStackIn.pop();
            } else if (podium.getStackInSlot(0).getItem() instanceof ItemMyrmexEgg) {
                boolean jungle = podium.getStackInSlot(0).getItem() == IafItemRegistry.MYRMEX_JUNGLE_EGG;
                matrixStackIn.push();
                matrixStackIn.translate(0.5F, 0.475F, 0.5F);
                matrixStackIn.push();
                matrixStackIn.push();
                model.renderPodium();
                model.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutout(jungle ? RenderMyrmexEgg.EGG_JUNGLE : RenderMyrmexEgg.EGG_DESERT)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.pop();
                matrixStackIn.pop();
                matrixStackIn.pop();
            } else if (!podium.getStackInSlot(0).isEmpty()) {
                //if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new RenderPodiumItemEvent(this, podium, f, x, y, z))) {
                matrixStackIn.push();
                float f2 = ((float) podium.prevTicksExisted + (podium.ticksExisted - podium.prevTicksExisted) * partialTicks);
                float f3 = MathHelper.sin(f2 / 10.0F) * 0.1F + 0.1F;
                matrixStackIn.translate(0.5F, 1.55F + f3, 0.5F);
                float f4 = (f2 / 20.0F) * (180F / (float) Math.PI);
                matrixStackIn.rotate(new Quaternion(Vector3f.YP, f4, true));
                matrixStackIn.push();
                matrixStackIn.translate(0, 0.2F, 0);
                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                Minecraft.getInstance().getItemRenderer().renderItem(podium.getStackInSlot(0), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.pop();
                matrixStackIn.pop();
                //}
            }
        }

    }
}
