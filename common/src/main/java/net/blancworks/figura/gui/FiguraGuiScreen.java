package net.blancworks.figura.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.gui.panels.FiguraMainPanel;
import net.blancworks.figura.gui.panels.FiguraWardrobePanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the generic, top-level screen that's used in minecraft itself for the menu.
 * <p>
 * We don't use the Minecraft menu system for sub-menus because we require significant customization for them.
 */
public class FiguraGuiScreen extends Screen {

    public Screen parentScreen;

    public FiguraPanel currentPanel;

    private final ArrayList<FiguraPanel> allPanels = new ArrayList<>();

    public FiguraMainPanel mainPanel = new FiguraMainPanel(this);
    public FiguraWardrobePanel wardrobePanel = new FiguraWardrobePanel(this);

    public final FiguraGUIFramebuffer guiFramebuffer = new FiguraGUIFramebuffer();

    public FiguraGuiScreen(Screen parentScreen) {
        super(new TranslatableText("gui.figura.mainpanel"));

        allPanels.add(mainPanel);
        allPanels.add(wardrobePanel);
        allPanels.add(wardrobePanel);
        allPanels.add(wardrobePanel);
        allPanels.add(wardrobePanel);


        switchToPanel(wardrobePanel);
    }

    public void switchToPanel(FiguraPanel newPanel) {
        if (currentPanel != null) currentPanel.onLostFocus(newPanel);
        newPanel.onGainedFocus(currentPanel);

        currentPanel = newPanel;
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        super.render(matrixStack, i, j, f);

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

        drawMenuSelector(matrixStack, i, j, f);

        if (currentPanel != null) currentPanel.render(matrixStack, i, j, f);

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
    public boolean mouseClicked(double d, double e, int i) {
        if (currentPanel != null) return currentPanel.mouseClicked(d, e, i);
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (currentPanel != null) return currentPanel.mouseDragged(d, e, i, f, g);
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (currentPanel != null) return currentPanel.mouseReleased(d, e, i);
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void mouseMoved(double d, double e) {
        if (currentPanel != null) currentPanel.mouseMoved(d, e);
        super.mouseMoved(d, e);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (currentPanel != null) return currentPanel.mouseScrolled(d, e, f);
        return super.mouseScrolled(d, e, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) return true;
        if (currentPanel != null) return currentPanel.keyPressed(i, j, k);

        return false;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if (super.keyReleased(i, j, k)) return true;
        if (currentPanel != null) return currentPanel.keyReleased(i, j, k);

        return false;
    }


    private float rot = 0;

    private void drawMenuSelector(MatrixStack stack, int i, int j, float f) {

        stack.push();

        stack.translate(width / 2.0f, 20, 0);
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-5));

        int currIndex = allPanels.indexOf(currentPanel);

        //rot = MathHelper.lerp(0.1f, rot, currIndex * 45);

        rot = (rot + f * 4);

        int index = 0;

        float offset = 0;

        for (FiguraPanel panel : allPanels) {
            Text txt = panel.getName();

            float selectionRot = ((index++) - currIndex) * 60;
            float realRot = rot + selectionRot;

            //if (realRot > 91 || realRot < -91) continue;

            float width = textRenderer.getWidth(txt);

            stack.push();

            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(realRot));
            stack.translate(-width/2.0f, 0, 45);

            drawTextWithShadow(stack, textRenderer, txt, 0, 0, 0xffffff);

            if (index == 0) offset -= width / 2.0f;
            offset += width + 10;

            stack.pop();
        }

        stack.pop();
    }
}
