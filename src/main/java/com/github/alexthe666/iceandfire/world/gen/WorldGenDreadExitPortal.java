package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenDreadExitPortal {
    private static final ResourceLocation STRUCTURE = new ResourceLocation(IceAndFire.MODID, "dread_exit_portal");

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        /*
        MinecraftServer server = worldIn.getMinecraftServer();
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
        PlacementSettings settings = new PlacementSettings().setRotation(Rotation.NONE);
        Template template = templateManager.getTemplate(server, STRUCTURE);
        Biome biome = worldIn.getBiome(position);
        template.addBlocksToWorld(worldIn, position, new DreadPortalProcessor(position, settings, biome), settings, 2);

         */
        return true;
    }
}