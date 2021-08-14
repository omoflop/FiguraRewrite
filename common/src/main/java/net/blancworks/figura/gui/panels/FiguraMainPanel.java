package net.blancworks.figura.gui.panels;

import net.blancworks.figura.gui.FiguraGuiScreen;
import net.blancworks.figura.gui.FiguraPanel;
import net.blancworks.figura.gui.elements.CardElement;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class FiguraMainPanel extends FiguraPanel {

    public CardElement card1;
    public CardElement card2;

    public FiguraMainPanel(FiguraGuiScreen screen) {
        super(screen);

        card1 = new CardElement(CardElement.CardBackground.BLUE, new LiteralText("Bunny"), new LiteralText("Fran"));
        card1.stencilLayerID = 10;
        card2 = new CardElement(CardElement.CardBackground.CLOUDS, new LiteralText("Bunny"), new LiteralText("Fran"));
        card2.stencilLayerID = 15;
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);

        matrixStack.push();
        card1.render(matrixStack, i, j, f);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(100, 0, 0);
        card2.render(matrixStack, i, j, f);
        matrixStack.pop();
    }
}
