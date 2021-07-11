package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import net.minecraft.world.gen.feature.structure.Structure.IStartFactory;

public class GorgonTempleStructure extends Structure<NoFeatureConfig> {

    public GorgonTempleStructure(Codec<NoFeatureConfig> p_i51440_1_) {
        super(p_i51440_1_);
        this.setRegistryName("iceandfire:gorgon_temple");
    }

    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public String getStructureName() {
        return IceAndFire.MODID + ":gorgon_temple";
    }

    public IStartFactory getStartFactory() {
        return GorgonTempleStructure.Start::new;
    }

   /*
    public int getSize() {
        return 4;
    }

    protected int getSeedModifier() {
        return 123456789;
    }

    protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
        return 8;// Math.max(IafConfig.spawnGorgonsChance, 2);
    }

    protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
        return 4; //Math.max(IafConfig.spawnGorgonsChance / 2, 1);
    }*/

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structure, int x, int z, MutableBoundingBox boundingBox, int refCount, long seed) {
            super(structure, x, z, boundingBox, refCount, seed);
        }

        @Override
        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, int x, int z, Biome biome, NoFeatureConfig config) {
            if(IafConfig.spawnGorgons) {
                Rotation rotation = Rotation.randomRotation(this.rand);
                int i = 5;
                int j = 5;
                if (rotation == Rotation.CLOCKWISE_90) {
                   i = -5;
                } else if (rotation == Rotation.CLOCKWISE_180) {
                   i = -5;
                   j = -5;
                } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                   j = -5;
                }

                int k = (x << 4) + 7;
                int l = (z << 4) + 7;
                int i1 = chunkGenerator.getNoiseHeightMinusOne(k, l, Heightmap.Type.WORLD_SURFACE_WG);
                int j1 = chunkGenerator.getNoiseHeightMinusOne(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
                int k1 = chunkGenerator.getNoiseHeightMinusOne(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
                int l1 = chunkGenerator.getNoiseHeightMinusOne(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
                int i2 = Math.min(Math.min(i1, j1), Math.min(k1, l1));
                BlockPos blockpos = new BlockPos(x * 16 + 8, i2 + 2, z * 16 + 8);

                // All a structure has to do is call this method to turn it into a jigsaw based structure!
                // No manual pieces class needed.
                JigsawManager.func_242837_a(
                        dynamicRegistries,
                        new VillageConfig(() -> dynamicRegistries.getRegistry(Registry.JIGSAW_POOL_KEY)
                                .getOrDefault(new ResourceLocation(IceAndFire.MODID, "gorgon_temple/top_pool")),
                                3), // Depth of jigsaw branches. Gorgon temple has a depth of 3. (start top -> bottom -> gorgon)
                        AbstractVillagePiece::new,
                        chunkGenerator,
                        templateManager,
                        blockpos,
                        this.components,
                        this.rand,
                        false,
                        false);
                
                this.recalculateStructureSize();
            }
        }
    }
}
