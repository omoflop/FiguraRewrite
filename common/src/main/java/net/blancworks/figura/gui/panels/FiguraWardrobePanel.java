package net.blancworks.figura.gui.panels;

import net.blancworks.figura.gui.FiguraGuiScreen;
import net.blancworks.figura.gui.FiguraPanel;
import net.blancworks.figura.gui.elements.CardElement;
import net.blancworks.figura.gui.elements.FiguraGuiElement;
import net.blancworks.figura.gui.elements.StencilElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * The wardrobe is the panel for Figura that holds all avatars a user has downloaded, created, or favorited.
 * <p>
 * This is the default panel that opens when the Figura GUI opens.
 */
public class FiguraWardrobePanel extends FiguraPanel {

    public final ArrayList<FiguraGuiElement> elements = new ArrayList<>();

    private FiguraGuiElement currentElement = null;

    public FiguraWardrobePanel(FiguraGuiScreen screen) {
        super(screen);

        buildCardsForUI();

    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);

        matrixStack.translate(screen.width / 2.0f, screen.height - 75, 0);

        float rightmostPosition = 0;
        
        //Prepass to collect positions.
        for (FiguraGuiElement element : elements) {
            rightmostPosition += element.getSize().getX() + 10.0f;
        }
        
        if(elements.size() > 0) {
            rightmostPosition -= (elements.get(0).getSize().getX() / 2.0f) + 10.0f;
            rightmostPosition -= (elements.get(elements.size() - 1).getSize().getX() / 2.0f) + 10.0f;
        }
        
        int id = 1;

        int x = 0;

        for (FiguraGuiElement element : elements) {
            matrixStack.push();
            
            element.position = new Vector2f(x - (rightmostPosition / 2.0f), 0);
            
            if(element instanceof StencilElement){
                ((StencilElement) element).stencilLayerID = id++;
            }
            
            if (currentElement == element) {
                matrixStack.translate(0, 0, -50);
                element.scale = new Vector2f(1.4f, 1.4f);
                element.position = new Vector2f(element.position.getX() + (element.getSize().getX() / 8.0f), element.position.getY());
                element.render(matrixStack, i, j, f);
            } else {
                element.scale = new Vector2f(1, 1);
                element.render(matrixStack, i, j, f);
            }

            x += element.getSize().getX() + 10.0f;

            matrixStack.pop();
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void mouseMoved(double d, double e) {
        super.mouseMoved(d, e);

        Vector2f mousePosCentered = new Vector2f((float) (d - (screen.width / 2.0f)), (float) (e - (screen.height - 75)));

        FiguraGuiElement prevElement = currentElement;
        currentElement = null;

        for (FiguraGuiElement element : elements) {

            Vector2f size = element.getSize();
            size = new Vector2f(size.getX() / 2.0f, size.getY() / 2.0f);

            Vector2f min = new Vector2f(element.position.getX() - size.getX(), element.position.getY() - size.getY());
            Vector2f max = new Vector2f(element.position.getX() + size.getX(), element.position.getY() + size.getY());

            if (mousePosCentered.getX() >= min.getX() && mousePosCentered.getX() <= max.getX() && mousePosCentered.getY() >= min.getY() && mousePosCentered.getY() <= max.getY()) {
                currentElement = element;
            }
        }

        if(prevElement != null)
            prevElement.rotation = new Vec3f(0, 0, 0);

        if (currentElement != null) {
            Vector2f dirToMousePos = new Vector2f(mousePosCentered.getX() - currentElement.position.getX(), mousePosCentered.getY() - currentElement.position.getY());
            currentElement.rotation = new Vec3f(-dirToMousePos.getY() * 0.25f, dirToMousePos.getX() * 0.3f, 0);
        }
    }

    public void buildCardsForUI() {
        File contentDirectory = MinecraftClient.getInstance().runDirectory.toPath().resolve("figura").toFile();

        try {
            Files.createDirectories(contentDirectory.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] files = contentDirectory.listFiles();

        for (File file : files) {
            String fileName = FilenameUtils.removeExtension(file.getName());

            if (file.isDirectory() && (Files.exists(file.toPath().resolve("model.bbmodel")) || Files.exists(file.toPath().resolve("player_model.bbmodel"))) && Files.exists(file.toPath().resolve("texture.png"))) {
                CardElement card = new CardElement(CardElement.CardBackground.BLUE, new LiteralText(fileName), new LiteralText("Local"), MinecraftClient.getInstance().player, 1);
                elements.add(card);
            }
        }
    }

}
