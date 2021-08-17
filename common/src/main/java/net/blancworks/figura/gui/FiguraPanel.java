package net.blancworks.figura.gui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * interface used as the base of display panels for Figura.
 */
public class FiguraPanel {

    public FiguraGuiScreen screen;

    public FiguraPanel(FiguraGuiScreen screen){
        this.screen = screen;
    }

    public void render(MatrixStack matrixStack, int i, int j, float f){}

    public void tick() {}

    public boolean mouseClicked(double d, double e, int i) {return false;}

    public boolean mouseDragged(double d, double e, int i, double f, double g) {return false;}

    public boolean mouseReleased(double d, double e, int i) {return false;}

    public void mouseMoved(double d, double e) {}

    public boolean mouseScrolled(double d, double e, double f){return false;}

    public boolean keyPressed(int i, int j, int k) {return false;}

    public boolean keyReleased(int i, int j, int k) {return false;}

    public void onLostFocus(FiguraPanel newPanel) { }

    public void onGainedFocus(FiguraPanel oldPanel) { }

    public Text getName(){ return new LiteralText("panel"); }
}
