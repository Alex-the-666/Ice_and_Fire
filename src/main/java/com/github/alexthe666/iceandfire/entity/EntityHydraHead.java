package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntityHydraHead extends EntityMutlipartPart {
    public int headIndex;
    public EntityHydra hydra;
    private boolean neck;

    public EntityHydraHead(EntityType t, World world) {
        super(t, world);
    }

    public EntityHydraHead(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.HYDRA_MULTIPART, worldIn);
    }

    public EntityHydraHead(EntityHydra entity, float radius, float angle, float y, float width, float height, float damageMulti, int headIndex, boolean neck) {
        super(IafEntityRegistry.HYDRA_MULTIPART, entity, radius, angle, y, width, height, damageMulti);
        this.headIndex = headIndex;
        this.neck = neck;
        this.hydra = entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (hydra != null && hydra.getSeveredHead() != -1 && this.neck && !EntityGorgon.isStoneMob(hydra)) {
            if (hydra.getSeveredHead() == headIndex) {
                if (this.world.isRemote) {
                    for (int k = 0; k < 5; ++k) {
                        double d2 = 0.4;
                        double d0 = 0.1;
                        double d1 = 0.1;
                        IceAndFire.PROXY.spawnParticle("blood", this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() - 0.5D, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d2, d0, d1);
                    }
                }
            }
        }
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity parent = this.getParent();
        if (parent instanceof EntityHydra) {
            ((EntityHydra) parent).onHitHead(damage, headIndex);
            return parent.attackEntityFrom(source, damage);
        } else {
            return parent != null && parent.attackEntityFrom(source, damage);
        }
    }


}
