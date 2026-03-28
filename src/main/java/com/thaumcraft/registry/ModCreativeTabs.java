package com.thaumcraft.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.thaumcraft.Thaumcraft;
import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thaumcraft.MODID);

    public static final Supplier<CreativeModeTab> THAUMCRAFT_TAB = CREATIVE_MODE_TABS.register("thaumcraft_tab", 
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thaumcraft"))
            .icon(() -> new ItemStack(ModItems.THAUMIUM_INGOT.get()))
            .displayItems((parameters, output) -> {
                // Blocks
                output.accept(ModItems.AMBER_BLOCK.get());
                output.accept(ModItems.AMBER_BRICK.get());
                output.accept(ModItems.ARCANE_STONE.get());
                output.accept(ModItems.ANCIENT_STONE.get());
                output.accept(ModItems.GREATWOOD_LOG.get());
                output.accept(ModItems.GREATWOOD_PLANKS.get());
                output.accept(ModItems.SILVERWOOD_LOG.get());
                output.accept(ModItems.SILVERWOOD_PLANKS.get());
                
                // Items
                output.accept(ModItems.THAUMIUM_INGOT.get());
                output.accept(ModItems.VOID_INGOT.get());
                output.accept(ModItems.BRASS_INGOT.get());
                output.accept(ModItems.ALUMENTUM.get());
                output.accept(ModItems.AMBER.get());
                output.accept(ModItems.QUICKSILVER.get());
                
                // Crystals
                output.accept(ModItems.AIR_CRYSTAL.get());
                output.accept(ModItems.FIRE_CRYSTAL.get());
                output.accept(ModItems.WATER_CRYSTAL.get());
                output.accept(ModItems.EARTH_CRYSTAL.get());
                output.accept(ModItems.ORDER_CRYSTAL.get());
                output.accept(ModItems.ENTROPY_CRYSTAL.get());
            }).build()
    );
}
