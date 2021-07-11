package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDeathwormGauntlet;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceAndFireTEISR extends ItemStackTileEntityRenderer {

    private RenderTrollWeapon renderTrollWeapon = new RenderTrollWeapon();
    private RenderDeathWormGauntlet renderDeathWormGauntlet = new RenderDeathWormGauntlet();
    private RenderDreadPortal renderDreadPortal = new RenderDreadPortal(TileEntityRendererDispatcher.instance);
    private RenderGorgonHead renderGorgonHead = new RenderGorgonHead(true);
    private RenderGorgonHead renderGorgonHeadDead = new RenderGorgonHead(false);
    private RenderPixieHouse renderPixieHouse = new RenderPixieHouse(TileEntityRendererDispatcher.instance);
    private TileEntityDreadPortal dreadPortalDummy = new TileEntityDreadPortal();
    private RenderGhostChest renderGhostChest = new RenderGhostChest(TileEntityRendererDispatcher.instance);
    private TileEntityGhostChest ghostChestDummy = new TileEntityGhostChest();

    @Override
    public void func_239207_a_(ItemStack itemStackIn, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (itemStackIn.getItem() == IafItemRegistry.GORGON_HEAD) {
            if (itemStackIn.getTag() != null) {
                if (itemStackIn.getTag().getBoolean("Active")) {
                    renderGorgonHead.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
                } else {
                    renderGorgonHeadDead.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
                }
            }
        }
        if (itemStackIn.getItem() == IafBlockRegistry.GHOST_CHEST.asItem()) {
            renderGhostChest.render(ghostChestDummy, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }

        if (itemStackIn.getItem() instanceof ItemTrollWeapon) {
            ItemTrollWeapon weaponItem = (ItemTrollWeapon) itemStackIn.getItem();
            renderTrollWeapon.renderItem(weaponItem.weapon, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof ItemDeathwormGauntlet) {
            renderDeathWormGauntlet.renderItem(itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof BlockItem && ((BlockItem) itemStackIn.getItem()).getBlock() == IafBlockRegistry.DREAD_PORTAL) {
            renderDreadPortal.render(dreadPortalDummy, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof BlockItem && ((BlockItem) itemStackIn.getItem()).getBlock() instanceof BlockPixieHouse) {
            renderPixieHouse.metaOverride = (BlockItem) itemStackIn.getItem();
            renderPixieHouse.render(null, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }
}
