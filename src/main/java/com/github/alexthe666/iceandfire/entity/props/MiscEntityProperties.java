package com.github.alexthe666.iceandfire.entity.props;

import java.util.ArrayList;
import java.util.List;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;

public class MiscEntityProperties extends EntityProperties<LivingEntity> {

    public boolean hasDismountedDragon;
    public int deathwormLungeTicks = 0;
    public int prevDeathwormLungeTicks = 0;
    public int specialWeaponDmg = 0;
    public boolean deathwormLaunched = false;
    public boolean deathwormReceded = false;
    public boolean isBeingGlaredAt = false;
    public int lastEnteredDreadPortalX = 0;
    public int lastEnteredDreadPortalY = 0;
    public int lastEnteredDreadPortalZ = 0;
    public List<Entity> glarers = new ArrayList<>();
    public List<Entity> entitiesWeAreGlaringAt = new ArrayList<>();
    public int inLoveTicks;

    @Override
    public int getTrackingTime() {
        return 20;
    }

    @Override
    public void saveNBTData(CompoundNBT compound) {
        compound.putBoolean("DismountedDragon", hasDismountedDragon);
        compound.putInt("GauntletDamage", specialWeaponDmg);
        compound.putInt("DreadPortalX", lastEnteredDreadPortalX);
        compound.putInt("DreadPortalY", lastEnteredDreadPortalY);
        compound.putInt("DreadPortalZ", lastEnteredDreadPortalZ);
    }

    @Override
    public void loadNBTData(CompoundNBT compound) {
        this.hasDismountedDragon = compound.getBoolean("DismountedDragon");
        this.specialWeaponDmg = compound.getInt("GauntletDamage");
        this.lastEnteredDreadPortalX = compound.getInt("DreadPortalX");
        this.lastEnteredDreadPortalY = compound.getInt("DreadPortalY");
        this.lastEnteredDreadPortalZ = compound.getInt("DreadPortalZ");
    }

    @Override
    public void init() {
        hasDismountedDragon = false;
        lastEnteredDreadPortalX = 0;
        lastEnteredDreadPortalY = 0;
        lastEnteredDreadPortalZ = 0;
    }

    @Override
    public String getID() {
        return "Ice and Fire - Player Property Tracker";
    }

    @Override
    public Class<LivingEntity> getEntityClass() {
        return LivingEntity.class;
    }
}
