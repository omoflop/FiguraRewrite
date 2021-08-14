package net.blancworks.figura.gui.elements;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class FiguraGuiElement extends DrawableHelper {
    public abstract void render(MatrixStack matrixStack, int i, int j, float f);
    public abstract void tick();
}
