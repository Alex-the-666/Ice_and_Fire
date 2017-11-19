package com.github.alexthe666.iceandfire.client;

import com.github.alexthe666.iceandfire.client.gui.GuiDragon;
import com.github.alexthe666.iceandfire.client.gui.GuiHippogryph;
import com.github.alexthe666.iceandfire.client.gui.GuiLectern;
import com.github.alexthe666.iceandfire.client.gui.GuiPodium;
import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
import com.github.alexthe666.iceandfire.inventory.ContainerHippogryph;
import com.github.alexthe666.iceandfire.inventory.ContainerLectern;
import com.github.alexthe666.iceandfire.inventory.ContainerPodium;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(x);
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		switch (id) {

			case 0:
				if (entity != null) {
					if (entity instanceof EntityDragonBase) {
						return new ContainerDragon((EntityDragonBase) entity, player);
					}
				}
				break;

			case 1:
				if (tile != null) {

					if (tile instanceof TileEntityPodium) {
						return new ContainerPodium(player.inventory, (TileEntityPodium) tile, player);
					}
				}
				break;

			case 2:
				if (tile != null) {

					if (tile instanceof TileEntityLectern) {
						return new ContainerLectern(player.inventory, (TileEntityLectern) tile);
					}
				}
				break;

			case 4:
				if (entity != null) {
					if (entity instanceof EntityHippogryph) {
						return new ContainerHippogryph((EntityHippogryph) entity, player);
					}
				}
				break;
		}
		return null;

	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Entity entity = world.getEntityByID(x);
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		switch (id) {

			case 0:
				if (entity != null) {

					if (entity instanceof EntityDragonBase) {
						return new GuiDragon(player.inventory, (EntityDragonBase) entity);
					}
				}
				break;

			case 1:
				if (tile != null) {

					if (tile instanceof TileEntityPodium) {
						return new GuiPodium(player.inventory, (TileEntityPodium) tile);
					}
				}
				break;

			case 2:
				if (tile != null) {

					if (tile instanceof TileEntityLectern) {
						return new GuiLectern(player.inventory, (TileEntityLectern) tile);
					}
				}
				break;
			case 3:
				return new GuiBestiary(player.getActiveItemStack());

			case 4:
				if (entity != null) {

					if (entity instanceof EntityHippogryph) {
						return new GuiHippogryph(player.inventory, (EntityHippogryph) entity);
					}
				}
				break;
		}
		return entity;
	}
}