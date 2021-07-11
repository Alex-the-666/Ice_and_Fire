package com.github.alexthe666.iceandfire.client.gui;

import java.util.List;

import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.gui.bestiary.ChangePageButton;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageGetMyrmexHive;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiMyrmexStaff extends Screen {
    private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_jungle.png");
    private static final ResourceLocation DESERT_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_desert.png");
    private static final WorldGenMyrmexHive.RoomType[] ROOMS = {WorldGenMyrmexHive.RoomType.FOOD, WorldGenMyrmexHive.RoomType.NURSERY, WorldGenMyrmexHive.RoomType.EMPTY};
    private static final int ROOMS_PER_PAGE = 5;
    private final List<Room> allRoomPos = Lists.newArrayList();
    private final List<MyrmexDeleteButton> allRoomButtonPos = Lists.newArrayList();
    public ChangePageButton previousPage;
    public ChangePageButton nextPage;
    int ticksSinceDeleted = 0;
    int currentPage = 0;
    private ItemStack staff;
    private boolean jungle;
    private int hiveCount;

    public GuiMyrmexStaff(ItemStack staff) {
        super(new TranslationTextComponent("myrmex_staff_screen"));
        this.staff = staff;
        this.jungle = staff.getItem() == IafItemRegistry.MYRMEX_JUNGLE_STAFF;
        init();
    }

    protected void init() {
        super.init();
        this.buttons.clear();
        this.allRoomButtonPos.clear();
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        int x_translate = 193;
        int y_translate = 37;
        if (ClientProxy.getReferedClientHive() == null) {
            return;
        }
        populateRoomMap();
        this.addButton(new Button(i + 124, j + 15, 120, 20, ClientProxy.getReferedClientHive().reproduces ? new TranslationTextComponent("myrmex.message.disablebreeding") : new TranslationTextComponent("myrmex.message.enablebreeding"), (p_214132_1_) -> {
            boolean opposite = !ClientProxy.getReferedClientHive().reproduces;
            ClientProxy.getReferedClientHive().reproduces = opposite;
        }));
        this.addButton(this.previousPage = new ChangePageButton(i + 5, j + 150, false, 0, this.jungle ? 2 : 1, (p_214132_1_) -> {
            if (this.currentPage > 0) {
                this.currentPage--;
            }
        }));
        this.addButton(this.nextPage = new ChangePageButton(i + 225, j + 150, true, 0, this.jungle ? 2 : 1, (p_214132_1_) -> {
            if (this.currentPage < this.allRoomButtonPos.size() / ROOMS_PER_PAGE) {
                this.currentPage++;
            }
        }));
        int totalRooms = allRoomPos.size();
        for (int rooms = 0; rooms < allRoomPos.size(); rooms++) {
            int yIndex = rooms % ROOMS_PER_PAGE;
            BlockPos pos = allRoomPos.get(rooms).pos;
            //IndexPageButton button = new IndexPageButton(2 + i, centerX + 15 + (xIndex * 200), centerY + 10 + (yIndex * 20) - (xIndex == 1 ? 20 : 0), StatCollector.translateToLocal("bestiary." + EnumBestiaryPages.values()[allPageTypes.get(i).ordinal()].toString().toLowerCase()));
            MyrmexDeleteButton button = new MyrmexDeleteButton(i + x_translate, j + y_translate + (yIndex) * 22, pos, new TranslationTextComponent("myrmex.message.delete"), (p_214132_1_) -> {
                if (ticksSinceDeleted <= 0) {
                    ClientProxy.getReferedClientHive().removeRoom(pos);
                    ticksSinceDeleted = 5;
                }
            });
            button.visible = rooms < ROOMS_PER_PAGE * (this.currentPage + 1) && rooms >= ROOMS_PER_PAGE * this.currentPage;
            this.addButton(button);
            this.allRoomButtonPos.add(button);
        }
        if (totalRooms <= ROOMS_PER_PAGE * (this.currentPage) && this.currentPage > 0) {
            this.currentPage--;
        }
    }

    private void populateRoomMap() {
        allRoomPos.clear();

        for (WorldGenMyrmexHive.RoomType type : ROOMS) {
            List<BlockPos> roomPos = ClientProxy.getReferedClientHive().getRooms(type);
            for (BlockPos pos : roomPos) {
                String name = type == WorldGenMyrmexHive.RoomType.FOOD ? "food" : type == WorldGenMyrmexHive.RoomType.NURSERY ? "nursery" : "misc";
                allRoomPos.add(new Room(pos, name));
                //this.buttonList.add(new MyrmexDeleteButton(buttons, i + x_translate, j + y_translate + (-1 + buttons) * 22, pos, I18n.format("myrmex.message.delete")));
            }
        }
        for (BlockPos pos : ClientProxy.getReferedClientHive().getEntrances().keySet()) {
            allRoomPos.add(new Room(pos, "enterance_surface"));
            //this.buttonList.add(new MyrmexDeleteButton(buttons, i + x_translate, j + y_translate + (-1 + buttons) * 22, pos, I18n.format("myrmex.message.delete")));
        }
        for (BlockPos pos : ClientProxy.getReferedClientHive().getEntranceBottoms().keySet()) {
            allRoomPos.add(new Room(pos, "enterance_bottom"));
            //this.buttonList.add(new MyrmexDeleteButton(buttons, i + x_translate, j + y_translate + (-1 + buttons) * 22, pos, I18n.format("myrmex.message.delete")));
        }
    }

    public void renderBackground(MatrixStack ms) {
        super.renderBackground(ms);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(jungle ? JUNGLE_TEXTURE : DESERT_TEXTURE);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.blit(ms, i, j, 0, 0, 248, 166);
    }

    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        init();
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        super.render(ms, mouseX, mouseY, partialTicks);
        int color = this.jungle ? 0X35EA15 : 0XFFBF00;
        if (ticksSinceDeleted > 0) {
            ticksSinceDeleted--;
        }
        hiveCount = 0;
        for (int rooms = 0; rooms < this.allRoomButtonPos.size(); rooms++) {
            if (rooms < ROOMS_PER_PAGE * (this.currentPage + 1) && rooms >= ROOMS_PER_PAGE * this.currentPage) {
                this.drawRoomInfo(ms, this.allRoomPos.get(rooms).string, this.allRoomPos.get(rooms).pos, i, j, color);
            }
        }
        if (ClientProxy.getReferedClientHive() != null) {
            if (!ClientProxy.getReferedClientHive().colonyName.isEmpty()) {
                String title = I18n.format("myrmex.message.colony_named", ClientProxy.getReferedClientHive().colonyName);
                this.getMinecraft().fontRenderer.drawString(ms, title, i + 40 - title.length() / 2, j - 3, color);
            } else {
                this.getMinecraft().fontRenderer.drawString(ms, I18n.format("myrmex.message.colony"), i + 80, j - 3, color);
            }
            int opinion = ClientProxy.getReferedClientHive().getPlayerReputation(Minecraft.getInstance().player.getUniqueID());
            this.getMinecraft().fontRenderer.drawString(ms, I18n.format("myrmex.message.hive_opinion", opinion), i, j + 12, color);
            this.getMinecraft().fontRenderer.drawString(ms, I18n.format("myrmex.message.rooms"), i, j + 25, color);
            /*int hiveCount = 0;
            for (WorldGenMyrmexHive.RoomType type : ROOMS) {
                List<BlockPos> roomPos = ClientProxy.getReferedClientHive().getRooms(type);
                String name = type == WorldGenMyrmexHive.RoomType.FOOD ? "myrmex.message.room.food" : type == WorldGenMyrmexHive.RoomType.NURSERY ? "myrmex.message.room.nursery" : "myrmex.message.room.misc";
                for (BlockPos pos : roomPos) {
                    hiveCount++;
                    this.fontRenderer.drawString(I18n.format(name, pos.getX(), pos.getY(), pos.getZ()), i, j + 16 + hiveCount * 22, color, true);
                }
            }
            for (BlockPos pos : ClientProxy.getReferedClientHive().getEntrances().keySet()) {
                hiveCount++;
                this.fontRenderer.drawString(I18n.format("myrmex.message.room.enterance_surface", pos.getX(), pos.getY(), pos.getZ()), i, j + 16 + hiveCount * 22, color, true);
            }
            for (BlockPos pos : ClientProxy.getReferedClientHive().getEntranceBottoms().keySet()) {
                hiveCount++;
                this.fontRenderer.drawString(I18n.format("myrmex.message.room.enterance_bottom", pos.getX(), pos.getY(), pos.getZ()), i, j + 16 + hiveCount * 22, color, true);
            }*/

        }
    }

    public void onClose() {
        if(ClientProxy.getReferedClientHive() != null){
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageGetMyrmexHive(ClientProxy.getReferedClientHive().toNBT()));
        }
    }


    private void drawRoomInfo(MatrixStack ms, String type, BlockPos pos, int i, int j, int color) {
        String translate = "myrmex.message.room." + type;
        this.getMinecraft().fontRenderer.drawString(ms, I18n.format(translate, pos.getX(), pos.getY(), pos.getZ()), i, j + 36 + hiveCount * 22, color);
        hiveCount++;
    }

    private class Room {
        public BlockPos pos;
        public String string;

        public Room(BlockPos pos, String string) {
            this.pos = pos;
            this.string = string;
        }
    }
}
