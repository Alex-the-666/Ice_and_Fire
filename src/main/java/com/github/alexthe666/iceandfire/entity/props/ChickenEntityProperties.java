package com.github.alexthe666.iceandfire.entity.props;

import java.util.Random;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityProperties;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;

public class ChickenEntityProperties extends EntityProperties<AnimalEntity> {

    private static Random rand = new Random();
    public int timeUntilNextEgg = 1;

    @Override
    public int getTrackingTime() {
        return 20;
    }

    @Override
    public void saveNBTData(CompoundNBT compound) {
        compound.putInt("TimeUntilNextEgg", timeUntilNextEgg);
    }

    @Override
    public void loadNBTData(CompoundNBT compound) {
        this.timeUntilNextEgg = compound.getInt("TimeUntilNextEgg");
    }

    @Override
    public void init() {
        timeUntilNextEgg = generateTime();
    }

    @Override
    public String getID() {
        return "Ice And Fire - Chicken Property Tracker";
    }

    @Override
    public Class<AnimalEntity> getEntityClass() {
        return AnimalEntity.class;
    }

    public int generateTime() {
        return rand.nextInt(6000) + 6000;
    }
}
