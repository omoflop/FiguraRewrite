package net.blancworks.figura.gui.panels;

import net.blancworks.figura.gui.FiguraGuiScreen;
import net.blancworks.figura.gui.FiguraPanel;
import net.blancworks.figura.gui.elements.CardElement;
import net.minecraft.client.util.math.MatrixStack;

public class FiguraMainPanel extends FiguraPanel {

    public CardElement card = new CardElement();

    public FiguraMainPanel(FiguraGuiScreen screen) {
        super(screen);
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);
        
        card.render(matrixStack, i, j, f);
    }
}
