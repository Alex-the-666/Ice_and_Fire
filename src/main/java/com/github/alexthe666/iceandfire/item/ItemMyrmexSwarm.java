package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSwarmer;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemMyrmexSwarm extends Item {
    private boolean jungle;

    public ItemMyrmexSwarm(boolean jungle) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        if (jungle) {
            this.setRegistryName(IceAndFire.MODID, "myrmex_jungle_swarm");
        } else {
            this.setRegistryName(IceAndFire.MODID, "myrmex_desert_swarm");
        }
        this.jungle = jungle;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        playerIn.setActiveHand(hand);
        playerIn.swingArm(hand);
        if (!playerIn.isCreative()) {
            itemStackIn.shrink(1);
            playerIn.getCooldownTracker().setCooldown(this, 20);
        }
        for (int i = 0; i < 5; i++) {
            EntityMyrmexSwarmer myrmex = new EntityMyrmexSwarmer(IafEntityRegistry.MYRMEX_SWARMER, worldIn);
            myrmex.setPosition(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());
            myrmex.setJungleVariant(jungle);
            myrmex.setSummonedBy(playerIn);
            myrmex.setFlying(true);
            if (!worldIn.isRemote) {
                worldIn.addEntity(myrmex);
            }
        }
        playerIn.getCooldownTracker().setCooldown(this, 1800);
        return new ActionResult<ItemStack>(ActionResultType.PASS, itemStackIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.myrmex_swarm.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.myrmex_swarm.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}