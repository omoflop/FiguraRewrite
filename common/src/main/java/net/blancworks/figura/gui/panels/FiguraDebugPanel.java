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
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;

public class FiguraDebugPanel extends FiguraPanel {

    public ArrayList<CardElement> cards = new ArrayList<>();

    public FiguraDebugPanel(FiguraGuiScreen screen) {
        super(screen);

        ClientWorld world = MinecraftClient.getInstance().world;
        RabbitEntity entity1 = new RabbitEntity(EntityType.RABBIT, world);
        entity1.setRabbitType(99);

        cards.add(new CardElement(CardElement.CardBackground.BLUE, new LiteralText("Booni"), new LiteralText("Fran"), entity1, 10));
        cards.add(new CardElement(CardElement.CardBackground.CLOUDS, new LiteralText("Axolotl"), new LiteralText("Fran"), new AxolotlEntity(EntityType.AXOLOTL, world), 15));
        cards.add(new CardElement(CardElement.CardBackground.FADE, new LiteralText("Me!"), new LiteralText("Fran"), MinecraftClient.getInstance().player, 20));
        cards.add(new CardElement(CardElement.CardBackground.FLAMES, new LiteralText("Really long name"), new LiteralText("made by really long Fran"), new BlazeEntity(EntityType.BLAZE, world), 25));
        cards.add(new CardElement(CardElement.CardBackground.SPACE, new LiteralText("SPACE!"), new LiteralText("Fran"), new DolphinEntity(EntityType.DOLPHIN, world), 30));
        cards.add(new CardElement(CardElement.CardBackground.DEBUG, new LiteralText("<debug>"), new LiteralText("Fran"), new ArmorStandEntity(EntityType.ARMOR_STAND, world), 35));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);

        float x = mouseX - MinecraftClient.getInstance().getWindow().getWidth() / 2.0f;
        float y = mouseY - MinecraftClient.getInstance().getWindow().getHeight() / 2.0f;
        Vec3f rotation = new Vec3f(((-y / MinecraftClient.getInstance().getWindow().getHeight()) * 150), (-x / MinecraftClient.getInstance().getWindow().getWidth()) * 150, 0);

        matrixStack.push();
        matrixStack.translate(50, 150, 0);

        for (int k = 0, xOff = 0; k < cards.size(); k++, xOff += 100) {
            matrixStack.push();
            matrixStack.translate(xOff, 0, 0);
            cards.get(k).rotation = rotation;
            cards.get(k).render(matrixStack, mouseX, mouseY, delta);
            matrixStack.pop();
        }

        matrixStack.pop();
    }

    @Override
    public Text getName() {
        return new LiteralText("Debug");
    }
}
