package com.thaumcraft;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

import com.thaumcraft.registry.ModBlocks;
import com.thaumcraft.registry.ModItems;
import com.thaumcraft.registry.ModCreativeTabs;
import com.thaumcraft.registry.ModMenuTypes;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {
    public static final String MODID = "thaumcraft";

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
    }
}
