package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Region;

public class NodeProcessorWalk extends WalkNodeProcessor {
    public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
        super.func_225578_a_(p_225578_1_, p_225578_2_);
    }

    public void setEntitySize(float width, float height){
        this.entitySizeX = MathHelper.floor(width + 1.0F);
        this.entitySizeY = MathHelper.floor(height + 1.0F);
        this.entitySizeZ = MathHelper.floor(width + 1.0F);
    }
}
