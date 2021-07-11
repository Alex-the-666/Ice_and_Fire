package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadRuinProcessor;
import com.github.alexthe666.iceandfire.world.gen.processor.GorgonTempleProcessor;
import com.github.alexthe666.iceandfire.world.gen.processor.GraveyardProcessor;
import com.github.alexthe666.iceandfire.world.gen.processor.VillageHouseProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;

public class IafProcessors {
    public static IStructureProcessorType<DreadRuinProcessor> DREADRUINPROCESSOR = () -> DreadRuinProcessor.CODEC;
    public static IStructureProcessorType<GorgonTempleProcessor> GORGONTEMPLEPROCESSOR = () -> GorgonTempleProcessor.CODEC;
    public static IStructureProcessorType<GraveyardProcessor> GRAVEYARDPROCESSOR = () -> GraveyardProcessor.CODEC;
    public static IStructureProcessorType<VillageHouseProcessor> VILLAGEHOUSEPROCESSOR = () -> VillageHouseProcessor.CODEC;
    public static void registerProcessors() {
        Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(IceAndFire.MODID, "dread_mausoleum_processor"), DREADRUINPROCESSOR);
        Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(IceAndFire.MODID, "gorgon_temple_processor"), GORGONTEMPLEPROCESSOR);
        Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(IceAndFire.MODID, "graveyard_processor"), GRAVEYARDPROCESSOR);
        Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(IceAndFire.MODID, "village_house_processor"), VILLAGEHOUSEPROCESSOR);
    }
}
