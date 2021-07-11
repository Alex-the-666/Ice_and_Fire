package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
public interface IStuckHandlerEntity
{
    /**
     * Check whether the entity could currently be stuck, return false to prevent the stuck handler from taking action
     *
     * @return true if so.
     */
    default public boolean canBeStuck()
    {
        return true;
    }
}