package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * DragonFireEvent is fired right before a Dragon breathes fire or ice. <br>
 * {@link #dragonBase} dragon in question. <br>
 * {@link #targetX} x coordinate being targeted for burning/freezing. <br>
 * {@link #targetY} y coordinate being targeted for burning/freezing. <br>
 * {@link #targetZ} z coordinate being targeted for burning/freezing. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, no fire will be spawned from the dragon's mouth.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * <br>
 * If you only want to deal with the damage caused by dragon fire, see {@link DragonFireDamageWorldEvent} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class DragonFireEvent extends LivingEvent {
    private EntityDragonBase dragonBase;
    private double targetX;
    private double targetY;
    private double targetZ;

    public DragonFireEvent(EntityDragonBase dragonBase, double targetX, double targetY, double targetZ) {
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
