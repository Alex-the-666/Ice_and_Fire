package com.github.alexthe666.iceandfire.event;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.ai.AiDebug;
import com.github.alexthe666.iceandfire.entity.ai.EntitySheepAIFollowCyclops;
import com.github.alexthe666.iceandfire.entity.ai.VillagerAIFearUntamed;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.ChickenEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.SirenEntityProperties;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IHearsSiren;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemBlindfold;
import com.github.alexthe666.iceandfire.item.ItemChain;
import com.github.alexthe666.iceandfire.item.ItemCockatriceScepter;
import com.github.alexthe666.iceandfire.item.ItemDeathwormGauntlet;
import com.github.alexthe666.iceandfire.item.ItemScaleArmor;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import com.github.alexthe666.iceandfire.message.MessagePlayerHitMultipart;
import com.github.alexthe666.iceandfire.message.MessageSwingArm;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import com.github.alexthe666.iceandfire.misc.IafTagRegistry;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.Pathfinding;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import com.github.alexthe666.iceandfire.world.gen.WorldGenFireDragonCave;
import com.github.alexthe666.iceandfire.world.gen.WorldGenIceDragonCave;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public class ServerEvents {

    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    private static final Predicate VILLAGER_FEAR = new Predicate<LivingEntity>() {
        public boolean apply(@Nullable LivingEntity entity) {
            return entity != null && entity instanceof IVillagerFear;
        }
    };
    private Random rand = new Random();

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new IafRecipeRegistry());
    }

    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        onLeftClick(event.getPlayer(), event.getItemStack());
        if (event.getWorld().isRemote) {
            IceAndFire.sendMSGToServer(new MessageSwingArm());
        }
    }

    public static void onLeftClick(PlayerEntity living, ItemStack stack) {
        if (stack.getItem() == IafItemRegistry.GHOST_SWORD) {
            if (living.swingProgress == 0) {
                Multimap<Attribute, AttributeModifier> dmg = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
                double totalDmg = 0;
                for (AttributeModifier modifier : dmg.get(Attributes.ATTACK_DAMAGE)) {
                    totalDmg += modifier.getAmount();
                }
                living.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                EntityGhostSword shot = new EntityGhostSword(IafEntityRegistry.GHOST_SWORD, living.world, living, totalDmg * 0.5F);
                Vector3d vector3d = living.getLook(1.0F);
                Vector3f vector3f = new Vector3f(vector3d);
                shot.shoot(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0F, 0.5F);
                living.world.addEntity(shot);

            }
        }
    }

    private static void signalChickenAlarm(LivingEntity chicken, LivingEntity attacker) {
        float d0 = IafConfig.cockatriceChickenSearchLength;
        List<Entity> list = chicken.world.getEntitiesWithinAABB(EntityCockatrice.class, (new AxisAlignedBB(chicken.getPosX(), chicken.getPosY(), chicken.getPosZ(), chicken.getPosX() + 1.0D, chicken.getPosY() + 1.0D, chicken.getPosZ() + 1.0D)).grow(d0, 10.0D, d0));
        if (!list.isEmpty()) {
            Iterator<Entity> itr = list.iterator();
            while (itr.hasNext()) {
                Entity entity = itr.next();
                if (entity instanceof EntityCockatrice && !(attacker instanceof EntityCockatrice)) {
                    EntityCockatrice cockatrice = (EntityCockatrice) entity;
                    if (!DragonUtils.hasSameOwner(cockatrice, attacker)) {
                        if (attacker instanceof PlayerEntity) {
                            PlayerEntity player = (PlayerEntity) attacker;
                            if (!player.isCreative() && !cockatrice.isOwner(player)) {
                                cockatrice.setAttackTarget(player);
                            }
                        } else {
                            cockatrice.setAttackTarget(attacker);
                        }
                    }
                }
            }
        }
    }

    private static void signalAmphithereAlarm(LivingEntity villager, LivingEntity attacker) {
        float d0 = IafConfig.amphithereVillagerSearchLength;
        List<Entity> list = villager.world.getEntitiesWithinAABB(EntityAmphithere.class, (new AxisAlignedBB(villager.getPosX() - 1.0D, villager.getPosY() - 1.0D, villager.getPosZ() - 1.0D, villager.getPosX() + 1.0D, villager.getPosY() + 1.0D, villager.getPosZ() + 1.0D)).grow(d0, d0, d0));
        if (!list.isEmpty()) {
            Iterator<Entity> itr = list.iterator();
            while (itr.hasNext()) {
                Entity entity = itr.next();
                if (entity instanceof EntityAmphithere && !(attacker instanceof EntityAmphithere)) {
                    TameableEntity amphithere = (TameableEntity) entity;
                    if (!DragonUtils.hasSameOwner(amphithere, attacker)) {
                        if (attacker instanceof PlayerEntity) {
                            PlayerEntity player = (PlayerEntity) attacker;
                            if (!player.isCreative() && !amphithere.isOwner(player)) {
                                amphithere.setAttackTarget(player);
                            }
                        } else {
                            amphithere.setAttackTarget(attacker);
                        }
                    }
                }
            }
        }
    }

    public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        if (f > maxIncrease) {
            f = maxIncrease;
        }
        if (f < -maxIncrease) {
            f = -maxIncrease;
        }
        return angle + f;
    }

    public static boolean isLivestock(Entity entity) {
        return entity != null && EntityTypeTags.getCollection().get(IafTagRegistry.FEAR_DRAGONS).contains(entity.getType());

    }

    public static boolean isVillager(Entity entity) {
        return entity != null && EntityTypeTags.getCollection().get(IafTagRegistry.VILLAGERS).contains(entity.getType());
    }

    public static boolean isAnimaniaSheep(Entity entity) {
        return entity != null && EntityTypeTags.getCollection().get(IafTagRegistry.SHEEP).contains(entity.getType());
    }

    public static boolean isAnimaniaChicken(Entity entity) {
        return entity != null && EntityTypeTags.getCollection().get(IafTagRegistry.CHICKENS).contains(entity.getType());
    }

    public static boolean isAnimaniaFerret(Entity entity) {
        return entity != null && EntityTypeTags.getCollection().get(IafTagRegistry.SCARES_COCKATRICES).contains(entity.getType());
    }

    public static boolean isRidingOrBeingRiddenBy(Entity first, Entity entityIn) {
        for (Entity entity : first.getPassengers()) {
            if (entity.equals(entityIn)) {
                return true;
            }

            if (isRidingOrBeingRiddenBy(entity, entityIn)) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public void onArrowCollide(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof AbstractArrowEntity && ((AbstractArrowEntity) event.getEntity()).getShooter() != null) {
            if (event.getRayTraceResult() instanceof EntityRayTraceResult && ((EntityRayTraceResult) event.getRayTraceResult()).getEntity() != null) {
                Entity shootingEntity = ((AbstractArrowEntity) event.getEntity()).getShooter();
                Entity shotEntity = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();
                if (shootingEntity instanceof LivingEntity && isRidingOrBeingRiddenBy(shootingEntity, shotEntity)) {
                    if (shotEntity instanceof TameableEntity && ((TameableEntity) shotEntity).isTamed() && shotEntity.isOnSameTeam(shootingEntity)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttackMob(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityMutlipartPart && event.getEntity() instanceof PlayerEntity) {
            event.setCanceled(true);
            Entity parent = ((EntityMutlipartPart) event.getTarget()).getParent();
            try {
                ((PlayerEntity) event.getEntity()).attackTargetEntityWithCurrentItem(parent);
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Exception thrown while interacting with entity");
            }
            int extraData = 0;
            if (event.getTarget() instanceof EntityHydraHead && parent instanceof EntityHydra) {
                extraData = ((EntityHydraHead) event.getTarget()).headIndex;
                ((EntityHydra) parent).triggerHeadFlags(extraData);
            }
            if (event.getTarget().world.isRemote && parent != null) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessagePlayerHitMultipart(parent.getEntityId(), extraData));
            }
        }
    }

    @SubscribeEvent
    public void onEntityFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), MiscEntityProperties.class);
            if (properties != null && properties.hasDismountedDragon) {
                event.setDamageMultiplier(0);
                properties.hasDismountedDragon = false;
            }
        }
    }

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event) {
        /*
        if (event.getEntityBeingMounted() instanceof EntityDragonBase) {
            EntityDragonBase dragon = (EntityDragonBase) event.getEntityBeingMounted();
            if (event.isDismounting() && event.getEntityMounting() instanceof PlayerEntity && !event.getEntityMounting().world.isRemote) {
                PlayerEntity player = (PlayerEntity) event.getEntityMounting();
                if (dragon.isOwner((PlayerEntity) event.getEntityMounting())) {
                    dragon.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
                    player.fallDistance = -dragon.height;
                } else {
                    dragon.renderYawOffset = dragon.rotationYaw;
                    float modTick_0 = dragon.getAnimationTick() - 25;
                    float modTick_1 = dragon.getAnimationTick() > 25 && dragon.getAnimationTick() < 55 ? 8 * MathHelper.clamp(MathHelper.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
                    float modTick_2 = dragon.getAnimationTick() > 30 ? 10 : Math.max(0, dragon.getAnimationTick() - 20);
                    float radius = 0.75F * (0.6F * dragon.getRenderSize() / 3) * -3;
                    float angle = (0.01745329251F * dragon.renderYawOffset) + 3.15F + (modTick_1 * 2F) * 0.015F;
                    double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
                    double extraZ = (double) (radius * MathHelper.cos(angle));
                    double extraY = modTick_2 == 0 ? 0 : 0.035F * ((dragon.getRenderSize() / 3) + (modTick_2 * 0.5 * (dragon.getRenderSize() / 3)));
                    player.setPosition(dragon.getPosX() + extraX, dragon.getPosY() + extraY, dragon.getPosZ() + extraZ);
                }
            }

        }
        if (event.getEntityBeingMounted() instanceof EntityHippogryph) {
            EntityHippogryph hippogryph = (EntityHippogryph) event.getEntityBeingMounted();
            if (event.isDismounting() && event.getEntityMounting() instanceof PlayerEntity && !event.getEntityMounting().world.isRemote && hippogryph.isOwner((PlayerEntity) event.getEntityMounting())) {
                PlayerEntity player = (PlayerEntity) event.getEntityMounting();
                hippogryph.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
            }
        }
         */
    }

    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {
        if (event.getSource().isProjectile()) {
            float multi = 1;
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemTrollArmor) {
                multi -= 0.1;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemTrollArmor) {
                multi -= 0.3;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemTrollArmor) {
                multi -= 0.2;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemTrollArmor) {
                multi -= 0.1;
            }
            event.setAmount(event.getAmount() * multi);
        }
        if (event.getSource() == IafDamageRegistry.DRAGON_FIRE || event.getSource() == IafDamageRegistry.DRAGON_ICE) {
            float multi = 1;
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemScaleArmor) {
                multi -= 0.1;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemScaleArmor) {
                multi -= 0.3;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemScaleArmor) {
                multi -= 0.2;
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemScaleArmor) {
                multi -= 0.1;
            }
            event.setAmount(event.getAmount() * multi);
        }
    }

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof WitherSkeletonEntity) {
            event.getDrops().add(new ItemEntity(event.getEntity().world, event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ(),
                    new ItemStack(IafItemRegistry.WITHERBONE, event.getEntityLiving().getRNG().nextInt(2))));
        }
    }

    @SubscribeEvent
    public void onLivingAttacked(LivingAttackEvent event) {
        if (event.getSource() != null && event.getSource().getTrueSource() != null) {
            Entity attacker = event.getSource().getTrueSource();
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(attacker, MiscEntityProperties.class);
            if (properties != null && properties.inLoveTicks > 0) {
                event.setCanceled(true);
            }
            if (isAnimaniaChicken(event.getEntityLiving()) && attacker instanceof LivingEntity) {
                signalChickenAlarm(event.getEntityLiving(), (LivingEntity) attacker);
            }
            if (DragonUtils.isVillager(event.getEntityLiving()) && attacker instanceof LivingEntity) {
                signalAmphithereAlarm(event.getEntityLiving(), (LivingEntity) attacker);
            }

        }

    }

    @SubscribeEvent
    public void onLivingSetTarget(LivingSetAttackTargetEvent event) {
        if (event.getTarget() != null) {
            LivingEntity attacker = event.getEntityLiving();
            if (isAnimaniaChicken(event.getTarget())) {
                signalChickenAlarm(event.getTarget(), attacker);
            }
            if (DragonUtils.isVillager(event.getTarget())) {
                signalAmphithereAlarm(event.getTarget(), attacker);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getTarget() != null && isAnimaniaSheep(event.getTarget())) {
            float dist = IafConfig.cyclopesSheepSearchLength;
            List<Entity> list = event.getTarget().world.getEntitiesWithinAABBExcludingEntity(event.getPlayer(), event.getPlayer().getBoundingBox().expand(dist, dist, dist));
            if (!list.isEmpty()) {
                Iterator<Entity> itr = list.iterator();
                while (itr.hasNext()) {
                    Entity entity = itr.next();
                    if (entity instanceof EntityCyclops) {
                        EntityCyclops cyclops = (EntityCyclops) entity;
                        if (!cyclops.isBlinded() && !event.getPlayer().isCreative()) {
                            cyclops.setAttackTarget(event.getPlayer());
                        }
                    }
                }
            }
        }
        if (event.getTarget() instanceof EntityStoneStatue) {
            ((LivingEntity) event.getTarget()).setHealth(((LivingEntity) event.getTarget()).getMaxHealth());
            if (event.getPlayer() != null) {
                ItemStack stack = event.getPlayer().getHeldItemMainhand();
                event.getTarget().playSound(SoundEvents.BLOCK_STONE_BREAK, 2, 0.5F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F);
                if (stack.getItem() != null && (stack.getItem().canHarvestBlock(Blocks.STONE.getDefaultState()) || stack.getItem().getTranslationKey().contains("pickaxe"))) {
                    boolean silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
                    boolean ready = false;
                    event.setCanceled(true);
                    EntityStoneStatue statue = (EntityStoneStatue) event.getTarget();
                    statue.setCrackAmount(statue.getCrackAmount() + 1);
                    ready = statue.getCrackAmount() > 9;
                    if (ready) {
                        CompoundNBT writtenTag = new CompoundNBT();
                        event.getTarget().writeWithoutTypeId(writtenTag);
                        event.getTarget().playSound(SoundEvents.BLOCK_STONE_BREAK, 2, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F);
                        event.getTarget().remove();
                        if (silkTouch) {
                            ItemStack statuette = new ItemStack(IafItemRegistry.STONE_STATUE);
                            statuette.setTag(new CompoundNBT());
                            statuette.getTag().putBoolean("IAFStoneStatuePlayerEntity", statue.getTrappedEntityTypeString().equalsIgnoreCase("minecraft:player"));
                            statuette.getTag().putString("IAFStoneStatueEntityID", statue.getTrappedEntityTypeString());
                            statuette.getTag().put("IAFStoneStatueNBT", writtenTag);
                            ((LivingEntity) event.getTarget()).writeAdditional(statuette.getTag());
                            if (!event.getTarget().world.isRemote) {
                                event.getTarget().entityDropItem(statuette, 1);
                            }
                        } else {
                            if (!((LivingEntity) event.getTarget()).world.isRemote) {
                                event.getTarget().entityDropItem(Item.getItemFromBlock(Blocks.COBBLESTONE), 2 + event.getEntityLiving().getRNG().nextInt(4));
                            }
                        }
                        event.getTarget().remove();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDie(LivingDeathEvent event) {
        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntity(), ChainEntityProperties.class);
        if (chainProperties != null) {
            chainProperties.minimizeLists();
            if (!event.getEntity().world.isRemote) {
                ItemEntity entityitem = new ItemEntity(event.getEntity().world, event.getEntity().getPosX(), event.getEntity().getPosY() + (double) 1, event.getEntity().getPosZ(), new ItemStack(IafItemRegistry.CHAIN, chainProperties.connectedEntities.size()));
                entityitem.setDefaultPickupDelay();
                event.getEntity().world.addEntity(entityitem);
            }
            chainProperties.clearChained();
        }
        if (event.getEntityLiving().getUniqueID().equals(ServerEvents.ALEX_UUID)) {
            event.getEntityLiving().entityDropItem(new ItemStack(IafItemRegistry.WEEZER_BLUE_ALBUM), 1);
        }
        if (event.getEntityLiving() instanceof PlayerEntity && IafConfig.ghostsFromPlayerDeaths) {
            CombatTracker combat = event.getEntityLiving().getCombatTracker();
            CombatEntry entry = combat.getBestCombatEntry();
            Entity attacker = event.getEntityLiving().getRevengeTarget();
            if (attacker instanceof PlayerEntity && event.getEntityLiving().getRNG().nextInt(3) == 0) {
                boolean flag = false;
                if (entry != null && (entry.getDamageSrc() == DamageSource.FALL || entry.getDamageSrc() == DamageSource.DROWN || entry.getDamageSrc() == DamageSource.LAVA)) {
                    flag = true;
                }
                if (event.getEntityLiving().isPotionActive(Effects.POISON)) {
                    flag = true;
                }
                World world = event.getEntityLiving().world;
                if (flag) {
                    EntityGhost ghost = IafEntityRegistry.GHOST.create(world);
                    ghost.copyLocationAndAnglesFrom(event.getEntityLiving());
                    if (!world.isRemote) {
                        ghost.onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(event.getEntityLiving().getPosition()), SpawnReason.SPAWNER, null, null);
                        world.addEntity(ghost);
                    }
                    ghost.setDaytimeMode(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityStopUsingItem(LivingEntityUseItemEvent.Tick event) {
        if (event.getItem().getItem() instanceof ItemDeathwormGauntlet || event.getItem().getItem() instanceof ItemCockatriceScepter) {
            event.setDuration(20);
        }
    }

    @SubscribeEvent
    public void onEntityUseItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().rotationPitch > 87 && event.getEntityLiving().getRidingEntity() != null && event.getEntityLiving().getRidingEntity() instanceof EntityDragonBase) {
            ((EntityDragonBase) event.getEntityLiving().getRidingEntity()).getEntityInteractionResult((PlayerEntity) event.getEntityLiving(), event.getHand());
        }
        if (event.getEntityLiving() instanceof EntityDragonBase && !event.getEntityLiving().isAlive()) {
            event.setResult(Event.Result.DENY);
            ((EntityDragonBase) event.getEntityLiving()).getEntityInteractionResult(event.getPlayer(), event.getHand());
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntity(), ChainEntityProperties.class);
        if (chainProperties != null && chainProperties.isChained()) {
            if (chainProperties.wasJustDisconnected) {
                chainProperties.wasJustDisconnected = false;
            }
            if (!event.getEntityLiving().world.isRemote) {
                chainProperties.updateConnectedEntities(event.getEntityLiving());
                for (Entity chainer : chainProperties.connectedEntities) {
                    float f = event.getEntityLiving().getDistance(chainer);
                    if (f > 7) {
                        double d0 = (chainer.getPosX() - event.getEntityLiving().getPosX()) / (double) f;
                        double d1 = (chainer.getPosY() - event.getEntityLiving().getPosY()) / (double) f;
                        double d2 = (chainer.getPosZ() - event.getEntityLiving().getPosZ()) / (double) f;
                        event.getEntityLiving().setMotion(event.getEntity().getMotion().add(d0 * Math.abs(d0) * 0.4D, d1 * Math.abs(d1) * 0.2D, d2 * Math.abs(d2) * 0.4D));
                    }
                }
            }
        }
        try {
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemSeaSerpentArmor || event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemSeaSerpentArmor) {
                event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 50, 0, false, false));
                if (event.getEntityLiving().isWet()) {
                    int headMod = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
                    int chestMod = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
                    int legMod = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
                    int footMod = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemSeaSerpentArmor ? 1 : 0;
                    event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.STRENGTH, 50, headMod + chestMod + legMod + footMod - 1, false, false));
                }
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof ItemBlindfold) {
                event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.BLINDNESS, 50, 0, false, false));

            }
        } catch (Exception e) {

        }
        if (IafConfig.chickensLayRottenEggs && !event.getEntityLiving().world.isRemote && isAnimaniaChicken(event.getEntityLiving()) && !event.getEntityLiving().isChild() && event.getEntityLiving() instanceof AnimalEntity) {
            ChickenEntityProperties chickenProps = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), ChickenEntityProperties.class);
            if (chickenProps != null) {
                if (chickenProps.timeUntilNextEgg < 0) {
                    chickenProps.timeUntilNextEgg = 0;
                }
                if (chickenProps.timeUntilNextEgg == 0) {
                    if (event.getEntityLiving().getRNG().nextInt(IafConfig.cockatriceEggChance + 1) == 0 && event.getEntityLiving().ticksExisted > 30) {
                        event.getEntityLiving().playSound(SoundEvents.ENTITY_CHICKEN_HURT, 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        event.getEntityLiving().playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        event.getEntityLiving().entityDropItem(IafItemRegistry.ROTTEN_EGG, 1);
                    }
                    chickenProps.timeUntilNextEgg = chickenProps.generateTime();
                } else if (chickenProps.timeUntilNextEgg > 0) {
                    chickenProps.timeUntilNextEgg--;
                }
            }

        }
        FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), FrozenEntityProperties.class);
        if (frozenProps != null) {
            boolean prevFrozen = frozenProps.isFrozen;
            if (event.getEntityLiving() instanceof EntityIceDragon) {
                frozenProps.isFrozen = false;
            }
            if (!event.getEntityLiving().world.isRemote) {
                if (frozenProps.isFrozen && event.getEntityLiving().isBurning()) {
                    frozenProps.isFrozen = false;
                    event.getEntityLiving().extinguish();
                }
                if (event.getEntityLiving().deathTime > 0) {
                    frozenProps.isFrozen = false;
                }
                if (frozenProps.ticksUntilUnfrozen > 0) {
                    frozenProps.ticksUntilUnfrozen--;
                } else {
                    frozenProps.ticksUntilUnfrozen = 0;
                    frozenProps.isFrozen = false;
                }
            }
            if (frozenProps.isFrozen && !(event.getEntityLiving() instanceof PlayerEntity && ((PlayerEntity) event.getEntityLiving()).isCreative())) {
                event.getEntity().setMotion(event.getEntity().getMotion().mul(0.25F, 1, 0.25F));
                if (!(event.getEntityLiving() instanceof EnderDragonEntity) && !event.getEntityLiving().isOnGround()) {
                    event.getEntity().setMotion(event.getEntity().getMotion().add(0, -0.2, 0));
                }

            }
            if (prevFrozen != frozenProps.isFrozen) {
                if (frozenProps.isFrozen) {
                    event.getEntityLiving().playSound(SoundEvents.BLOCK_GLASS_PLACE, 1, 1);
                } else {
                    for (int i = 0; i < 15; i++) {
                        event.getEntityLiving().world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, IafBlockRegistry.DRAGON_ICE.getDefaultState()), event.getEntityLiving().getPosX() + ((rand.nextDouble() - 0.5D) * event.getEntityLiving().getWidth()), event.getEntityLiving().getPosY() + ((rand.nextDouble()) * event.getEntityLiving().getHeight()), event.getEntityLiving().getPosZ() + ((rand.nextDouble() - 0.5D) * event.getEntityLiving().getWidth()), 0, 0, 0);
                    }
                    event.getEntityLiving().playSound(SoundEvents.BLOCK_GLASS_BREAK, 3, 1);
                }
            }
        }

        if (event.getEntityLiving() instanceof PlayerEntity || event.getEntityLiving() instanceof AbstractVillagerEntity || event.getEntityLiving() instanceof IHearsSiren) {
            SirenEntityProperties sirenProps = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), SirenEntityProperties.class);
            if (sirenProps != null && sirenProps.sirenID != 0) {
                EntitySiren closestSiren = sirenProps.getSiren(event.getEntityLiving().world);
                if (closestSiren != null && closestSiren.isActuallySinging()) {
                    if (EntitySiren.isWearingEarplugs(event.getEntityLiving()) || sirenProps.singTime > IafConfig.sirenMaxSingTime) {
                        sirenProps.isCharmed = false;
                        sirenProps.sirenID = 0;
                        sirenProps.singTime = 0;
                        closestSiren.singCooldown = IafConfig.sirenTimeBetweenSongs;
                    } else {
                        sirenProps.isCharmed = true;
                        sirenProps.singTime++;
                        if (rand.nextInt(7) == 0) {
                            for (int i = 0; i < 5; i++) {
                                event.getEntityLiving().world.addParticle(ParticleTypes.HEART, event.getEntityLiving().getPosX() + ((rand.nextDouble() - 0.5D) * 3), event.getEntityLiving().getPosY() + ((rand.nextDouble() - 0.5D) * 3), event.getEntityLiving().getPosZ() + ((rand.nextDouble() - 0.5D) * 3), 0, 0, 0);
                            }
                        }
                        LivingEntity entity = event.getEntityLiving();
                        if (entity.collidedHorizontally) {
                            if (entity instanceof LivingEntity) {
                                entity.setJumping(true);
                            } else if (entity.isOnGround()) {
                                entity.setMotion(entity.getMotion().add(0, 0.42, 0));
                            }
                        }
                        double motionXAdd = (Math.signum(closestSiren.getPosX() - entity.getPosX()) * 0.5D - entity.getMotion().x) * 0.100000000372529;
                        double motionYAdd = (Math.signum(closestSiren.getPosY() - entity.getPosY() + 1) * 0.5D - entity.getMotion().y) * 0.100000000372529;
                        double motionZAdd = (Math.signum(closestSiren.getPosZ() - entity.getPosZ()) * 0.5D - entity.getMotion().z) * 0.100000000372529;
                        entity.setMotion(entity.getMotion().add(motionXAdd, motionYAdd, motionZAdd));
                        float angle = (float) (Math.atan2(entity.getMotion().z, entity.getMotion().x) * 180.0D / Math.PI) - 90.0F;
                        double d0 = closestSiren.getPosX() - entity.getPosX();
                        double d2 = closestSiren.getPosZ() - entity.getPosZ();
                        double d1 = closestSiren.getPosY() - 1 - entity.getPosY();
                        if (entity.isPassenger()) {
                            entity.stopRiding();
                        }
                        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
                        float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                        float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
                        if (!(entity instanceof PlayerEntity)) {
                            entity.rotationPitch = updateRotation(entity.rotationPitch, f1, 30F);
                            entity.rotationYaw = updateRotation(entity.rotationYaw, f, 30F);
                        }
                        if (entity.getDistance(closestSiren) < 5D) {
                            sirenProps.isCharmed = false;
                            sirenProps.sirenID = 0;
                            sirenProps.singTime = 0;
                            closestSiren.singCooldown = IafConfig.sirenTimeBetweenSongs;
                            closestSiren.setSinging(false);
                            closestSiren.setAttackTarget(entity);
                            closestSiren.setAggressive(true);
                            closestSiren.triggerOtherSirens(entity);
                        }
                        if (!closestSiren.isAlive() || entity.getDistance(closestSiren) > EntitySiren.SEARCH_RANGE * 2 || sirenProps.getSiren(event.getEntityLiving().world) == null || entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) {
                            sirenProps.isCharmed = false;
                            sirenProps.sirenID = 0;
                            sirenProps.singTime = 0;
                        }
                    }
                }
            }
        }
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntityLiving(), MiscEntityProperties.class);
        if (properties != null && properties.entitiesWeAreGlaringAt.size() > 0) {
            Iterator<Entity> itr = properties.entitiesWeAreGlaringAt.iterator();
            while (itr.hasNext()) {
                Entity next = itr.next();
                double d5 = 80F;
                double d0 = next.getPosX() - event.getEntityLiving().getPosX();
                double d1 = next.getPosY() + (double) (next.getHeight() * 0.5F) - (event.getEntityLiving().getPosY() + (double) event.getEntityLiving().getEyeHeight() * 0.5D);
                double d2 = next.getPosZ() - event.getEntityLiving().getPosZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d0 = d0 / d3;
                d1 = d1 / d3;
                d2 = d2 / d3;
                double d4 = this.rand.nextDouble();
                while (d4 < d3) {
                    d4 += 1.0D;
                    event.getEntityLiving().world.addParticle(ParticleTypes.ENTITY_EFFECT, event.getEntityLiving().getPosX() + d0 * d4, event.getEntityLiving().getPosY() + d1 * d4 + (double) event.getEntityLiving().getEyeHeight() * 0.5D, event.getEntityLiving().getPosZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
                }
                ((LivingEntity) next).addPotionEffect(new EffectInstance(Effects.WITHER, 40, 2));
                if (event.getEntityLiving().ticksExisted % 20 == 0) {
                    properties.specialWeaponDmg++;
                    next.attackEntityFrom(DamageSource.WITHER, 2);
                }
                if (next == null || !next.isAlive()) {
                    itr.remove();
                }
            }
        }
        if (properties != null && properties.glarers.size() > 0) {
            Iterator<Entity> itr = properties.glarers.iterator();
            while (itr.hasNext()) {
                Entity next = itr.next();
                if (next instanceof LivingEntity && !EntityGorgon.isEntityLookingAt((LivingEntity) next, event.getEntityLiving(), 0.2F)) {
                    MiscEntityProperties theirProperties = EntityPropertiesHandler.INSTANCE.getProperties(next, MiscEntityProperties.class);
                    theirProperties.entitiesWeAreGlaringAt.remove(event.getEntityLiving());
                    itr.remove();

                }
            }
        }
        if (properties != null && properties.inLoveTicks > 0) {
            properties.inLoveTicks--;
            if (event.getEntityLiving() instanceof MobEntity) {
                ((MobEntity) event.getEntityLiving()).setAttackTarget(null);
            }
            if (rand.nextInt(7) == 0) {
                for (int i = 0; i < 5; i++) {
                    event.getEntityLiving().world.addParticle(ParticleTypes.HEART, event.getEntityLiving().getPosX() + ((rand.nextDouble() - 0.5D) * 3), event.getEntityLiving().getPosY() + ((rand.nextDouble() - 0.5D) * 3), event.getEntityLiving().getPosZ() + ((rand.nextDouble() - 0.5D) * 3), 0, 0, 0);
                }
            }
        }
        if (AiDebug.isEnabled() && event.getEntityLiving() instanceof MobEntity && AiDebug.contains((MobEntity) event.getEntityLiving())){
            AiDebug.logData();
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(event.getTarget(), ChainEntityProperties.class);
        if (chainProperties != null) {
            //chainProperties.debug();
            chainProperties.updateConnectedEntities(event.getTarget());
            if (chainProperties.isChained() && chainProperties.isConnectedToEntity(event.getTarget(), event.getPlayer())) {
                chainProperties.removeChain(event.getTarget(), event.getPlayer());
                if (!event.getWorld().isRemote) {
                    event.getTarget().entityDropItem(IafItemRegistry.CHAIN, 1);
                }
            }
        }
        if (AiDebug.isEnabled() && !event.getWorld().isRemote() && event.getTarget() instanceof MobEntity && event.getItemStack().getItem() == Items.STICK ){
            AiDebug.addEntity((MobEntity) event.getTarget());
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() != null && event.getRayTraceResult() instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityResult = (EntityRayTraceResult) event.getRayTraceResult();
            if (entityResult.getEntity() != null && entityResult.getEntity() instanceof EntityGhost) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer() != null && (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof AbstractChestBlock)) {
            float dist = IafConfig.dragonGoldSearchLength;
            List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getPlayer(), event.getPlayer().getBoundingBox().expand(dist, dist, dist));
            if (!list.isEmpty()) {
                Iterator<Entity> itr = list.iterator();
                while (itr.hasNext()) {
                    Entity entity = itr.next();
                    if (entity instanceof EntityDragonBase) {
                        EntityDragonBase dragon = (EntityDragonBase) entity;
                        if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getPlayer()) && !event.getPlayer().isCreative()) {
                            dragon.setQueuedToSit(false);
                            dragon.setSitting(false);
                            dragon.setAttackTarget(event.getPlayer());
                        }
                    }
                }
            }
        }
        if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof WallBlock) {
            ItemChain.attachToFence(event.getPlayer(), event.getWorld(), event.getPos());
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && (event.getState().getBlock() instanceof AbstractChestBlock || event.getState().getBlock() == IafBlockRegistry.GOLD_PILE || event.getState().getBlock() == IafBlockRegistry.SILVER_PILE || event.getState().getBlock() == IafBlockRegistry.COPPER_PILE)) {
            float dist = IafConfig.dragonGoldSearchLength;
            List<Entity> list = event.getWorld().getEntitiesWithinAABBExcludingEntity(event.getPlayer(), event.getPlayer().getBoundingBox().expand(dist, dist, dist));
            if (!list.isEmpty()) {
                Iterator<Entity> itr = list.iterator();
                while (itr.hasNext()) {
                    Entity entity = itr.next();
                    if (entity instanceof EntityDragonBase) {
                        EntityDragonBase dragon = (EntityDragonBase) entity;
                        if (!dragon.isTamed() && !dragon.isModelDead() && !dragon.isOwner(event.getPlayer()) && !event.getPlayer().isCreative()) {
                            dragon.setQueuedToSit(false);
                            dragon.setSitting(false);
                            dragon.setAttackTarget(event.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChestGenerated(LootTableLoadEvent event) {
        if (event.getName().equals(LootTables.CHESTS_SIMPLE_DUNGEON) || event.getName().equals(LootTables.CHESTS_ABANDONED_MINESHAFT)
                || event.getName().equals(LootTables.CHESTS_DESERT_PYRAMID) || event.getName().equals(LootTables.CHESTS_JUNGLE_TEMPLE)
                || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CORRIDOR) || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CROSSING)
                || event.getName().equals(LootTables.CHESTS_VILLAGE_VILLAGE_CARTOGRAPHER)) {

            LootEntry.Builder item = ItemLootEntry.builder(IafItemRegistry.MANUSCRIPT).quality(20).weight(5);
            LootPool.Builder builder = new LootPool.Builder().name("iaf_manuscript").addEntry(item).acceptCondition(RandomChance.builder(0.35f)).rolls(new RandomValueRange(1, 4)).bonusRolls(0, 3);
            event.getTable().addPool(builder.build());
        }
        if (IafConfig.generateSilverOre && (event.getName().equals(LootTables.CHESTS_SIMPLE_DUNGEON) || event.getName().equals(LootTables.CHESTS_ABANDONED_MINESHAFT)
                || event.getName().equals(LootTables.CHESTS_DESERT_PYRAMID) || event.getName().equals(LootTables.CHESTS_JUNGLE_TEMPLE)
                || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CORRIDOR) || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CROSSING)
                || event.getName().equals(LootTables.CHESTS_IGLOO_CHEST) || event.getName().equals(LootTables.CHESTS_WOODLAND_MANSION)
                || event.getName().equals(LootTables.CHESTS_VILLAGE_VILLAGE_TOOLSMITH) || event.getName().equals(LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER))) {
            LootEntry.Builder item = ItemLootEntry.builder(IafItemRegistry.SILVER_INGOT).quality(15).weight(12);
            LootPool.Builder builder = new LootPool.Builder().name("iaf_silver_ingot").addEntry(item).acceptCondition(RandomChance.builder(0.5f)).rolls(new RandomValueRange(1, 3)).bonusRolls(0, 2);
            event.getTable().addPool(builder.build());

        }
        if (IafConfig.generateCopperOre && (event.getName().equals(LootTables.CHESTS_SIMPLE_DUNGEON) || event.getName().equals(LootTables.CHESTS_ABANDONED_MINESHAFT)
                || event.getName().equals(LootTables.CHESTS_DESERT_PYRAMID) || event.getName().equals(LootTables.CHESTS_JUNGLE_TEMPLE)
                || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CORRIDOR) || event.getName().equals(LootTables.CHESTS_STRONGHOLD_CROSSING)
                || event.getName().equals(LootTables.CHESTS_IGLOO_CHEST) || event.getName().equals(LootTables.CHESTS_WOODLAND_MANSION)
                || event.getName().equals(LootTables.CHESTS_VILLAGE_VILLAGE_TOOLSMITH) || event.getName().equals(LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER))) {
            LootEntry.Builder item = ItemLootEntry.builder(IafItemRegistry.COPPER_INGOT).quality(10).weight(14);
            LootPool.Builder builder = new LootPool.Builder().name("iaf_copper_ingot").addEntry(item).acceptCondition(RandomChance.builder(0.6f)).rolls(new RandomValueRange(1, 2)).bonusRolls(0, 3);
            event.getTable().addPool(builder.build());

        }
        if ((event.getName().equals(WorldGenFireDragonCave.FIREDRAGON_CHEST)
                || event.getName().equals(WorldGenFireDragonCave.FIREDRAGON_MALE_CHEST)
                || event.getName().equals(WorldGenIceDragonCave.ICEDRAGON_CHEST)
                || event.getName().equals(WorldGenIceDragonCave.ICEDRAGON_MALE_CHEST))) {
            LootEntry.Builder item = ItemLootEntry.builder(IafItemRegistry.WEEZER_BLUE_ALBUM).quality(100).weight(1);
            LootPool.Builder builder = new LootPool.Builder().name("iaf_weezer").addEntry(item).acceptCondition(RandomChance.builder(0.01f)).rolls(new RandomValueRange(1, 1)).bonusRolls(0, 0);
            event.getTable().addPool(builder.build());
        }
    }

    @SubscribeEvent
    public void onPlayerLeaveEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().getPassengers().isEmpty()) {
            Iterator<Entity> itr = event.getPlayer().getPassengers().iterator();
            while (itr.hasNext()) {
                (itr.next()).stopRiding();
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        try {
            if (event.getEntity() instanceof LivingEntity) {
                try {
                    ChainEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(event.getEntity(), ChainEntityProperties.class);
                    if (properties != null) {
                        properties.updateConnectedEntities(event.getEntity());
                    }
                } catch (Exception e) {
                    IceAndFire.LOGGER.warn("could not instantiate chain properties for " + event.getEntity().getName());
                }
            }
            if (event.getEntity() != null && isAnimaniaSheep(event.getEntity()) && event.getEntity() instanceof AnimalEntity) {
                AnimalEntity animal = (AnimalEntity) event.getEntity();
                animal.goalSelector.addGoal(8, new EntitySheepAIFollowCyclops(animal, 1.2D));
            }
            if (event.getEntity() != null && isVillager(event.getEntity()) && event.getEntity() instanceof MobEntity && IafConfig.villagersFearDragons) {
                MobEntity villager = (MobEntity) event.getEntity();
                villager.goalSelector.addGoal(1, new VillagerAIFearUntamed((CreatureEntity) villager, LivingEntity.class, 8.0F, 0.8D, 0.8D, VILLAGER_FEAR));
            }
            if (event.getEntity() != null && isLivestock(event.getEntity()) && event.getEntity() instanceof MobEntity && IafConfig.animalsFearDragons) {
                MobEntity animal = (MobEntity) event.getEntity();
                animal.goalSelector.addGoal(1, new VillagerAIFearUntamed((CreatureEntity) animal, LivingEntity.class, 30, 1.0D, 0.5D, new java.util.function.Predicate<LivingEntity>() {
                    public boolean test(LivingEntity entity) {
                        return entity != null && entity instanceof IAnimalFear && ((IAnimalFear) entity).shouldAnimalsFear(animal);
                    }
                }));
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == IafVillagerRegistry.SCRIBE) {
            IafVillagerRegistry.addScribeTrades(event.getTrades());
        }
    }
}
