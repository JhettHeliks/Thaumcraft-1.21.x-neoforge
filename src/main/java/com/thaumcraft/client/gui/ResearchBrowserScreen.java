package com.thaumcraft.client.gui;

import com.thaumcraft.Thaumcraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResearchBrowserScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_browser.png");

    private double scrollX = 0;
    private double scrollY = 0;
    private double targetScrollX = 0;
    private double targetScrollY = 0;
    private float zoom = 1.0f;
    
    private boolean isPanning = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    public ResearchBrowserScreen() {
        super(Component.translatable("gui.thaumcraft.research_browser"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Smooth panning interpolation
        this.scrollX += (this.targetScrollX - this.scrollX) * 0.2D;
        this.scrollY += (this.targetScrollY - this.scrollY) * 0.2D;

        guiGraphics.pose().pushPose();
        
        guiGraphics.pose().translate(this.width / 2.0F, this.height / 2.0F, 0);
        guiGraphics.pose().scale(this.zoom, this.zoom, 1.0f);
        guiGraphics.pose().translate(-this.scrollX, -this.scrollY, 0);

        // Draw a repeating background tile map 
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                // Drawing the 256x256 tiles from gui_research_browser.png
                guiGraphics.blit(TEXTURE, i * 256, j * 256, 0, 0, 256, 256, 256, 256);
            }
        }
        
        // Mocking some research node placements for visual confirmation
        guiGraphics.fill(-16, -16, 16, 16, 0xFFAAAAAA);
        guiGraphics.fill(150, 50, 182, 82, 0xFFFFAA00);

        guiGraphics.pose().popPose();

        guiGraphics.drawString(this.font, "Thaumonomicon - Work in progress layout", 10, 10, 0xFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isPanning = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isPanning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isPanning && button == 0) {
            this.targetScrollX -= (mouseX - this.lastMouseX) / this.zoom;
            this.targetScrollY -= (mouseY - this.lastMouseY) / this.zoom;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.zoom = (float) Math.max(0.5f, Math.min(2.0f, this.zoom + (scrollY * 0.1f)));
        return true;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
