package net.blancworks.figura.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.gui.panels.FiguraDebugPanel;
import net.blancworks.figura.gui.panels.FiguraWardrobePanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

/**
 * This is the generic, top-level screen that's used in minecraft itself for the menu.
 * <p>
 * We don't use the Minecraft menu system for sub-menus because we require significant customization for them.
 */
public class FiguraGuiScreen extends Screen {

    public Screen parentScreen;

    public FiguraPanel currentPanel;

    private final ArrayList<FiguraPanel> allPanels = new ArrayList<>();

    public FiguraDebugPanel debugPanel = new FiguraDebugPanel(this);
    public FiguraWardrobePanel wardrobePanel = new FiguraWardrobePanel(this);

    public final FiguraGUIFramebuffer guiFramebuffer = new FiguraGUIFramebuffer();

    public FiguraGuiScreen(Screen parentScreen) {
        super(new TranslatableText("gui.figura.mainpanel"));
        this.parentScreen = parentScreen;

        allPanels.add(debugPanel);
        allPanels.add(wardrobePanel);

        switchToPanel(debugPanel);
    }

    public void switchToPanel(FiguraPanel newPanel) {
        if (currentPanel != null) currentPanel.onLostFocus(newPanel);
        newPanel.onGainedFocus(currentPanel);

        currentPanel = newPanel;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);

        int windowWidth = MinecraftClient.getInstance().getWindow().getWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        guiFramebuffer.setSize(windowWidth, windowHeight);

        //Enable stencil buffer during this phase of rendering
        GL30.glEnable(GL30.GL_STENCIL_TEST);
        GlStateManager._stencilMask(0xFF);
        //Bind custom GUI framebuffer to be used for rendering
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, guiFramebuffer.getFbo());

        //Clear GUI framebuffer
        GlStateManager._clearStencil(0);
        GlStateManager._clearColor(0, 0, 0, 1.0F);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT, false);

        RenderSystem.backupProjectionMatrix();
        MinecraftClient.getInstance().getFramebuffer().draw(windowWidth, windowHeight, false);
        RenderSystem.restoreProjectionMatrix();

        drawMenuSelector(matrixStack, mouseX, mouseY, delta);
        if (currentPanel != null) currentPanel.render(matrixStack, mouseX, mouseY, delta);

        //Reset state before we go back to normal rendering
        GlStateManager._enableDepthTest();
        //Set a sensible default for stencil buffer operations
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, 255);
        GL30.glDisable(GL30.GL_STENCIL_TEST);

        //Bind vanilla framebuffer again
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, MinecraftClient.getInstance().getFramebuffer().fbo);

        RenderSystem.disableBlend();
        //Draw GUI framebuffer -> vanilla framebuffer
        guiFramebuffer.drawToScreen(matrixStack, width, height);
        RenderSystem.enableBlend();
    }

    @Override
    public void tick() {
        super.tick();

        if (currentPanel != null) currentPanel.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentPanel != null) return currentPanel.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (currentPanel != null) return currentPanel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (currentPanel != null) return currentPanel.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (currentPanel != null) currentPanel.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (currentPanel != null) return currentPanel.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (currentPanel != null) return currentPanel.keyPressed(keyCode, scanCode, modifiers);

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (super.keyReleased(keyCode, scanCode, modifiers)) return true;
        if (currentPanel != null) return currentPanel.keyReleased(keyCode, scanCode, modifiers);

        return false;
    }

    private float rot = 0;

    private void drawMenuSelector(MatrixStack stack, int mouseX, int mouseY, float delta) {

        stack.push();

        stack.translate(width / 2.0f, 20, 0);
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-5));

        RenderSystem.setShaderTexture(0, new Identifier("figura", "textures/cards/cheese_platform.png"));
        drawTexture(stack, -60, -26, 120, 48, 0, 0, 240, 96, 240, 96);

        int currIndex = allPanels.indexOf(currentPanel);

        //rot = MathHelper.lerp(0.1f, rot, currIndex * 45);
        rot = (rot + delta * 4);

        int index = 0;

        for (FiguraPanel panel : allPanels) {
            Text txt = panel.getName();

            float selectionRot = ((index++) - currIndex) * 60;
            float realRot = rot + selectionRot;

            //if (realRot > 91 || realRot < -91) continue;

            float width = textRenderer.getWidth(txt);

            stack.push();

            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(realRot));
            stack.translate(-width / 2f, 0, 45);

            drawTextWithShadow(stack, textRenderer, txt, 0, 0, 0xffffff);

            stack.pop();
        }

        stack.pop();
    }
}
