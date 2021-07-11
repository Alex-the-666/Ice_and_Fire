package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public enum EnumSeaSerpentAnimations {
    T_POSE(""),
    SWIM1("Swim1"),
    SWIM2("Swim2"),
    SWIM3("Swim3"),
    SWIM4("Swim4"),
    SWIM5("Swim5"),
    SWIM6("Swim6"),
    BITE1("Bite1"),
    BITE2("Bite2"),
    BITE3("Bite3"),
    ROAR1("Roar1"),
    ROAR2("Roar2"),
    ROAR3("Roar3"),
    DEAD("Dead"),
    JUMPING1("Jumping1"),
    JUMPING2("Jumping2");

    @OnlyIn(Dist.CLIENT)
    public TabulaModel seaserpent_model;
    private String fileSuffix;

    EnumSeaSerpentAnimations(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    @OnlyIn(Dist.CLIENT)
    public static void initializeSerpentModels() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            for (EnumSeaSerpentAnimations animation : values()) {
                try {
                    animation.seaserpent_model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/seaserpent/seaserpent" + animation.fileSuffix));
                } catch (Exception e) {
                    IceAndFire.LOGGER.warn("sea serpent model at: seaserpent" + animation.fileSuffix + ".tbl doesn't exist!");
                    e.printStackTrace();
                }
            }
        }

    }
}
