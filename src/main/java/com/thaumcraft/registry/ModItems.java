package com.thaumcraft.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import com.thaumcraft.Thaumcraft;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    // Ingots & Basics
    public static final DeferredItem<Item> THAUMIUM_INGOT = ITEMS.registerSimpleItem("thaumium_ingot", new Item.Properties());
    public static final DeferredItem<Item> VOID_INGOT = ITEMS.registerSimpleItem("void_ingot", new Item.Properties());
    public static final DeferredItem<Item> BRASS_INGOT = ITEMS.registerSimpleItem("brass_ingot", new Item.Properties());
    
    public static final DeferredItem<Item> ALUMENTUM = ITEMS.registerSimpleItem("alumentum", new Item.Properties());
    public static final DeferredItem<Item> AMBER = ITEMS.registerSimpleItem("amber", new Item.Properties());
    public static final DeferredItem<Item> QUICKSILVER = ITEMS.registerSimpleItem("quicksilver", new Item.Properties());

    // Crystals
    public static final DeferredItem<Item> AIR_CRYSTAL = ITEMS.registerSimpleItem("air_crystal", new Item.Properties());
    public static final DeferredItem<Item> FIRE_CRYSTAL = ITEMS.registerSimpleItem("fire_crystal", new Item.Properties());
    public static final DeferredItem<Item> WATER_CRYSTAL = ITEMS.registerSimpleItem("water_crystal", new Item.Properties());
    public static final DeferredItem<Item> EARTH_CRYSTAL = ITEMS.registerSimpleItem("earth_crystal", new Item.Properties());
    public static final DeferredItem<Item> ORDER_CRYSTAL = ITEMS.registerSimpleItem("order_crystal", new Item.Properties());
    public static final DeferredItem<Item> ENTROPY_CRYSTAL = ITEMS.registerSimpleItem("entropy_crystal", new Item.Properties());
    
    // Block Items
    public static final DeferredItem<BlockItem> AMBER_BLOCK = ITEMS.registerSimpleBlockItem("amber_block", ModBlocks.AMBER_BLOCK);
    public static final DeferredItem<BlockItem> AMBER_BRICK = ITEMS.registerSimpleBlockItem("amber_brick", ModBlocks.AMBER_BRICK);
    public static final DeferredItem<BlockItem> ARCANE_STONE = ITEMS.registerSimpleBlockItem("arcane_stone", ModBlocks.ARCANE_STONE);
    public static final DeferredItem<BlockItem> ANCIENT_STONE = ITEMS.registerSimpleBlockItem("ancient_stone", ModBlocks.ANCIENT_STONE);
    public static final DeferredItem<BlockItem> GREATWOOD_LOG = ITEMS.registerSimpleBlockItem("greatwood_log", ModBlocks.GREATWOOD_LOG);
    public static final DeferredItem<BlockItem> GREATWOOD_PLANKS = ITEMS.registerSimpleBlockItem("greatwood_planks", ModBlocks.GREATWOOD_PLANKS);
    public static final DeferredItem<BlockItem> SILVERWOOD_LOG = ITEMS.registerSimpleBlockItem("silverwood_log", ModBlocks.SILVERWOOD_LOG);
    public static final DeferredItem<BlockItem> SILVERWOOD_PLANKS = ITEMS.registerSimpleBlockItem("silverwood_planks", ModBlocks.SILVERWOOD_PLANKS);
}
