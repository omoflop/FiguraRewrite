package net.blancworks.figura.gui.panels;

import net.blancworks.figura.gui.FiguraGuiScreen;
import net.blancworks.figura.gui.FiguraPanel;
import net.blancworks.figura.gui.elements.CardElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

public class FiguraMainPanel extends FiguraPanel {

    public ArrayList<CardElement> cards = new ArrayList<>();

    public FiguraMainPanel(FiguraGuiScreen screen) {
        super(screen);

        ClientWorld world = MinecraftClient.getInstance().world;
        RabbitEntity entity1 = new RabbitEntity(EntityType.RABBIT, world);
        entity1.setRabbitType(99);

        cards.add(new CardElement(CardElement.CardBackground.BLUE, new LiteralText("Booni"), new LiteralText("Fran"), entity1, 10));
        cards.add(new CardElement(CardElement.CardBackground.CLOUDS, new LiteralText("Axolotl"), new LiteralText("Fran"), new AxolotlEntity(EntityType.AXOLOTL, world), 15));
        cards.add(new CardElement(CardElement.CardBackground.FADE, new LiteralText("Me!"), new LiteralText("Fran"), MinecraftClient.getInstance().player, 20));
        cards.add(new CardElement(CardElement.CardBackground.FLAMES, new LiteralText("uwu"), new LiteralText("Fran"), new BlazeEntity(EntityType.BLAZE, world), 25));
        cards.add(new CardElement(CardElement.CardBackground.SPACE, new LiteralText("SPACE!"), new LiteralText("Fran"), new DolphinEntity(EntityType.DOLPHIN, world), 30));
        cards.add(new CardElement(CardElement.CardBackground.DEBUG, new LiteralText("<debug>"), new LiteralText("Fran"), new ArmorStandEntity(EntityType.ARMOR_STAND, world), 35));
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);

        for (int k = 0, x = 0; k < cards.size(); k++, x += 100) {
            matrixStack.push();
            matrixStack.translate(x, 0, 0);
            cards.get(k).render(matrixStack, i, j, f);
            matrixStack.pop();
        }
    }
}
