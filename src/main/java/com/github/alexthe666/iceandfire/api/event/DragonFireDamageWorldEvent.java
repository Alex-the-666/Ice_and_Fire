package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * DragonFireDamageWorldEvent is fired right before a Dragon damages/changes terrain fire, lightning or ice. <br>
 * {@link #dragonBase} dragon in question. <br>
 * {@link #targetX} x coordinate being targeted for burning/freezing. <br>
 * {@link #targetY} y coordinate being targeted for burning/freezing. <br>
 * {@link #targetZ} z coordinate being targeted for burning/freezing. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, no blocks will be modified by the dragons breath.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * <br>
 * If you want to cancel all aspects of dragon fire, see {@link DragonFireEvent} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class DragonFireDamageWorldEvent extends LivingEvent {
    private EntityDragonBase dragonBase;
    private double targetX;
    private double targetY;
    private double targetZ;

    public DragonFireDamageWorldEvent(EntityDragonBase dragonBase, double targetX, double targetY, double targetZ) {
        super(dragonBase);
        this.dragonBase = dragonBase;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public EntityDragonBase getDragon() {
        return dragonBase;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getTargetZ() {
        return targetZ;
    }

}
