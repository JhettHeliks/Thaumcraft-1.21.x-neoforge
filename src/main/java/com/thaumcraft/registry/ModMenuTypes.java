package com.thaumcraft.registry;

import com.thaumcraft.Thaumcraft;
import com.thaumcraft.common.menu.ArcaneWorkbenchMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);

    public static final Supplier<MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH = MENU_TYPES.register("arcane_workbench",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new ArcaneWorkbenchMenu(windowId, inv)));
}
