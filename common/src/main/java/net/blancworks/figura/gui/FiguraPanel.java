package net.blancworks.figura.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * interface used as the base of display panels for Figura.
 */
public class FiguraPanel {

    public FiguraGuiScreen screen;

    public FiguraPanel(FiguraGuiScreen screen) {
        this.screen = screen;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {}

    public void tick() {}

    public boolean mouseClicked(double mouseX, double mouseY, int button) {return false;}

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {return false;}

    public boolean mouseReleased(double mouseX, double mouseY, int button) {return false;}

    public void mouseMoved(double mouseX, double mouseY) {}

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {return false;}

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {return false;}

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {return false;}

    public void onLostFocus(FiguraPanel newPanel) {}

    public void onGainedFocus(FiguraPanel oldPanel) {}

    public Text getName() { return new LiteralText("panel"); }
}
