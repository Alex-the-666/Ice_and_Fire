package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.item.ICustomRendered;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockMyrmexResin extends Block implements ICustomRendered {

    private boolean sticky;

    public BlockMyrmexResin(boolean sticky, String suffix) {
        super(
    		Properties
    			.create(Material.CLAY)
    			.hardnessAndResistance(2.5F)
    			.sound(sticky ? SoundType.SLIME : SoundType.GROUND)
		);

        this.setRegistryName(IceAndFire.MODID, sticky ? "myrmex_resin_sticky_" + suffix : "myrmex_resin_" + suffix);
        this.sticky = sticky;
    }

    @Deprecated
    public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if (sticky) {
            if (!(entity instanceof EntityMyrmexBase)) {
                entity.setMotion(entity.getMotion().mul(0.4D, 0.4D, 0.4D));
            }

        }
    }
}
