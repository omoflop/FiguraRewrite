package net.blancworks.figura.gui.panels;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.gui.FiguraGuiScreen;
import net.blancworks.figura.gui.FiguraPanel;
import net.blancworks.figura.gui.elements.CardElement;
import net.blancworks.figura.gui.elements.EntityCardElement;
import net.blancworks.figura.gui.elements.FiguraGuiElement;
import net.blancworks.figura.gui.elements.StencilElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The wardrobe is the panel for Figura that holds all avatars a user has downloaded, created, or favorited.
 * <p>
 * This is the default panel that opens when the Figura GUI opens.
 */
public class FiguraWardrobePanel extends FiguraPanel {

    public final ArrayList<FiguraGuiElement> elements = new ArrayList<>();

    private FiguraGuiElement currentElement = null;

    private final Queue<LoadEntry> entriesToLoad = new LinkedList<>();
    private CompletableFuture loadFuture = CompletableFuture.completedFuture(null);

    public FiguraWardrobePanel(FiguraGuiScreen screen) {
        super(screen);
    }

    @Override
    public void onGainedFocus(FiguraPanel oldPanel) {
        super.onGainedFocus(oldPanel);

        buildCardsForUI();
    }

    @Override
    public void onLostFocus(FiguraPanel newPanel) {
        super.onLostFocus(newPanel);

        elements.clear();
        entriesToLoad.clear();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);

        matrixStack.translate(screen.width / 2.0f, screen.height - 75, 0);

        float rightmostPosition = 0;

        //Prepass to collect positions.
        for (FiguraGuiElement element : elements) {
            rightmostPosition += element.getSize().getX() + 10.0f;
        }

        if (elements.size() > 0) {
            rightmostPosition -= (elements.get(0).getSize().getX() / 2.0f) + 10.0f;
            rightmostPosition -= (elements.get(elements.size() - 1).getSize().getX() / 2.0f) + 10.0f;
        }

        int id = 1;
        int x = 0;

        for (FiguraGuiElement element : elements) {
            matrixStack.push();

            element.position = new Vector2f(x - (rightmostPosition / 2.0f), 0);

            if (element instanceof StencilElement) {
                ((StencilElement) element).stencilLayerID = id++;
            }

            if (element.isReady) {
                if (currentElement == element) {
                    matrixStack.translate(0, 0, -50);
                    element.scale = new Vector2f(1.2f, 1.2f);
                    element.position = new Vector2f(element.position.getX(), element.position.getY());
                } else {
                    element.rotation = new Vec3f(0, 0, 0);
                    element.scale = new Vector2f(1, 1);
                }
            } else {
                element.rotation = new Vec3f(0, 180, 0);
            }
            element.render(matrixStack, mouseX, mouseY, delta);
            
            x += element.getSize().getX() + 10.0f;

            matrixStack.pop();
        }
    }

    @Override
    public void tick() {
        super.tick();


        if (entriesToLoad.size() > 0 && loadFuture.isDone()) {
            LoadEntry entry = entriesToLoad.poll();

            loadFuture = entry.doLoad();
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        Vector2f mousePosCentered = new Vector2f((float) (mouseX - (screen.width / 2.0f)), (float) (mouseY - (screen.height - 75)));

        FiguraGuiElement prevElement = currentElement;
        currentElement = null;

        for (FiguraGuiElement element : elements) {

            //Unable to select elements that aren't ready.
            if (!element.isReady) continue;

            Vector2f size = element.getSize();
            size = new Vector2f((size.getX() * element.scale.getX()) / 2.0f, (size.getY() * element.scale.getY()) / 2.0f);

            Vector2f min = new Vector2f(element.position.getX() - size.getX(), element.position.getY() - size.getY());
            Vector2f max = new Vector2f(element.position.getX() + size.getX(), element.position.getY() + size.getY());

            if (mousePosCentered.getX() >= min.getX() && mousePosCentered.getX() <= max.getX() && mousePosCentered.getY() >= min.getY() && mousePosCentered.getY() <= max.getY()) {
                currentElement = element;
            }
        }

        if (prevElement != null)
            prevElement.rotation = new Vec3f(0, 0, 0);

        if (currentElement != null) {
            Vector2f dirToMousePos = new Vector2f(mousePosCentered.getX() - currentElement.position.getX(), mousePosCentered.getY() - currentElement.position.getY());
            currentElement.rotation = new Vec3f(-dirToMousePos.getY() * 0.4f, dirToMousePos.getX() * 0.5f, 0);
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
                CardElement card = new EntityCardElement(CardElement.CardBackground.BLUE, new LiteralText(fileName), new LiteralText("Local"), 1, MinecraftClient.getInstance().player);
                elements.add(card);

                card.rotation = new Vec3f(0,180,0);
                card.setTransformsToTarget();

                LoadEntry entry = new LoadEntry();
                entry.card = card;
                entry.targetFile = file;

                entry.task = this::attemptAvatarLoad;

                entriesToLoad.add(entry);
            }
        }
    }

    private void attemptAvatarLoad(File file, CardElement card) {
        card.isReady = true;
    }

    @Override
    public Text getName() {
        return new LiteralText("Wardrobe");
    }

    /**
     * A single file-card pair for entries in the wardrobe screen
     */
    private static class LoadEntry {
        public CardElement card;
        public File targetFile;

        public BiConsumer<File, CardElement> task;

        public CompletableFuture doLoad() {
            return FiguraMod.doTask(() -> {
                if (task != null) {
                    task.accept(targetFile, card);
                }
            });
        }
    }
}
