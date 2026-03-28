package com.thaumcraft.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import com.thaumcraft.Thaumcraft;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final DeferredBlock<Block> AMBER_BLOCK = BLOCKS.register("amber_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.GLASS)));
            
    public static final DeferredBlock<Block> AMBER_BRICK = BLOCKS.register("amber_brick",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.GLASS)));

    public static final DeferredBlock<Block> ARCANE_STONE = BLOCKS.register("arcane_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f, 10.0f).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> ANCIENT_STONE = BLOCKS.register("ancient_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f, 10.0f).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GREATWOOD_LOG = BLOCKS.register("greatwood_log",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> GREATWOOD_PLANKS = BLOCKS.register("greatwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> SILVERWOOD_LOG = BLOCKS.register("silverwood_log",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> SILVERWOOD_PLANKS = BLOCKS.register("silverwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD)));
}
