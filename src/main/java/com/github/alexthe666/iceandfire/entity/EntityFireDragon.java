package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.google.common.base.Predicate;
import fossilsarcheology.api.EnumDiet;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityFireDragon extends EntityDragonBase {

	public static Animation ANIMATION_TAILWHACK;
	public static Animation ANIMATION_FIRECHARGE;
	public static float[] growth_stage_1 = new float[]{1F, 3F};
	public static float[] growth_stage_2 = new float[]{3F, 7F};
	public static float[] growth_stage_3 = new float[]{7F, 12.5F};
	public static float[] growth_stage_4 = new float[]{12.5F, 20F};
	public static float[] growth_stage_5 = new float[]{20F, 30F};
	public int walkCycle;

	public EntityFireDragon(World worldIn) {
		super(worldIn, EnumDiet.CARNIVORE, 1, 1 + IceAndFire.CONFIG.dragonAttackDamage, IceAndFire.CONFIG.dragonHealth * 0.04, IceAndFire.CONFIG.dragonHealth, 0.2F, 0.5F);
		this.setSize(0.78F, 1.2F);
		this.isImmuneToFire = true;
		this.ignoreFrustumCheck = true;
		ANIMATION_SPEAK = Animation.create(45);
		ANIMATION_BITE = Animation.create(35);
		ANIMATION_SHAKEPREY = Animation.create(65);
		ANIMATION_TAILWHACK = Animation.create(40);
		ANIMATION_FIRECHARGE = Animation.create(40);
		this.growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit = new EntityAISit(this));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.5D, true));
		this.tasks.addTask(4, new DragonAIMate(this, 1.0D));
		this.tasks.addTask(5, new EntityAITempt(this, 1.0D, ModItems.fire_stew, false));
		this.tasks.addTask(6, new DragonAIAirTarget(this));
		this.tasks.addTask(7, new DragonAIWander(this, 1.0D));
		this.tasks.addTask(8, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
		this.tasks.addTask(8, new DragonAILookIdle(this));
		this.tasks.addTask(9, new DragonAIBreakBlocks(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(4, new DragonAITarget(this, EntityLivingBase.class, false, new Predicate<Entity>() {
			@Override
			public boolean apply(@Nullable Entity entity) {
				return entity instanceof EntityLivingBase;
			}
		}));
		this.targetTasks.addTask(5, new DragonAITargetItems(this, false));
	}

	@Override
	public String getTexture() {
		if (this.isModelDead()) {
			if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
				return "iceandfire:textures/models/firedragon/fire_skeleton_" + this.getDragonStage();
			} else {
				return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_sleeping";
			}
		}
		if (this.isSleeping() || this.isBlinking()) {
			return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_sleeping";
		} else {
			return "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "";
		}
	}

	public String getVariantName(int variant) {
		switch (variant) {
			default:
				return "red_";
			case 1:
				return "green_";
			case 2:
				return "bronze_";
			case 3:
				return "gray_";
		}
	}

	public Item getVariantScale(int variant) {
		switch (variant) {
			default:
				return ModItems.dragonscales_red;
			case 1:
				return ModItems.dragonscales_green;
			case 2:
				return ModItems.dragonscales_bronze;
			case 3:
				return ModItems.dragonscales_gray;
		}
	}

	public Item getVariantEgg(int variant) {
		switch (variant) {
			default:
				return ModItems.dragonegg_red;
			case 1:
				return ModItems.dragonegg_green;
			case 2:
				return ModItems.dragonegg_bronze;
			case 3:
				return ModItems.dragonegg_gray;
		}
	}

	public boolean canBeSteered() {
		return true;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		switch (this.getRNG().nextInt(3)) {
			case 0:
				if (this.getAnimation() != this.ANIMATION_BITE) {
					this.setAnimation(this.ANIMATION_BITE);
					return false;
				} else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
					boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
					this.attackDecision = false;
					return flag;
				}
				break;
			case 1:
				if (entityIn.width < this.width * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1) {
					if (this.getAnimation() != this.ANIMATION_SHAKEPREY) {
						this.setAnimation(this.ANIMATION_SHAKEPREY);
						entityIn.startRiding(this);
						return false;
					}
				} else {
					if (this.getAnimation() != this.ANIMATION_BITE) {
						this.setAnimation(this.ANIMATION_BITE);
						return false;
					} else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
						boolean flag1 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
						this.attackDecision = false;
						return flag1;
					}
				}
				break;
			case 2:
				if (this.getAnimation() != this.ANIMATION_TAILWHACK) {
					this.setAnimation(this.ANIMATION_TAILWHACK);
					return false;
				} else if (this.getAnimationTick() > 20 && this.getAnimationTick() < 25) {
					boolean flag2 = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
					if (entityIn instanceof EntityLivingBase) {
						((EntityLivingBase) entityIn).knockBack(entityIn, 1, 1, 1);
					}
					this.attackDecision = false;
					return flag2;
				}
				break;
		}

		return false;
	}

	public void moveEntityTowards(Entity entity, double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		entity.motionX = x;
		entity.motionY = y;
		entity.motionZ = z;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.walkCycle < 39) {
			this.walkCycle++;
		} else {
			this.walkCycle = 0;
		}
		if (this.getAttackTarget() != null && !this.isSleeping()) {
			if ((!attackDecision || this.isFlying())) {
				shootFireAtMob(this.getAttackTarget());
			} else {
				if (this.getEntityBoundingBox().expand(this.getRenderSize() / 3, this.getRenderSize() / 3, this.getRenderSize() / 3).intersects(this.getAttackTarget().getEntityBoundingBox())) {
					attackEntityAsMob(this.getAttackTarget());
				}

			}
		} else {
			this.setBreathingFire(false);
		}
	}

	public void riderShootFire(Entity controller) {
		if (this.getRNG().nextInt(5) == 0 && !this.isChild()) {
			if (this.getAnimation() != this.ANIMATION_FIRECHARGE) {
				this.setAnimation(this.ANIMATION_FIRECHARGE);
			} else if (this.getAnimationTick() == 15) {
				rotationYaw = renderYawOffset;
				float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
				float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
				float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
				this.playSound(ModSounds.FIREDRAGON_BREATH, 4, 1);
				double d2 = controller.getLookVec().x;
				double d3 = controller.getLookVec().y;
				double d4 = controller.getLookVec().z;
				EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(world, this, d2, d3, d4);
				float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
				entitylargefireball.setSizes(size, size);
				entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
				if (!world.isRemote) {
					world.spawnEntity(entitylargefireball);
				}

			}
		} else {
			if (this.isBreathingFire()) {
				if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
					rotationYaw = renderYawOffset;
					float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
					float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
					float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
					double d2 = controller.getLookVec().x;
					double d3 = controller.getLookVec().y;
					double d4 = controller.getLookVec().z;
					EntityDragonFire entitylargefireball = new EntityDragonFire(world, this, d2, d3, d4);
					this.playSound(ModSounds.FIREDRAGON_BREATH, 4, 1);
					float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
					entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
					if (!world.isRemote) {
						world.spawnEntity(entitylargefireball);
					}
				}
			} else {
				this.setBreathingFire(true);
			}
		}
	}

	private void shootFireAtMob(EntityLivingBase entity) {
		if (!this.attackDecision) {
			if (this.getRNG().nextInt(5) == 0) {
				if (this.getAnimation() != this.ANIMATION_FIRECHARGE) {
					this.setAnimation(this.ANIMATION_FIRECHARGE);
				} else if (this.getAnimationTick() == 15) {
					rotationYaw = renderYawOffset;
					float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
					float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
					float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
					double d2 = entity.posX - headPosX;
					double d3 = entity.posY - headPosY;
					double d4 = entity.posZ - headPosZ;
					this.playSound(ModSounds.FIREDRAGON_BREATH, 4, 1);
					EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(world, this, d2, d3, d4);
					float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
					entitylargefireball.setSizes(size, size);
					entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
					if (!world.isRemote) {
						world.spawnEntity(entitylargefireball);
					}
					if (entity.isDead || entity == null) {
						this.setBreathingFire(false);
						this.attackDecision = true;
					}
				}
			} else {
				if (this.isBreathingFire()) {
					if (this.isActuallyBreathingFire() && this.ticksExisted % 3 == 0) {
						rotationYaw = renderYawOffset;
						float headPosX = (float) (posX + 1.8F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
						float headPosZ = (float) (posZ + 1.8F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
						float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
						double d2 = entity.posX - headPosX;
						double d3 = entity.posY - headPosY;
						double d4 = entity.posZ - headPosZ;
						this.playSound(ModSounds.FIREDRAGON_BREATH, 4, 1);
						EntityDragonFire entitylargefireball = new EntityDragonFire(world, this, d2, d3, d4);
						float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
						entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
						if (!world.isRemote && !entity.isDead) {
							world.spawnEntity(entitylargefireball);
						}
						entitylargefireball.setSizes(size, size);
						if (entity.isDead || entity == null) {
							this.setBreathingFire(false);
							this.attackDecision = true;
						}
					}
				} else {
					this.setBreathingFire(true);
				}
			}
		}
		this.faceEntity(entity, 360, 360);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_IDLE : this.isAdult() ? ModSounds.FIREDRAGON_ADULT_IDLE : ModSounds.FIREDRAGON_CHILD_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_HURT : this.isAdult() ? ModSounds.FIREDRAGON_ADULT_HURT : ModSounds.FIREDRAGON_CHILD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isTeen() ? ModSounds.FIREDRAGON_TEEN_DEATH : this.isAdult() ? ModSounds.FIREDRAGON_ADULT_DEATH : ModSounds.FIREDRAGON_CHILD_DEATH;
	}

	@Override
	public Animation[] getAnimations() {
		return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT, EntityDragonBase.ANIMATION_SPEAK, EntityDragonBase.ANIMATION_BITE, EntityDragonBase.ANIMATION_SHAKEPREY, EntityFireDragon.ANIMATION_TAILWHACK, EntityFireDragon.ANIMATION_FIRECHARGE};
	}

	@Override
	public String getTextureOverlay() {
		return this.isSleeping() || this.isBlinking() || this.isModelDead() ? null : "iceandfire:textures/models/firedragon/" + this.getVariantName(this.getVariant()) + this.getDragonStage() + "_eyes";
	}

	public boolean isBreedingItem(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.fire_stew;
	}


}
