package com.thaumcraft.common.menu;

import com.thaumcraft.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    public ArcaneWorkbenchMenu(int windowId, Inventory playerInv) {
        super(ModMenuTypes.ARCANE_WORKBENCH.get(), windowId);

        // Core 3x3 crafting grid arrangement offset from Thaumcraft Legacy UI
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                // Mocking layout to avoid complex container interaction until logical integration
                this.addSlot(new Slot(playerInv, j + i * 3, 40 + j * 18, 40 + i * 18));
            }
        }
        
        // Standard Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, 9 + j + i * 9, 15 + j * 18, 152 + i * 18));
            }
        }
        
        // Standard Player Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, 15 + i * 18, 210));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; 
    }
}
