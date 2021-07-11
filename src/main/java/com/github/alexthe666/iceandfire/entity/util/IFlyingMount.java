package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.entity.player.PlayerEntity;

public interface IFlyingMount {

    PlayerEntity getRidingPlayer();

    double getFlightSpeedModifier();

    default boolean fliesLikeElytra() {
        return false;
    }

    boolean isFlying();

    default boolean isGoingUp() {
        return false;
    }

    default boolean isGoingDown() {
        return false;
    }

    default boolean isHovering() {
        return false;
    }

    default double getYSpeedMod(){ return 10; }
}
