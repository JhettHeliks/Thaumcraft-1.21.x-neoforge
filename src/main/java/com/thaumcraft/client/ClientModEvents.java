package com.thaumcraft.client;

import com.thaumcraft.Thaumcraft;
import com.thaumcraft.registry.ModMenuTypes;
import com.thaumcraft.client.gui.ArcaneWorkbenchScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Thaumcraft.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
    }
}
