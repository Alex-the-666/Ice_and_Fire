package com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs;

public interface ICustomSizeNavigator {

    boolean isSmallerThanBlock();
    float getXZNavSize();
    int getYNavSize();
}
