package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;

public class PixieAISteal extends Goal {
    private final EntityPixie temptedEntity;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double pitch;
    private double yaw;
    private PlayerEntity temptingPlayer;
    private int delayTemptCounter = 0;
    private boolean isRunning;

    public PixieAISteal(EntityPixie temptedEntityIn, double speedIn) {
        this.temptedEntity = temptedEntityIn;
        this.speed = speedIn;
    }

    public boolean shouldExecute() {
        if (!IafConfig.pixiesStealItems || !temptedEntity.getHeldItemMainhand().isEmpty() || temptedEntity.stealCooldown > 0) {
            return false;
        }
        if (temptedEntity.getRNG().nextInt(200) == 0) {
            return false;
        }
        if (temptedEntity.isTamed()) {
            return false;
        }
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.temptingPlayer = this.temptedEntity.world.getClosestPlayer(this.temptedEntity, 10.0D);
            return this.temptingPlayer != null && (this.temptedEntity.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.temptingPlayer.inventory.isEmpty() && !this.temptingPlayer.isCreative());
        }
    }

    public boolean shouldContinueExecuting() {
        return !temptedEntity.isTamed() && temptedEntity.getHeldItemMainhand().isEmpty() && this.delayTemptCounter == 0 && temptedEntity.stealCooldown == 0;
    }

    public void startExecuting() {
        this.targetX = this.temptingPlayer.getPosX();
        this.targetY = this.temptingPlayer.getPosY();
        this.targetZ = this.temptingPlayer.getPosZ();
        this.isRunning = true;
    }

    public void resetTask() {
        this.temptingPlayer = null;
        if (this.delayTemptCounter < 10)
            this.delayTemptCounter += 10;
        this.isRunning = false;
    }

    public void tick() {
        this.temptedEntity.getLookController().setLookPositionWithEntity(this.temptingPlayer, (float) (this.temptedEntity.getHorizontalFaceSpeed() + 20), (float) this.temptedEntity.getVerticalFaceSpeed());
        ArrayList<Integer> slotlist = new ArrayList<Integer>();
        if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 3D && !this.temptingPlayer.inventory.isEmpty()) {

            for (int i = 0; i < this.temptingPlayer.inventory.getSizeInventory(); i++) {
                ItemStack targetStack = this.temptingPlayer.inventory.getStackInSlot(i);
                if (!PlayerInventory.isHotbar(i) && !targetStack.isEmpty() && targetStack.isStackable()) {
                    slotlist.add(i);
                }
            }
            if(slotlist.size() >= 1) {
                int slot;
                if(slotlist.size() == 1) {
                    slot = slotlist.get(0);
                } else {
                     slot = slotlist.get(new Random().nextInt(slotlist.size()));
                }
                ItemStack randomItem = this.temptingPlayer.inventory.getStackInSlot(slot);
                this.temptedEntity.setHeldItem(Hand.MAIN_HAND, randomItem);
                this.temptingPlayer.inventory.removeStackFromSlot(slot);
                this.temptedEntity.flipAI(true);
                this.temptedEntity.playSound(IafSoundRegistry.PIXIE_TAUNT, 1F, 1F);

                for (EntityPixie pixie : this.temptingPlayer.world.getEntitiesWithinAABB(EntityPixie.class, temptedEntity.getBoundingBox().grow(40))) {
                    pixie.stealCooldown = 1000 + pixie.getRNG().nextInt(3000);
                }
                if (temptingPlayer != null) {
                    this.temptingPlayer.addPotionEffect(new EffectInstance(this.temptedEntity.negativePotions[this.temptedEntity.getColor()], 100));
                }
            }
            //If the pixie couldn't steal anything
            else{
                this.temptedEntity.flipAI(true);
                this.delayTemptCounter = 10 *20;
            }
        } else {
            this.temptedEntity.getMoveHelper().setMoveTo(this.temptingPlayer.getPosX(), this.temptingPlayer.getPosY() + 1.5F, this.temptingPlayer.getPosZ(), 1D);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}