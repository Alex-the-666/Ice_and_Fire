package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

public enum EnumDragonTextures {
    VARIANT1("red_", "blue_", "electric_"),
    VARIANT2("green_", "white_", "amythest_"),
    VARIANT3("bronze_", "sapphire_", "copper_"),
    VARIANT4("gray_", "silver_", "black_");

    public final ResourceLocation FIRESTAGE1TEXTURE;
    public final ResourceLocation FIRESTAGE2TEXTURE;
    public final ResourceLocation FIRESTAGE3TEXTURE;
    public final ResourceLocation FIRESTAGE4TEXTURE;
    public final ResourceLocation FIRESTAGE5TEXTURE;
    public final ResourceLocation ICESTAGE1TEXTURE;
    public final ResourceLocation ICESTAGE2TEXTURE;
    public final ResourceLocation ICESTAGE3TEXTURE;
    public final ResourceLocation ICESTAGE4TEXTURE;
    public final ResourceLocation ICESTAGE5TEXTURE;
    public final ResourceLocation FIRESTAGE1SLEEPINGTEXTURE;
    public final ResourceLocation FIRESTAGE2SLEEPINGTEXTURE;
    public final ResourceLocation FIRESTAGE3SLEEPINGTEXTURE;
    public final ResourceLocation FIRESTAGE4SLEEPINGTEXTURE;
    public final ResourceLocation FIRESTAGE5SLEEPINGTEXTURE;
    public final ResourceLocation ICESTAGE1SLEEPINGTEXTURE;
    public final ResourceLocation ICESTAGE2SLEEPINGTEXTURE;
    public final ResourceLocation ICESTAGE3SLEEPINGTEXTURE;
    public final ResourceLocation ICESTAGE4SLEEPINGTEXTURE;
    public final ResourceLocation ICESTAGE5SLEEPINGTEXTURE;
    public final ResourceLocation FIRESTAGE1EYESTEXTURE;
    public final ResourceLocation FIRESTAGE2EYESTEXTURE;
    public final ResourceLocation FIRESTAGE3EYESTEXTURE;
    public final ResourceLocation FIRESTAGE4EYESTEXTURE;
    public final ResourceLocation FIRESTAGE5EYESTEXTURE;
    public final ResourceLocation ICESTAGE1EYESTEXTURE;
    public final ResourceLocation ICESTAGE2EYESTEXTURE;
    public final ResourceLocation ICESTAGE3EYESTEXTURE;
    public final ResourceLocation ICESTAGE4EYESTEXTURE;
    public final ResourceLocation ICESTAGE5EYESTEXTURE;
    public final ResourceLocation FIRESTAGE1SKELETONTEXTURE;
    public final ResourceLocation FIRESTAGE2SKELETONTEXTURE;
    public final ResourceLocation FIRESTAGE3SKELETONTEXTURE;
    public final ResourceLocation FIRESTAGE4SKELETONTEXTURE;
    public final ResourceLocation FIRESTAGE5SKELETONTEXTURE;
    public final ResourceLocation ICESTAGE1SKELETONTEXTURE;
    public final ResourceLocation ICESTAGE2SKELETONTEXTURE;
    public final ResourceLocation ICESTAGE3SKELETONTEXTURE;
    public final ResourceLocation ICESTAGE4SKELETONTEXTURE;
    public final ResourceLocation ICESTAGE5SKELETONTEXTURE;
    public final ResourceLocation FIRE_MALE_OVERLAY;
    public final ResourceLocation ICE_MALE_OVERLAY;

    public final ResourceLocation LIGHTNINGSTAGE1TEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE2TEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE3TEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE4TEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE5TEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE1SLEEPINGTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE2SLEEPINGTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE3SLEEPINGTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE4SLEEPINGTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE5SLEEPINGTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE1EYESTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE2EYESTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE3EYESTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE4EYESTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE5EYESTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE1SKELETONTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE2SKELETONTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE3SKELETONTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE4SKELETONTEXTURE;
    public final ResourceLocation LIGHTNINGSTAGE5SKELETONTEXTURE;

    public final ResourceLocation LIGHTNING_MALE_OVERLAY;

