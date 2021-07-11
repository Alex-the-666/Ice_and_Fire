package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.inventory.IafContainerRegistry;

import net.minecraft.client.gui.ScreenManager;

public class IafGuiRegistry {

    public static void register() {
        ScreenManager.registerFactory(IafContainerRegistry.IAF_LECTERN_CONTAINER, GuiLectern::new);
        ScreenManager.registerFactory(IafContainerRegistry.PODIUM_CONTAINER, GuiPodium::new);
        ScreenManager.registerFactory(IafContainerRegistry.DRAGON_CONTAINER, GuiDragon::new);
        ScreenManager.registerFactory(IafContainerRegistry.HIPPOGRYPH_CONTAINER, GuiHippogryph::new);
        ScreenManager.registerFactory(IafContainerRegistry.HIPPOCAMPUS_CONTAINER, GuiHippocampus::new);
        ScreenManager.registerFactory(IafContainerRegistry.DRAGON_FORGE_CONTAINER, GuiDragonForge::new);
    }
}
