package net.goo.brutality.gui.screen;

import net.goo.brutality.Brutality;
import net.goo.brutality.block.block_entity.GrayFilingCabinetBlockEntity;
import net.goo.brutality.block.block_entity.WhiteFilingCabinetBlockEntity;
import net.goo.brutality.gui.menu.FilingCabinetMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilingCabinetScreen extends AbstractContainerScreen<FilingCabinetMenu> implements MenuAccess<FilingCabinetMenu> {
    private static final ResourceLocation WHITE_CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Brutality.MOD_ID, "textures/gui/screens/white_filing_cabinet.png");
    private static final ResourceLocation GRAY_CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Brutality.MOD_ID, "textures/gui/screens/gray_filing_cabinet.png");
    /**
     * Window height is calculated with these values" the more rows, the higher
     */
    private final int containerRows;
    private final WhiteFilingCabinetBlockEntity blockEntity;

    public FilingCabinetScreen(FilingCabinetMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.containerRows = pMenu.getRowCount();
        this.blockEntity = pMenu.getBlockEntity();
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX      the x-coordinate of the mouse cursor.
     * @param pMouseY      the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        ResourceLocation bg = blockEntity instanceof GrayFilingCabinetBlockEntity ? GRAY_CONTAINER_BACKGROUND : WHITE_CONTAINER_BACKGROUND;
        pGuiGraphics.blit(bg, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        pGuiGraphics.blit(bg, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}