    EnumDragonTextures(String fireVariant, String iceVariant, String lightningVariant) {
        FIRESTAGE1TEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "1.png");
        FIRESTAGE2TEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "2.png");
        FIRESTAGE3TEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "3.png");
        FIRESTAGE4TEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "4.png");
        FIRESTAGE5TEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "5.png");
        FIRESTAGE1SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "1_sleeping.png");
        FIRESTAGE2SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "2_sleeping.png");
        FIRESTAGE3SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "3_sleeping.png");
        FIRESTAGE4SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "4_sleeping.png");
        FIRESTAGE5SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "5_sleeping.png");
        FIRESTAGE1EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "1_eyes.png");
        FIRESTAGE2EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "2_eyes.png");
        FIRESTAGE3EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "3_eyes.png");
        FIRESTAGE4EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "4_eyes.png");
        FIRESTAGE5EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + fireVariant + "5_eyes.png");
        FIRESTAGE1SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_1.png");
        FIRESTAGE2SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_2.png");
        FIRESTAGE3SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_3.png");
        FIRESTAGE4SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_4.png");
        FIRESTAGE5SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/fire_skeleton_5.png");
        ICESTAGE1TEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "1.png");
        ICESTAGE2TEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "2.png");
        ICESTAGE3TEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "3.png");
        ICESTAGE4TEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "4.png");
        ICESTAGE5TEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "5.png");
        ICESTAGE1SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "1_sleeping.png");
        ICESTAGE2SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "2_sleeping.png");
        ICESTAGE3SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "3_sleeping.png");
        ICESTAGE4SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "4_sleeping.png");
        ICESTAGE5SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "5_sleeping.png");
        ICESTAGE1EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "1_eyes.png");
        ICESTAGE2EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "2_eyes.png");
        ICESTAGE3EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "3_eyes.png");
        ICESTAGE4EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "4_eyes.png");
        ICESTAGE5EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + iceVariant + "5_eyes.png");
        ICESTAGE1SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_1.png");
        ICESTAGE2SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_2.png");
        ICESTAGE3SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_3.png");
        ICESTAGE4SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_4.png");
        ICESTAGE5SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/ice_skeleton_5.png");
        FIRE_MALE_OVERLAY = new ResourceLocation("iceandfire:textures/models/firedragon/male_" + fireVariant.substring(0, fireVariant.length() - 1) + ".png");
        ICE_MALE_OVERLAY = new ResourceLocation("iceandfire:textures/models/icedragon/male_" + iceVariant.substring(0, iceVariant.length() - 1) + ".png");

        LIGHTNINGSTAGE1TEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "1.png");
        LIGHTNINGSTAGE2TEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "2.png");
        LIGHTNINGSTAGE3TEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "3.png");
        LIGHTNINGSTAGE4TEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "4.png");
        LIGHTNINGSTAGE5TEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "5.png");
        LIGHTNINGSTAGE1SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "1_sleeping.png");
        LIGHTNINGSTAGE2SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "2_sleeping.png");
        LIGHTNINGSTAGE3SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "3_sleeping.png");
        LIGHTNINGSTAGE4SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "4_sleeping.png");
        LIGHTNINGSTAGE5SLEEPINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "5_sleeping.png");
        LIGHTNINGSTAGE1EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "1_eyes.png");
        LIGHTNINGSTAGE2EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "2_eyes.png");
        LIGHTNINGSTAGE3EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "3_eyes.png");
        LIGHTNINGSTAGE4EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "4_eyes.png");
        LIGHTNINGSTAGE5EYESTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + lightningVariant + "5_eyes.png");
        LIGHTNINGSTAGE1SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/lightning_skeleton_1.png");
        LIGHTNINGSTAGE2SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/lightning_skeleton_2.png");
        LIGHTNINGSTAGE3SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/lightning_skeleton_3.png");
        LIGHTNINGSTAGE4SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/lightning_skeleton_4.png");
        LIGHTNINGSTAGE5SKELETONTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/lightning_skeleton_5.png");
        LIGHTNING_MALE_OVERLAY = new ResourceLocation("iceandfire:textures/models/lightningdragon/male_" + lightningVariant.substring(0, lightningVariant.length() - 1) + ".png");

    }


    public static ResourceLocation getTextureFromDragon(EntityDragonBase dragon) {
        if (dragon instanceof EntityIceDragon) {
            return getIceDragonTextures(dragon);
        } else if (dragon instanceof EntityLightningDragon) {
            return getLightningDragonTextures(dragon);
        } else {
            return getFireDragonTextures(dragon);
        }
    }


    public static ResourceLocation getEyeTextureFromDragon(EntityDragonBase dragon) {
        EnumDragonTextures textures = getDragonEnum(dragon);
        if (dragon instanceof EntityIceDragon) {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.ICESTAGE1EYESTEXTURE;
                case 2:
                    return textures.ICESTAGE2EYESTEXTURE;
                case 3:
                    return textures.ICESTAGE3EYESTEXTURE;
                case 4:
                    return textures.ICESTAGE4EYESTEXTURE;
                case 5:
                    return textures.ICESTAGE5EYESTEXTURE;
                default:
                    return textures.ICESTAGE4EYESTEXTURE;
            }
        } else if (dragon instanceof EntityLightningDragon) {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.LIGHTNINGSTAGE1EYESTEXTURE;
                case 2:
                    return textures.LIGHTNINGSTAGE2EYESTEXTURE;
                case 3:
                    return textures.LIGHTNINGSTAGE3EYESTEXTURE;
                case 4:
                    return textures.LIGHTNINGSTAGE4EYESTEXTURE;
                case 5:
                    return textures.LIGHTNINGSTAGE5EYESTEXTURE;
                default:
                    return textures.LIGHTNINGSTAGE4EYESTEXTURE;
            }
        } else {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.FIRESTAGE1EYESTEXTURE;
                case 2:
                    return textures.FIRESTAGE2EYESTEXTURE;
                case 3:
                    return textures.FIRESTAGE3EYESTEXTURE;
                case 4:
                    return textures.FIRESTAGE4EYESTEXTURE;
                case 5:
                    return textures.FIRESTAGE5EYESTEXTURE;
                default:
                    return textures.FIRESTAGE4EYESTEXTURE;
            }
        }
    }

    private static ResourceLocation getFireDragonTextures(EntityDragonBase dragon) {
        EnumDragonTextures textures = getDragonEnum(dragon);
        if (dragon.isModelDead()) {
            if (dragon.getDeathStage() >= (dragon.getAgeInDays() / 5) / 2) {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.FIRESTAGE1SKELETONTEXTURE;
                    case 2:
                        return textures.FIRESTAGE2SKELETONTEXTURE;
                    case 3:
                        return textures.FIRESTAGE3SKELETONTEXTURE;
                    case 4:
                        return textures.FIRESTAGE4SKELETONTEXTURE;
                    case 5:
                        return textures.FIRESTAGE5SKELETONTEXTURE;
                    default:
                        return textures.FIRESTAGE4SKELETONTEXTURE;
                }
            } else {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.FIRESTAGE1SLEEPINGTEXTURE;
                    case 2:
                        return textures.FIRESTAGE2SLEEPINGTEXTURE;
                    case 3:
                        return textures.FIRESTAGE3SLEEPINGTEXTURE;
                    case 4:
                        return textures.FIRESTAGE4SLEEPINGTEXTURE;
                    case 5:
                        return textures.FIRESTAGE5SLEEPINGTEXTURE;
                    default:
                        return textures.FIRESTAGE4SLEEPINGTEXTURE;
                }
            }
        }
        if (dragon.isSleeping() || dragon.isBlinking()) {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.FIRESTAGE1SLEEPINGTEXTURE;
                case 2:
                    return textures.FIRESTAGE2SLEEPINGTEXTURE;
                case 3:
                    return textures.FIRESTAGE3SLEEPINGTEXTURE;
                case 4:
                    return textures.FIRESTAGE4SLEEPINGTEXTURE;
                case 5:
                    return textures.FIRESTAGE5SLEEPINGTEXTURE;
                default:
                    return textures.FIRESTAGE4SLEEPINGTEXTURE;
            }
        } else {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.FIRESTAGE1TEXTURE;
                case 2:
                    return textures.FIRESTAGE2TEXTURE;
                case 3:
                    return textures.FIRESTAGE3TEXTURE;
                case 4:
                    return textures.FIRESTAGE4TEXTURE;
                case 5:
                    return textures.FIRESTAGE5TEXTURE;
                default:
                    return textures.FIRESTAGE4TEXTURE;
            }
        }
    }

    private static ResourceLocation getIceDragonTextures(EntityDragonBase dragon) {
        EnumDragonTextures textures = getDragonEnum(dragon);
        if (dragon.isModelDead()) {
            if (dragon.getDeathStage() >= (dragon.getAgeInDays() / 5) / 2) {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.ICESTAGE1SKELETONTEXTURE;
                    case 2:
                        return textures.ICESTAGE2SKELETONTEXTURE;
                    case 3:
                        return textures.ICESTAGE3SKELETONTEXTURE;
                    case 4:
                        return textures.ICESTAGE4SKELETONTEXTURE;
                    case 5:
                        return textures.ICESTAGE5SKELETONTEXTURE;
                    default:
                        return textures.ICESTAGE4SKELETONTEXTURE;
                }
            } else {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.ICESTAGE1SLEEPINGTEXTURE;
                    case 2:
                        return textures.ICESTAGE2SLEEPINGTEXTURE;
                    case 3:
                        return textures.ICESTAGE3SLEEPINGTEXTURE;
                    case 4:
                        return textures.ICESTAGE4SLEEPINGTEXTURE;
                    case 5:
                        return textures.ICESTAGE5SLEEPINGTEXTURE;
                    default:
                        return textures.ICESTAGE4SLEEPINGTEXTURE;
                }
            }
        }
        if (dragon.isSleeping() || dragon.isBlinking()) {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.ICESTAGE1SLEEPINGTEXTURE;
                case 2:
                    return textures.ICESTAGE2SLEEPINGTEXTURE;
                case 3:
                    return textures.ICESTAGE3SLEEPINGTEXTURE;
                case 4:
                    return textures.ICESTAGE4SLEEPINGTEXTURE;
                case 5:
                    return textures.ICESTAGE5SLEEPINGTEXTURE;
                default:
                    return textures.ICESTAGE4SLEEPINGTEXTURE;
            }
        } else {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.ICESTAGE1TEXTURE;
                case 2:
                    return textures.ICESTAGE2TEXTURE;
                case 3:
                    return textures.ICESTAGE3TEXTURE;
                case 4:
                    return textures.ICESTAGE4TEXTURE;
                case 5:
                    return textures.ICESTAGE5TEXTURE;
                default:
                    return textures.ICESTAGE4TEXTURE;
            }
        }
    }

    private static ResourceLocation getLightningDragonTextures(EntityDragonBase dragon) {
        EnumDragonTextures textures = getDragonEnum(dragon);
        if (dragon.isModelDead()) {
            if (dragon.getDeathStage() >= (dragon.getAgeInDays() / 5) / 2) {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.LIGHTNINGSTAGE1SKELETONTEXTURE;
                    case 2:
                        return textures.LIGHTNINGSTAGE2SKELETONTEXTURE;
                    case 3:
                        return textures.LIGHTNINGSTAGE3SKELETONTEXTURE;
                    case 4:
                        return textures.LIGHTNINGSTAGE4SKELETONTEXTURE;
                    case 5:
                        return textures.LIGHTNINGSTAGE5SKELETONTEXTURE;
                    default:
                        return textures.LIGHTNINGSTAGE4SKELETONTEXTURE;
                }
            } else {
                switch (dragon.getDragonStage()) {
                    case 1:
                        return textures.LIGHTNINGSTAGE1SLEEPINGTEXTURE;
                    case 2:
                        return textures.LIGHTNINGSTAGE2SLEEPINGTEXTURE;
                    case 3:
                        return textures.LIGHTNINGSTAGE3SLEEPINGTEXTURE;
                    case 4:
                        return textures.LIGHTNINGSTAGE4SLEEPINGTEXTURE;
                    case 5:
                        return textures.LIGHTNINGSTAGE5SLEEPINGTEXTURE;
                    default:
                        return textures.LIGHTNINGSTAGE4SLEEPINGTEXTURE;
                }
            }
        }
        if (dragon.isSleeping() || dragon.isBlinking()) {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.LIGHTNINGSTAGE1SLEEPINGTEXTURE;
                case 2:
                    return textures.LIGHTNINGSTAGE2SLEEPINGTEXTURE;
                case 3:
                    return textures.LIGHTNINGSTAGE3SLEEPINGTEXTURE;
                case 4:
                    return textures.LIGHTNINGSTAGE4SLEEPINGTEXTURE;
                case 5:
                    return textures.LIGHTNINGSTAGE5SLEEPINGTEXTURE;
                default:
                    return textures.LIGHTNINGSTAGE4SLEEPINGTEXTURE;
            }
        } else {
            switch (dragon.getDragonStage()) {
                case 1:
                    return textures.LIGHTNINGSTAGE1TEXTURE;
                case 2:
                    return textures.LIGHTNINGSTAGE2TEXTURE;
                case 3:
                    return textures.LIGHTNINGSTAGE3TEXTURE;
                case 4:
                    return textures.LIGHTNINGSTAGE4TEXTURE;
                case 5:
                    return textures.LIGHTNINGSTAGE5TEXTURE;
                default:
                    return textures.LIGHTNINGSTAGE4TEXTURE;
            }
        }
    }


    public static EnumDragonTextures getDragonEnum(EntityDragonBase dragon) {
        switch (dragon.getVariant()) {
            default:
                return VARIANT1;
            case 1:
                return VARIANT2;
            case 2:
                return VARIANT3;
            case 3:
                return VARIANT4;
        }
    }

    public static ResourceLocation getFireDragonSkullTextures(EntityDragonSkull skull) {
        switch (skull.getDragonStage()) {
            case 1:
                return VARIANT1.FIRESTAGE1SKELETONTEXTURE;
            case 2:
                return VARIANT1.FIRESTAGE2SKELETONTEXTURE;
            case 3:
                return VARIANT1.FIRESTAGE3SKELETONTEXTURE;
            case 4:
                return VARIANT1.FIRESTAGE4SKELETONTEXTURE;
            case 5:
                return VARIANT1.FIRESTAGE5SKELETONTEXTURE;
            default:
                return VARIANT1.FIRESTAGE4SKELETONTEXTURE;
        }
    }

    public static ResourceLocation getIceDragonSkullTextures(EntityDragonSkull skull) {
        switch (skull.getDragonStage()) {
            case 1:
                return VARIANT1.ICESTAGE1SKELETONTEXTURE;
            case 2:
                return VARIANT1.ICESTAGE2SKELETONTEXTURE;
            case 3:
                return VARIANT1.ICESTAGE3SKELETONTEXTURE;
            case 4:
                return VARIANT1.ICESTAGE4SKELETONTEXTURE;
            case 5:
                return VARIANT1.ICESTAGE5SKELETONTEXTURE;
            default:
                return VARIANT1.ICESTAGE4SKELETONTEXTURE;
        }
    }

    public static ResourceLocation getLightningDragonSkullTextures(EntityDragonSkull skull) {
        switch (skull.getDragonStage()) {
            case 1:
                return VARIANT1.LIGHTNINGSTAGE1SKELETONTEXTURE;
            case 2:
                return VARIANT1.LIGHTNINGSTAGE2SKELETONTEXTURE;
            case 3:
                return VARIANT1.LIGHTNINGSTAGE3SKELETONTEXTURE;
            case 4:
                return VARIANT1.LIGHTNINGSTAGE4SKELETONTEXTURE;
            case 5:
                return VARIANT1.LIGHTNINGSTAGE5SKELETONTEXTURE;
            default:
                return VARIANT1.LIGHTNINGSTAGE4SKELETONTEXTURE;
        }
    }

    public enum Armor {
        EMPTY(""),
        ARMORBODY1("armor_body_1"),
        ARMORBODY2("armor_body_2"),
        ARMORBODY3("armor_body_3"),
        ARMORBODY4("armor_body_4"),
        ARMORBODY5("armor_body_5"),
        ARMORBODY6("armor_body_6"),
        ARMORBODY7("armor_body_7"),
        ARMORBODY8("armor_body_8"),
        ARMORHEAD1("armor_head_1"),
        ARMORHEAD2("armor_head_2"),
        ARMORHEAD3("armor_head_3"),
        ARMORHEAD4("armor_head_4"),
        ARMORHEAD5("armor_head_5"),
        ARMORHEAD6("armor_head_6"),
        ARMORHEAD7("armor_head_7"),
        ARMORHEAD8("armor_head_8"),
        ARMORNECK1("armor_neck_1"),
        ARMORNECK2("armor_neck_2"),
        ARMORNECK3("armor_neck_3"),
        ARMORNECK4("armor_neck_4"),
        ARMORNECK5("armor_neck_5"),
        ARMORNECK6("armor_neck_6"),
        ARMORNECK7("armor_neck_7"),
        ARMORNECK8("armor_neck_8"),
        ARMORTAIL1("armor_tail_1"),
        ARMORTAIL2("armor_tail_2"),
        ARMORTAIL3("armor_tail_3"),
        ARMORTAIL4("armor_tail_4"),
        ARMORTAIL5("armor_tail_5"),
        ARMORTAIL6("armor_tail_6"),
        ARMORTAIL7("armor_tail_7"),
        ARMORTAIL8("armor_tail_8");

        public final ResourceLocation FIRETEXTURE;
        public final ResourceLocation ICETEXTURE;
        public final ResourceLocation LIGHTNINGTEXTURE;

        Armor(String resource) {
            if (!resource.isEmpty()) {
                FIRETEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/" + resource + ".png");
                ICETEXTURE = new ResourceLocation("iceandfire:textures/models/icedragon/" + resource + ".png");
                LIGHTNINGTEXTURE = new ResourceLocation("iceandfire:textures/models/lightningdragon/" + resource + ".png");
            } else {
                FIRETEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/empty.png");
                ICETEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/empty.png");
                LIGHTNINGTEXTURE = new ResourceLocation("iceandfire:textures/models/firedragon/empty.png");
            }
        }

        public static Armor getArmorForDragon(EntityDragonBase dragon, EquipmentSlotType slot) {
            int armor = dragon.getArmorOrdinal(dragon.getItemStackFromSlot(slot));
            switch (slot) {
                case CHEST:
                    //neck
                    switch (armor) {
                        default:
                            return EMPTY;
                        case 1:
                            return ARMORNECK1;
                        case 2:
                            return ARMORNECK2;
                        case 3:
                            return ARMORNECK3;
                        case 4:
                            return ARMORNECK4;
                        case 5:
                            return ARMORNECK5;
                        case 6:
                            return ARMORNECK6;
                        case 7:
                            return ARMORNECK7;
                        case 8:
                            return ARMORNECK8;
                    }
                case LEGS:
                    //body
                    switch (armor) {
                        default:
                            return EMPTY;
                        case 1:
                            return ARMORBODY1;
                        case 2:
                            return ARMORBODY2;
                        case 3:
                            return ARMORBODY3;
                        case 4:
                            return ARMORBODY4;
                        case 5:
                            return ARMORBODY5;
                        case 6:
                            return ARMORBODY6;
                        case 7:
                            return ARMORBODY7;
                        case 8:
                            return ARMORBODY8;
                    }
                case FEET:
                    //tail
                    switch (armor) {
                        default:
                            return EMPTY;
                        case 1:
                            return ARMORTAIL1;
                        case 2:
                            return ARMORTAIL2;
                        case 3:
                            return ARMORTAIL3;
                        case 4:
                            return ARMORTAIL4;
                        case 5:
                            return ARMORTAIL5;
                        case 6:
                            return ARMORTAIL6;
                        case 7:
                            return ARMORTAIL7;
                        case 8:
                            return ARMORTAIL8;
                    }
                default:
                    //head
                    switch (armor) {
                        default:
                            return EMPTY;
                        case 1:
                            return ARMORHEAD1;
                        case 2:
                            return ARMORHEAD2;
                        case 3:
                            return ARMORHEAD3;
                        case 4:
                            return ARMORHEAD4;
                        case 5:
                            return ARMORHEAD5;
                        case 6:
                            return ARMORHEAD6;
                        case 7:
                            return ARMORHEAD7;
                        case 8:
                            return ARMORHEAD8;
                    }
            }
        }
    }
}
