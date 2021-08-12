package net.blancworks.figura.gui;

import net.blancworks.figura.gui.panels.FiguraMainPanel;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

/**
 * This is the generic, top-level screen that's used in minecraft itself for the menu.
 * <p>
 * We don't use the Minecraft menu system for sub-menus because we require significant customization for them.
 */
public class FiguraGuiScreen extends Screen {

    public Screen parentScreen;

    public FiguraPanel currentPanel;

    public FiguraMainPanel mainPanel = new FiguraMainPanel(this);

    public FiguraGuiScreen(Screen parentScreen) {
        super(new TranslatableText("gui.figura.mainpanel"));

        switchToPanel(mainPanel);
    }

    public void switchToPanel(FiguraPanel newPanel) {
        if (currentPanel != null) currentPanel.onLostFocus(newPanel);
        newPanel.onGainedFocus(currentPanel);

        currentPanel = newPanel;
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);

        if (currentPanel != null) currentPanel.render(matrixStack, i, j, f);
    }

    @Override
    public void tick() {
        super.tick();

        if (currentPanel != null) currentPanel.tick();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (currentPanel != null) return currentPanel.mouseClicked(d, e, i);
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (currentPanel != null) return currentPanel.mouseDragged(d, e, i, f, g);
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (currentPanel != null) return currentPanel.mouseReleased(d, e, i);
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void mouseMoved(double d, double e) {
        if (currentPanel != null) currentPanel.mouseMoved(d, e);
        super.mouseMoved(d, e);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (currentPanel != null) return currentPanel.mouseScrolled(d, e, f);
        return super.mouseScrolled(d, e, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) return true;
        if (currentPanel != null) return currentPanel.keyPressed(i, j, k);

        return false;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if (super.keyReleased(i, j, k)) return true;
        if (currentPanel != null) return currentPanel.keyReleased(i, j, k);
        
        return false;
    }
}
