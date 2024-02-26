package net.joelinrome.plasmacraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.screen.renderer.EnergyDisplayTooltipArea;
import net.joelinrome.plasmacraft.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class DeuteriumExtractorScreen extends AbstractContainerScreen<DeuteriumExtractorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(PlasmaCraft.MOD_ID,"textures/gui/deuterium_extractor_gui.png");
    private EnergyDisplayTooltipArea energyInfoArea;

    public DeuteriumExtractorScreen(DeuteriumExtractorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        // Simply hide the labels so they don't appear on screen
        this.inventoryLabelY = 100000;
        this.titleLabelY = 10000;
        
        assignEnergyInfoArea();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltip(pGuiGraphics, pMouseX, pMouseY, x, y);
    }

    private void renderEnergyAreaTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 156, 11, 8, 64)) {
            pGuiGraphics.renderTooltip(
                    this.font,
                    energyInfoArea.getTooltips(),
                    Optional.empty(),
                    pMouseX - x,
                    pMouseY - y
            );
        }
    }


    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(
                (width - imageWidth) / 2 + 156,
                (height - imageHeight) / 2 + 11,
                menu.blockEntity.getEnergyStorage()
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);

        energyInfoArea.render(guiGraphics);
//        fluidRenderer.render(guiGraphics, x + 26, y + 11, menu.blockEntity.getFluid());
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 85, y + 30, 176, 0, 8, menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY,
                                     int x, int y,
                                     int offsetX, int offsetY,
                                     int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x+offsetX, y+offsetY, width, height);
    }
}
