package com.github.alexthe666.iceandfire.client.gui.bestiary;

import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class GuiBestiary extends Screen {
    protected static final int X = 390;
    protected static final int Y = 245;
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/gui/bestiary/bestiary.png");
    private static final ResourceLocation DRAWINGS_0 = new ResourceLocation("iceandfire:textures/gui/bestiary/drawings_0.png");
    private static final ResourceLocation DRAWINGS_1 = new ResourceLocation("iceandfire:textures/gui/bestiary/drawings_1.png");
    private static final ResourceLocation DRAWINGS_2 = new ResourceLocation("iceandfire:textures/gui/bestiary/drawings_2.png");
    private static final Map<String, ResourceLocation> PICTURE_LOCATION_CACHE = Maps.newHashMap();
    public List<EnumBestiaryPages> allPageTypes = new ArrayList<EnumBestiaryPages>();
    public EnumBestiaryPages pageType;
    public List<IndexPageButton> indexButtons = new ArrayList<IndexPageButton>();
    public ChangePageButton previousPage;
    public ChangePageButton nextPage;
    public int bookPages;
    public int bookPagesTotal = 1;
    public int indexPages;
    public int indexPagesTotal = 1;
    protected ItemStack book;
    protected boolean index;
    protected FontRenderer font = getFont();

    public GuiBestiary(ItemStack book) {
        super(new TranslationTextComponent("bestiary_gui"));
        this.book = book;
        int indexPageTotal = 0;
        if (!book.isEmpty() && book.getItem() != null && book.getItem() == IafItemRegistry.BESTIARY) {
            if (book.getTag() != null) {
                List<EnumBestiaryPages> pages = EnumBestiaryPages.containedPages(EnumBestiaryPages.toList(book.getTag().getIntArray("Pages")));
                allPageTypes.addAll(pages);
                indexPagesTotal = (int) Math.ceil(pages.size() / 10D);
            }
        }
        index = true;
    }

    private FontRenderer getFont() {
        FontRenderer font;
        if (IafConfig.useVanillaFont || !Minecraft.getInstance().gameSettings.language.equalsIgnoreCase("en_us")) {
            font = Minecraft.getInstance().fontRenderer;
        } else {
            font = (FontRenderer) IceAndFire.PROXY.getFontRenderer();
        }
        return font;
    }

    protected void init() {
        super.init();
        int centerX = (width - X) / 2;
        int centerY = (height - Y) / 2;
        this.addButton(this.previousPage = new ChangePageButton(centerX + 15, centerY + 215, false, bookPages, 0, (p_214132_1_) -> {
            if ((this.index ? this.indexPages > 0 : this.pageType != null)) {
                if (this.index) {
                    this.indexPages--;
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(IafSoundRegistry.BESTIARY_PAGE, 1.0F));
                } else {
                    if (this.bookPages > 0) {
                        this.bookPages--;
                        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(IafSoundRegistry.BESTIARY_PAGE, 1.0F));
                    } else {
                        this.index = true;
                    }
                }
            }
        }));
        this.addButton(this.nextPage = new ChangePageButton(centerX + 357, centerY + 215, true, bookPages, 0, (p_214132_1_) -> {
            if ((this.index ? this.indexPages < this.indexPagesTotal - 1 : this.pageType != null && this.bookPages < this.pageType.pages)) {
                if (this.index) {
                    this.indexPages++;
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(IafSoundRegistry.BESTIARY_PAGE, 1.0F));
                } else {
                    this.bookPages++;
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(IafSoundRegistry.BESTIARY_PAGE, 1.0F));
                }
            }
        }));
        if (!allPageTypes.isEmpty()) {
            for (int i = 0; i < allPageTypes.size(); i++) {
                int xIndex = i % -2;
                int yIndex = i % 10;
                int id = 2 + i;
                IndexPageButton button = new IndexPageButton(id, centerX + 15 + (xIndex * 200), centerY + 10 + (yIndex * 20) - (xIndex == 1 ? 20 : 0), new TranslationTextComponent("bestiary." + EnumBestiaryPages.values()[allPageTypes.get(i).ordinal()].toString().toLowerCase()), (p_214132_1_) -> {
                    if (this.indexButtons.get(id - 2) != null && allPageTypes.get(id - 2) != null) {
                        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(IafSoundRegistry.BESTIARY_PAGE, 1.0F));
                        this.index = false;
                        this.bookPages = 0;
                        this.pageType = allPageTypes.get(id - 2);
                    }
                });
                this.indexButtons.add(button);
                this.addButton(button);
            }
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        for (Widget button : this.buttons) {
            if (button instanceof IndexPageButton) {
                button.active = index;
                button.visible = index;
            }
        }
        for (int i = 0; i < this.indexButtons.size(); i++) {
            this.indexButtons.get(i).active = i < 10 * (this.indexPages + 1) && i >= 10 * (this.indexPages) && this.index;
        }
        this.renderBackground(ms);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        int cornerX = (width - X) / 2;
        int cornerY = (height - Y) / 2;
        blit(ms, cornerX, cornerY, 0, 0, X, Y, 390, 390);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.render(ms, mouseX, mouseY, partialTicks);
        RenderHelper.enableStandardItemLighting();
        ms.push();
        ms.translate(cornerX, cornerY, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        int centerX = (width - X) / 2;
        int centerY = (height - Y) / 2;
        if (!index) {
            drawPerPage(ms, bookPages);
            int pageLeft = bookPages * 2 + 1;
            int pageRight = pageLeft + 1;
            font.drawString(ms, "" + pageLeft, centerX, centerY - (int) (Y * 0.13), 0X303030);
            font.drawString(ms, "" + pageRight, centerX, centerY - (int) (Y * 0.13), 0X303030);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        ms.pop();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    public void drawPerPage(MatrixStack ms, int bookPages) {
        imageFromTxt(ms);
        switch (this.pageType) {
            case INTRODUCTION:
                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(IafBlockRegistry.SAPPHIRE_ORE), 30, 20, 2.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SAPPHIRE_GEM), 40, 60, 2F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                    ms.pop();
                    boolean drawGold = Minecraft.getInstance().player.ticksExisted % 20 < 10;
                    drawItemStack(ms, new ItemStack(drawGold ? Items.GOLD_NUGGET : IafItemRegistry.SILVER_NUGGET), 144, 34, 1.5F);
                    drawItemStack(ms, new ItemStack(drawGold ? Items.GOLD_NUGGET : IafItemRegistry.SILVER_NUGGET), 161, 34, 1.5F);
                    drawItemStack(ms, new ItemStack(drawGold ? IafBlockRegistry.GOLD_PILE : IafBlockRegistry.SILVER_PILE), 151, 7, 2F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 161, 124, 1.5F);
                    drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 161, 107, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MANUSCRIPT), 161, 91, 1.5F);
                    drawItemStack(ms, new ItemStack(IafBlockRegistry.LECTERN), 151, 78, 2F);
                }
                break;
            case FIREDRAGON:
                break;
            case FIREDRAGONEGG:
                break;
            case ICEDRAGON:
                break;
            case ICEDRAGONEGG:
                break;
            case TAMEDDRAGONS:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 90, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.BONE), 145, 124, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.PORKCHOP), 145, 107, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.BONE), 145, 91, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.PORKCHOP), 161, 124, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.BONE), 161, 107, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.PORKCHOP), 161, 91, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.BONE), 177, 124, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.PORKCHOP), 177, 107, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.BONE), 177, 91, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_MEAL), 151, 78, 2F);
                }
                if (bookPages == 1) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_SKULL_FIRE), 161, 17, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.STICK), 161, 32, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_STAFF), 151, 10, 2F);
                }
                if (bookPages == 2) {
                    ms.push();
                    drawItemStack(ms, new ItemStack(IafBlockRegistry.FIRE_LILY), 5, 14, 3.75F);
                    drawItemStack(ms, new ItemStack(IafBlockRegistry.FROST_LILY), 30, 14, 3.75F);
                    ms.pop();
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                    ms.pop();
                    boolean drawFire = Minecraft.getInstance().player.ticksExisted % 40 < 20;
                    drawItemStack(ms, new ItemStack(drawFire ? IafBlockRegistry.FIRE_LILY : IafBlockRegistry.FROST_LILY), 161, 17, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.BOWL), 161, 32, 1.5F);
                    drawItemStack(ms, new ItemStack(drawFire ? Items.BLAZE_ROD : Items.PRISMARINE_CRYSTALS), 177, 17, 1.5F);
                    drawItemStack(ms, new ItemStack(drawFire ? IafItemRegistry.FIRE_STEW : IafItemRegistry.FROST_STEW), 151, 10, 2F);

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 65, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.STICK), 144, 97, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 180, 110, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 180, 92, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 198, 92, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 198, 74, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_HORN), 151, 60, 2F);

                }
                if (bookPages == 3) {
                    int j = 18;
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONARMOR_IRON_0, 1), j += 16, 60, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONARMOR_IRON_1, 1), j += 16, 60, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONARMOR_IRON_2, 1), j += 16, 60, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONARMOR_IRON_3, 1), j += 16, 60, 1.5F);

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 160, 12, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 180, 31, 1.35f);
                    drawItemStack(ms, new ItemStack(Items.IRON_INGOT), 199, 50, 1.35f);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_FLUTE), 151, 18, 2F);
                }
                break;
            case MATERIALS:
                if (bookPages == 0) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONSCALES_RED), 18, 16, 3.75F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BONE), 70, 10, 3.75F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.WITHERBONE), 112, 70, 2.5F);
                    {
                        int j = 18;
                        drawItemStack(ms, new ItemStack(EnumDragonArmor.armor_red.helmet), j += 16, 115, 1.5F);
                        drawItemStack(ms, new ItemStack(EnumDragonArmor.armor_red.chestplate), j += 16, 115, 1.5F);
                        drawItemStack(ms, new ItemStack(EnumDragonArmor.armor_red.leggings), j += 16, 115, 1.5F);
                        drawItemStack(ms, new ItemStack(EnumDragonArmor.armor_red.boots), j += 16, 115, 1.5F);
                    }
                }
                if (bookPages == 1) {
                    {
                        int j = 1;
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_SWORD), j += 16, 14, 1.5F);
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_PICKAXE), j += 16, 14, 1.5F);
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_AXE), j += 16, 14, 1.5F);
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_SHOVEL), j += 16, 14, 1.5F);
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_HOE), j += 16, 14, 1.5F);
                        drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_BOW), j += 16, 14, 1.5F);
                    }
                    drawItemStack(ms, new ItemStack(IafItemRegistry.FIRE_DRAGON_FLESH), 18, 24, 3.75F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.FIRE_DRAGON_HEART), 70, 14, 3.75F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGON_SKULL_FIRE), 70, 39, 3.75F);
                }
                if (bookPages == 2) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.FIRE_DRAGON_BLOOD), 18, 24, 3.75F);
                }
                break;
            case ALCHEMY:
                if (bookPages == 0) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.FIRE_DRAGON_BLOOD), 10, 24, 3.75F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.ICE_DRAGON_BLOOD), 26, 24, 3.75F);
                    boolean drawFire = Minecraft.getInstance().player.ticksExisted % 40 < 20;
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DRAGONBONE_SWORD), 161, 17, 1.5F);
                    drawItemStack(ms, new ItemStack(drawFire ? IafItemRegistry.FIRE_DRAGON_BLOOD : IafItemRegistry.ICE_DRAGON_BLOOD), 161, 32, 1.5F);
                    drawItemStack(ms, new ItemStack(drawFire ? IafItemRegistry.DRAGONBONE_SWORD_FIRE : IafItemRegistry.DRAGONBONE_SWORD_ICE), 151, 10, 2F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 0, 389, 1, 50, 50, 512F);
                    ms.pop();
                }
                break;
            case HIPPOGRYPH:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(0.8F, 0.8F, 1F);
                    drawImage(ms, DRAWINGS_0, 29, 150, 303, 151, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 91, 150, 364, 151, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 151, 150, 425, 151, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 29, 190, 303, 187, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 91, 190, 364, 187, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 151, 190, 425, 187, 61, 36, 512F);
                    drawImage(ms, DRAWINGS_0, 90, 230, 425, 223, 61, 35, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.RABBIT_FOOT), 70, 20, 3.75F);
                }

                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(Items.STICK), 16, 24, 3.75F);

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 10, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.FEATHER), 160, 31, 1.35F);
                    int drawType = Minecraft.getInstance().player.ticksExisted % 60 > 40 ? 2 : Minecraft.getInstance().player.ticksExisted % 60 > 20 ? 1 : 0;
                    drawItemStack(ms, new ItemStack(drawType == 0 ? Items.IRON_HORSE_ARMOR : drawType == 1 ? Items.GOLDEN_HORSE_ARMOR : Items.DIAMOND_HORSE_ARMOR), 180, 31, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.FEATHER), 199, 31, 1.35F);
                    drawItemStack(ms, new ItemStack(drawType == 0 ? IafItemRegistry.IRON_HIPPOGRYPH_ARMOR : drawType == 1 ? IafItemRegistry.GOLD_HIPPOGRYPH_ARMOR : IafItemRegistry.DIAMOND_HIPPOGRYPH_ARMOR), 151, 18, 2F);
                    drawItemStack(ms, new ItemStack(Items.RABBIT_STEW), 70, 23, 3.75F);
                }
                break;
            case GORGON:
                if (bookPages == 0) {

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 10, 89, 473, 117, 19, 34, 512F);
                    drawImage(ms, DRAWINGS_0, 50, 78, 399, 106, 28, 45, 512F);
                    drawImage(ms, DRAWINGS_0, 100, 89, 455, 117, 18, 34, 512F);
                    ms.pop();

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 70, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.STRING), 160, 97, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.LEATHER), 180, 97, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.STRING), 199, 97, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.BLINDFOLD), 171, 65, 2F);
                }
                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.GORGON_HEAD), 16, 12, 3.75F);
                    ms.push();
                    ms.scale(1.7F, 1.7F, 1F);
                    drawImage(ms, DRAWINGS_0, 37, 95, 473, 117, 19, 34, 512F);
                    drawImage(ms, DRAWINGS_0, 60, 95, 455, 117, 18, 34, 512F);
                    ms.pop();

                }
                break;
            case PIXIE:
                if (bookPages == 0) {
                    ms.push();
                    drawImage(ms, DRAWINGS_0, 20, 60, 371, 258, 47, 35, 512F);
                    drawImage(ms, DRAWINGS_0, 42, 95, 416, 258, 45, 35, 512F);
                    drawImage(ms, DRAWINGS_0, 67, 60, 462, 258, 47, 35, 512F);
                    drawImage(ms, DRAWINGS_0, 88, 95, 370, 293, 47, 35, 512F);
                    drawImage(ms, DRAWINGS_0, 110, 60, 416, 293, 47, 35, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(IafItemRegistry.PIXIE_DUST), 70, 10, 3.75F);
                    ms.push();
                    ms.scale(0.9F, 0.9F, 1F);
                    ms.translate(20, 24, 0);
                    ms.push();
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 150, 100, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 113, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 113, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.OAK_PLANKS), 180, 113, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 131, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 131, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 180, 150, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 160, 150, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.GLASS), 199, 150, 1.35F);
                    drawItemStack(ms, new ItemStack(IafBlockRegistry.JAR_EMPTY), 171, 85, 2F);
                    ms.pop();
                    ms.pop();

                }
                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.AMBROSIA), 14, 22, 3.75F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 100, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(IafItemRegistry.PIXIE_DUST), 180, 131, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.BOWL), 180, 150, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.AMBROSIA), 171, 85, 2F);
                }
                break;
            case CYCLOPS:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1.5F);
                    drawImage(ms, DRAWINGS_0, 185, 8, 399, 328, 24, 63, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1.5F);
                    drawImage(ms, DRAWINGS_0, 50, 35, 423, 328, 24, 63, 512F);
                    //drawImage(ms, DRAWINGS_0, 68, 60, 447, 328, 24, 63, 512F);
                    ms.pop();

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 50, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.LEATHER_HELMET), 180, 76, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 76, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 76, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 57, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 180, 57, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 57, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHEEP_HELMET), 165, 45, 2F);

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 144, 95, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.LEATHER_CHESTPLATE), 180, 126, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 126, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 126, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 107, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 107, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 160, 145, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 180, 145, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 199, 145, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHEEP_CHESTPLATE), 165, 95, 2F);
                }
                if (bookPages == 2) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1.5F);
                    drawImage(ms, DRAWINGS_0, 185, 30, 447, 328, 24, 63, 512F);
                    ms.pop();

                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 13, 24, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.LEATHER_LEGGINGS), 34, 46, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 46, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 46, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 27, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 34, 27, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 27, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 65, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 65, 1.35F);

                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHEEP_LEGGINGS), 64, 27, 2F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 13, 84, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.LEATHER_BOOTS), 34, 94, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 113, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 113, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 14, 94, 1.35F);
                    drawItemStack(ms, new ItemStack(Blocks.WHITE_WOOL), 53, 94, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHEEP_BOOTS), 64, 73, 2F);
                }
                break;
            case SIREN:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.25F, 1.25F, 1.25F);
                    drawImage(ms, DRAWINGS_1, 190, 25, 0, 0, 25, 42, 512F);
                    drawImage(ms, DRAWINGS_1, 220, 15, 25, 0, 25, 42, 512F);
                    drawImage(ms, DRAWINGS_1, 255, 25, 50, 0, 25, 42, 512F);

                    drawImage(ms, DRAWINGS_1, 190, 135, 0, 42, 26, 28, 512F);
                    drawImage(ms, DRAWINGS_1, 220, 125, 26, 42, 26, 28, 512F);
                    drawImage(ms, DRAWINGS_1, 255, 135, 52, 42, 26, 28, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.EARPLUGS), 18, 40, 3.75F);


                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 160, 0, 389, 1, 50, 50, 512F);
                    ms.pop();


                    drawItemStack(ms, new ItemStack(Item.getItemFromBlock(Blocks.OAK_BUTTON)), 180, 20, 1.35F);
                    drawItemStack(ms, new ItemStack(Item.getItemFromBlock(Blocks.OAK_BUTTON)), 215, 20, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.EARPLUGS), 170, 10, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHINY_SCALES), 123, 75, 2.25F);
                }
                break;
            case HIPPOCAMPUS:
                if (bookPages == 0) {
                    ms.push();
                    drawImage(ms, DRAWINGS_1, 210, 25, 0, 70, 57, 49, 512F);
                    drawImage(ms, DRAWINGS_1, 265, 25, 57, 70, 57, 49, 512F);
                    drawImage(ms, DRAWINGS_1, 320, 25, 0, 119, 57, 49, 512F);
                    drawImage(ms, DRAWINGS_1, 210, 80, 57, 119, 57, 49, 512F);
                    drawImage(ms, DRAWINGS_1, 265, 80, 0, 168, 57, 49, 512F);
                    drawImage(ms, DRAWINGS_1, 320, 80, 57, 168, 57, 49, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    drawItemStack(ms, new ItemStack(Items.KELP), 37, 33, 2.25F);
                    drawItemStack(ms, new ItemStack(Items.PRISMARINE_CRYSTALS), 37, 73, 2.25F);
                }
                if (bookPages == 2) {
                    drawItemStack(ms, new ItemStack(Items.STICK), 35, 25, 2.25F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SHINY_SCALES), 35, 75, 2.25F);
                }
                break;
            case DEATHWORM:
                if (bookPages == 0) {
                    ms.push();
                    drawImage(ms, DRAWINGS_1, 230, 25, 0, 217, 133, 16, 512F);
                    drawImage(ms, DRAWINGS_1, 230, 50, 0, 233, 133, 16, 512F);
                    drawImage(ms, DRAWINGS_1, 230, 75, 0, 249, 133, 16, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    ms.push();
                    drawImage(ms, DRAWINGS_1, 25, 95, 0, 265, 148, 44, 512F);
                    drawImage(ms, DRAWINGS_1, 250, 5, 0, 309, 81, 162, 512F);
                    ms.pop();
                }
                if (bookPages == 2) {
                    int drawType = Minecraft.getInstance().player.ticksExisted % 60 > 40 ? 2 : Minecraft.getInstance().player.ticksExisted % 60 > 20 ? 1 : 0;
                    Item chitin = IafItemRegistry.DEATH_WORM_CHITIN_YELLOW;
                    if (drawType == 2) {
                        chitin = IafItemRegistry.DEATH_WORM_CHITIN_RED;
                    }
                    if (drawType == 1) {
                        chitin = IafItemRegistry.DEATH_WORM_CHITIN_WHITE;
                    }
                    drawItemStack(ms, new ItemStack(chitin, 1), 17, 30, 3.75F);
                    drawItemStack(ms, new ItemStack(drawType == 2 ? IafItemRegistry.DEATHWORM_RED_HELMET : drawType == 1 ? IafItemRegistry.DEATHWORM_WHITE_HELMET : IafItemRegistry.DEATHWORM_YELLOW_HELMET), 92, 8, 2.25F);
                    drawItemStack(ms, new ItemStack(drawType == 2 ? IafItemRegistry.DEATHWORM_RED_CHESTPLATE : drawType == 1 ? IafItemRegistry.DEATHWORM_WHITE_CHESTPLATE : IafItemRegistry.DEATHWORM_YELLOW_CHESTPLATE), 112, 8, 2.25F);
                    drawItemStack(ms, new ItemStack(drawType == 2 ? IafItemRegistry.DEATHWORM_RED_LEGGINGS : drawType == 1 ? IafItemRegistry.DEATHWORM_WHITE_LEGGINGS : IafItemRegistry.DEATHWORM_YELLOW_LEGGINGS), 132, 8, 2.25F);
                    drawItemStack(ms, new ItemStack(drawType == 2 ? IafItemRegistry.DEATHWORM_RED_BOOTS : drawType == 1 ? IafItemRegistry.DEATHWORM_WHITE_BOOTS : IafItemRegistry.DEATHWORM_YELLOW_BOOTS), 152, 8, 2.25F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DEATHWORM_EGG), 125, 42, 2.25F);
                }
                if (bookPages == 3) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.DEATHWORM_EGG_GIGANTIC, 1), 125, 4, 2.25F);
                    drawItemStack(ms, new ItemStack(Items.FISHING_ROD), 115, 55, 2.25F);
                    drawItemStack(ms, new ItemStack(Items.FISHING_ROD), 135, 55, 2.25F);
                }
                break;
            case COCKATRICE:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_1, 155, 10, 114, 0, 88, 36, 512F);
                    drawImage(ms, DRAWINGS_1, 155, 45, 114, 36, 88, 36, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.STRING), 20, 30, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.LEATHER), 40, 30, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.STRING), 59, 30, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.BLINDFOLD), 60, 18, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.WITHERBONE), 30, 58, 2.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.ROTTEN_EGG), 109, 18, 2.5F);
                }
                break;
            case STYMPHALIANBIRD:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_1, 34, 46, 114, 72, 59, 37, 512F);
                    drawImage(ms, DRAWINGS_1, 155, 35, 114, 109, 67, 35, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER), 109, 60, 2.5F);
                }
                if (bookPages == 1) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 18, 10, 389, 1, 50, 50, 512F);
                    ms.pop();

                    drawItemStack(ms, new ItemStack(Items.FLINT), 40, 13, 1.35F);
                    drawItemStack(ms, new ItemStack(Items.STICK), 40, 30, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER), 40, 49, 1.35F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.STYMPHALIAN_ARROW), 60, 18, 2F);

                }
                break;
            case TROLL:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_1, 15, 60, 156, 211, 25, 58, 512F);
                    drawImage(ms, DRAWINGS_1, 50, 55, 181, 211, 25, 58, 512F);
                    drawImage(ms, DRAWINGS_1, 85, 60, 206, 211, 25, 58, 512F);
                    drawImage(ms, DRAWINGS_1, 155, 22, 114, 145, 24, 66, 512F);
                    drawImage(ms, DRAWINGS_1, 190, 19, 188, 142, 47, 69, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    int i = (Minecraft.getInstance().player.ticksExisted % (EnumTroll.Weapon.values().length * 20)) / 20;
                    drawItemStack(ms, new ItemStack(EnumTroll.Weapon.values()[i].item), 30, 7, 2.5F);
                    int j = (Minecraft.getInstance().player.ticksExisted % (EnumTroll.values().length * 20)) / 20;
                    drawItemStack(ms, new ItemStack(EnumTroll.values()[j].leather), 100, 30, 2.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.TROLL_TUSK), 120, 30, 2.5F);
                }
                if (bookPages == 2) {
                    int j = (Minecraft.getInstance().player.ticksExisted % (EnumTroll.values().length * 20)) / 20;
                    drawItemStack(ms, new ItemStack(EnumTroll.values()[j].helmet), 27, 15, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumTroll.values()[j].chestplate), 47, 15, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumTroll.values()[j].leggings), 67, 15, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumTroll.values()[j].boots), 87, 15, 1.5F);
                }
                break;
            case MYRMEX:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(1.51F, 1.51F, 1F);
                    drawImage(ms, DRAWINGS_1, 137, 10, 202, 16, 57, 21, 512F);
                    drawImage(ms, DRAWINGS_1, 195, 10, 278, 16, 57, 21, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    ms.push();
                    ms.scale(1.51F, 1.51F, 1F);
                    drawImage(ms, DRAWINGS_1, 7, 17, 202, 37, 59, 21, 512F);
                    drawImage(ms, DRAWINGS_1, 65, 17, 278, 37, 59, 21, 512F);
                    drawImage(ms, DRAWINGS_1, 7, 77, 202, 58, 59, 21, 512F);
                    drawImage(ms, DRAWINGS_1, 65, 77, 278, 58, 59, 21, 512F);
                    drawImage(ms, DRAWINGS_1, 145, 20, 278, 103, 43, 45, 512F);
                    drawImage(ms, DRAWINGS_1, 195, 20, 321, 103, 43, 45, 512F);
                    ms.pop();
                }
                if (bookPages == 2) {
                    ms.push();
                    ms.scale(1.51F, 1.51F, 1F);
                    drawImage(ms, DRAWINGS_1, 25, 13, 202, 79, 76, 24, 512F);
                    drawImage(ms, DRAWINGS_1, 25, 40, 278, 79, 76, 24, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_DESERT_CHITIN), 125, 43, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_CHITIN), 155, 43, 2F);
                    int i = 133;
                    boolean jungle = Minecraft.getInstance().player.ticksExisted % 60 > 30;
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_SHOVEL : IafItemRegistry.MYRMEX_DESERT_SHOVEL), i += 16, 100, 1.51F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_PICKAXE : IafItemRegistry.MYRMEX_DESERT_PICKAXE), i += 16, 100, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_AXE : IafItemRegistry.MYRMEX_DESERT_AXE), i += 16, 100, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_SWORD : IafItemRegistry.MYRMEX_DESERT_SWORD), i += 16, 100, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_SWORD_VENOM : IafItemRegistry.MYRMEX_DESERT_SWORD_VENOM), i += 16, 100, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_HOE : IafItemRegistry.MYRMEX_DESERT_HOE), i += 16, 100, 1.5F);
                    int j = 148;
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_HELMET : IafItemRegistry.MYRMEX_DESERT_HELMET), j += 16, 115, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_CHESTPLATE : IafItemRegistry.MYRMEX_DESERT_CHESTPLATE), j += 16, 115, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_LEGGINGS : IafItemRegistry.MYRMEX_DESERT_LEGGINGS), j += 16, 115, 1.5F);
                    drawItemStack(ms, new ItemStack(jungle ? IafItemRegistry.MYRMEX_JUNGLE_BOOTS : IafItemRegistry.MYRMEX_DESERT_BOOTS), j += 16, 115, 1.5F);
                }
                if (bookPages == 3) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_STINGER), 35, 22, 2.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_DESERT_RESIN), 25, 64, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_RESIN), 55, 64, 2F);
                }
                if (bookPages == 4) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_DESERT_STAFF), 25, 73, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_STAFF), 55, 73, 2F);

                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_DESERT_EGG), 125, 90, 2F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_EGG), 155, 90, 2F);
                }
                break;
            case AMPHITHERE:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(0.75F, 0.75F, 0.75F);
                    drawImage(ms, DRAWINGS_1, 70, 97, 257, 163, 136, 93, 512F);
                    drawImage(ms, DRAWINGS_1, 270, 50, 148, 267, 120, 51, 512F);
                    drawImage(ms, DRAWINGS_1, 380, 50, 148, 318, 120, 51, 512F);
                    drawImage(ms, DRAWINGS_1, 270, 100, 148, 369, 120, 51, 512F);
                    drawImage(ms, DRAWINGS_1, 380, 100, 148, 420, 120, 51, 512F);
                    drawImage(ms, DRAWINGS_1, 330, 150, 268, 267, 120, 51, 512F);
                    ms.pop();
                }
                if (bookPages == 2) {
                    drawItemStack(ms, new ItemStack(IafItemRegistry.AMPHITHERE_FEATHER), 30, 20, 2.5F);
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 19, 71, 389, 1, 50, 50, 512F);
                    ms.pop();
                    drawItemStack(ms, new ItemStack(Items.FLINT), 36, 73, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.STICK), 36, 89, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.AMPHITHERE_FEATHER), 36, 106, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.AMPHITHERE_ARROW), 60, 65, 2F);
                }
                break;
            case SEASERPENT:
                if (bookPages == 0) {
                    ms.push();
                    ms.scale(0.75F, 0.75F, 0.75F);
                    drawImage(ms, DRAWINGS_1, 290, 5, 422, 0, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 380, 5, 422, 64, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 290, 70, 422, 128, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 380, 70, 422, 192, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 290, 140, 422, 256, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 380, 140, 422, 320, 90, 64, 512F);
                    drawImage(ms, DRAWINGS_1, 345, 210, 422, 384, 90, 64, 512F);
                    ms.pop();
                }
                if (bookPages == 1) {
                    drawImage(ms, DRAWINGS_1, 60, 90, 337, 0, 70, 83, 512F);
                    int j = (Minecraft.getInstance().player.ticksExisted % (EnumSeaSerpent.values().length * 20)) / 20;
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].scale), 130, 40, 2.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SERPENT_FANG), 90, 40, 2.5F);
                }
                if (bookPages == 2) {
                    ms.push();
                    ms.scale(1.5F, 1.5F, 1F);
                    drawImage(ms, DRAWINGS_0, 19, 31, 389, 1, 50, 50, 512F);
                    ms.pop();
                    int j = (Minecraft.getInstance().player.ticksExisted % (EnumSeaSerpent.values().length * 20)) / 20;
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SERPENT_FANG), 36, 32, 1.5F);
                    drawItemStack(ms, new ItemStack(Items.STICK), 36, 48, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].scale), 36, 66, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].helmet), 34, 125, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].chestplate), 50, 125, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].leggings), 66, 125, 1.5F);
                    drawItemStack(ms, new ItemStack(EnumSeaSerpent.values()[j].boots), 82, 125, 1.5F);
                    drawItemStack(ms, new ItemStack(IafItemRegistry.SEA_SERPENT_ARROW), 60, 33, 2F);
                }
                break;
        }
        writeFromTxt(ms);
    }

    public void imageFromTxt(MatrixStack ms) {
        String fileName = this.pageType.toString().toLowerCase(Locale.ROOT) + "_" + this.bookPages + ".txt";
        ResourceLocation fileLoc = new ResourceLocation("iceandfire:lang/bestiary/" + Minecraft.getInstance().gameSettings.language + "_0/" + fileName);
        ResourceLocation backupLoc = new ResourceLocation("iceandfire:lang/bestiary/en_us_0/" + fileName);
        IResource resource = null;

        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(fileLoc);
        } catch (IOException e) {
            try {
                resource = Minecraft.getInstance().getResourceManager().getResource(backupLoc);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            Iterator iterator = IOUtils.readLines(resource.getInputStream(), "UTF-8").iterator();
            String line = null;
            int linenumber = 0;
            int zLevelAdd = 0;
            while (iterator.hasNext()) {
                line = ((String) iterator.next()).trim();
                if (line.contains("<") || line.contains(">")) {
                    if (line.contains("<image>")) {
                        line = line.substring(8, line.length() - 1);
                        String[] split = line.split(" ");
                        String texture = "iceandfire:textures/gui/bestiary/" + split[0];
                        ResourceLocation resourcelocation = PICTURE_LOCATION_CACHE.get(texture);
                        if (resourcelocation == null) {
                            resourcelocation = new ResourceLocation(texture);
                            PICTURE_LOCATION_CACHE.put(texture, resourcelocation);
                        }
                        ms.push();
                        drawImage(ms, resourcelocation, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Float.parseFloat(split[7]) * 512F);
                        ms.pop();
                    }
                }
                if (line.contains("<item>")) {
                    line = line.substring(7, line.length() - 1);
                    String[] split = line.split(" ");
                    RenderHelper.enableStandardItemLighting();
                    drawItemStack(ms, new ItemStack(getItemByRegistryName(split[0]), 1), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]) * 2F);
                }
                if (line.contains("<block>")) {
                    zLevelAdd += 1;
                    line = line.substring(8, line.length() - 1);
                    String[] split = line.split(" ");
                    RenderHelper.enableStandardItemLighting();
                    drawBlockStack(ms, new ItemStack(getItemByRegistryName(split[0]), 1), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]) * 2F, zLevelAdd);
                }
                if (line.contains("<recipe>")) {
                    line = line.substring(9, line.length() - 1);
                    String[] split = line.split(" ");
                    RenderHelper.enableStandardItemLighting();
                    float scale = Float.parseFloat(split[split.length - 1]);
                    int x = Integer.parseInt(split[split.length - 3]);
                    int y = Integer.parseInt(split[split.length - 2]);
                    ItemStack result = new ItemStack(getItemByRegistryName(split[0]), 1);
                    ItemStack[] ingredients = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
                    int j = 8;
                    for (int i = split.length - 5; i >= 2; i -= 2) {
                        ingredients[j] = new ItemStack(getItemByRegistryName(split[i]), 1);
                        j--;
                    }
                    RenderHelper.enableStandardItemLighting();
                    ms.push();
                    drawRecipe(ms, result, ingredients, x, y, scale);
                    ms.pop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Item getItemByRegistryName(String registryName) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
    }

    private void drawRecipe(MatrixStack ms, ItemStack result, ItemStack[] ingredients, int x, int y, float scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);
        RenderSystem.scalef(scale, scale, 0);
        drawItemStack(ms, result, 62, 17, 2F);
        for (int i = 0; i < 9; i++) {
            drawItemStack(ms, ingredients[i], ((i % 3) * 22 + 30), ((i / 3) * 22 + 10), 1.25F);
        }
        RenderSystem.popMatrix();
        ms.push();
        ms.translate(x, y, 0);
        ms.scale(scale, scale, 0);
        ms.translate(37F, 13, 1F);
        ms.scale(1.5F, 1.5F, 1F);
        drawImage(ms, DRAWINGS_0, 0, 0, 389, 1, 50, 50, 512F);
        ms.pop();

    }

    public void writeFromTxt(MatrixStack ms) {
        String fileName = this.pageType.toString().toLowerCase(Locale.ROOT) + "_" + this.bookPages + ".txt";
        ResourceLocation fileLoc = new ResourceLocation("iceandfire:lang/bestiary/" + Minecraft.getInstance().gameSettings.language + "_0/" + fileName);
        ResourceLocation backupLoc = new ResourceLocation("iceandfire:lang/bestiary/en_us_0/" + fileName);
        IResource resource = null;

        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(fileLoc);
        } catch (IOException e) {
            try {
                resource = Minecraft.getInstance().getResourceManager().getResource(backupLoc);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            Iterator iterator = IOUtils.readLines(resource.getInputStream(), "UTF-8").iterator();
            String line = null;
            int linenumber = 0;
            while (iterator.hasNext()) {
                line = ((String) iterator.next()).trim();
                if (line.contains("<") || line.contains(">")) {
                    continue;
                }
                ms.push();
                if (usingVanillaFont()) {
                    ms.scale(0.945F, 0.945F, 0.945F);
                    ms.translate(0, 5.5F, 0);
                }
                if (linenumber <= 19) {
                    font.drawString(ms, line, 15, 20 + linenumber * 10, 0X303030);
                } else {
                    font.drawString(ms, line, 220, (linenumber - 19) * 10, 0X303030);
                }
                linenumber++;
                ms.pop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ms.push();
        String s = StatCollector.translateToLocal("bestiary." + this.pageType.toString().toLowerCase(Locale.ROOT));
        float scale = font.getStringWidth(s) <= 100 ? 2 : font.getStringWidth(s) * 0.0125F;
        ms.scale(scale, scale, scale);
        font.drawString(ms, s, 10, 2, 0X7A756A);
        ms.pop();
    }

    private boolean usingVanillaFont() {
        return font == Minecraft.getInstance().fontRenderer;
    }

    public void drawImage(MatrixStack ms, ResourceLocation texture, int x, int y, int u, int v, int width, int height, float scale) {
        ms.push();
        this.getMinecraft().getTextureManager().bindTexture(texture);
        ms.scale(scale / 512F, scale / 512F, scale / 512F);
        blit(ms, x, y, u, v, width, height, 512, 512);
        ms.pop();
    }

    private void drawItemStack(MatrixStack ms, ItemStack stack, int x, int y, float scale) {
        int cornerX = (width - X) / 2;
        int cornerY = (height - Y) / 2;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(cornerX, cornerY, 32.0F);
        float zLevel = 200.0F;
        this.itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (!stack.isEmpty()) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = getFont();
        RenderSystem.scalef(scale, scale, scale);
        this.itemRenderer.zLevel = -100;
        RenderSystem.depthMask(true);
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        zLevel = 0.0F;
        this.itemRenderer.zLevel = 0.0F;
        RenderSystem.popMatrix();
    }

    protected void renderItemModelIntoGUI(MatrixStack ms, ItemStack stack, int x, int y, IBakedModel bakedmodel, float scale) {
        int i = (this.width - X) / 2;
        int j = (this.height - Y) / 2;
        RenderSystem.pushMatrix();
        this.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.getMinecraft().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.scalef(16.0F * scale, 16.0F * scale, 16.0F * scale);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y , 100.0F + itemRenderer.zLevel);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !bakedmodel.isSideLit();
        if (flag) {
            RenderHelper.setupGuiFlatDiffuseLighting();
        }

        this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        irendertypebuffer$impl.finish();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }


    private void drawBlockStack(MatrixStack ms, ItemStack stack, int x, int y, float scale, int zScale) {
        int cornerX = (width - X) / 2;
        int cornerY = (height - Y) / 2;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(cornerX, cornerY, 32.0F);
        float zLevel = 200.0F;
        this.itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (!stack.isEmpty()) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = getFont();
        GL11.glScalef(scale, scale, scale);
        this.itemRenderer.zLevel = -100 + zScale * 10;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        zLevel = 0.0F;
        this.itemRenderer.zLevel = 0.0F;
        RenderSystem.popMatrix();

    }
}
