package com.github.alexthe666.iceandfire.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IDragonFlute;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemDragonFlute extends Item {

    public ItemDragonFlute() {
        super(new Item.Properties().maxStackSize(1).group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "dragon_flute");
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStackIn = player.getHeldItem(hand);
        player.getCooldownTracker().setCooldown(this, 60);

        float chunksize = 16 * IafConfig.dragonFluteDistance;
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(player, (new AxisAlignedBB(player.getPosX(), player.getPosY(), player.getPosZ(), player.getPosX() + 1.0D, player.getPosY() + 1.0D, player.getPosZ() + 1.0D)).grow(chunksize, 256, chunksize));
        Collections.sort(list, new Sorter(player));
        List<IDragonFlute> dragons = new ArrayList<IDragonFlute>();
        Iterator<Entity> itr_entities = list.iterator();
        while (itr_entities.hasNext()) {
            Entity entity = itr_entities.next();
            if (entity instanceof IDragonFlute) {
                dragons.add((IDragonFlute) entity);
            }
        }

        Iterator<IDragonFlute> itr_dragons = dragons.iterator();
        while (itr_dragons.hasNext()) {
            IDragonFlute dragon = itr_dragons.next();
            dragon.onHearFlute(player);
			/*
			if(dragon.isTamed() && dragon.isOwner(player)) {
                if (dragon.isFlying() || dragon.isHovering()) {
                    dragon.setFlying(false);
                    dragon.setHovering(false);
                }
            }*/
        }
        worldIn.playSound(player, player.getPosition(), IafSoundRegistry.DRAGONFLUTE, SoundCategory.NEUTRAL, 1, 1.75F);

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStackIn);
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}