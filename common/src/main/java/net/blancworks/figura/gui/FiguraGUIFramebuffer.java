package net.blancworks.figura.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

/**
 * Custom class made to track the status of the custom framebuffer that we use for rendering the UI in Figura.
 * <p>
 * The UI in figura uses stencil buffers, and we'd prefer not to mess with Vanilla's framebuffer- So our solution
 * is to use our own framebuffer, then draw that framebuffer to minecraft's, after it's all good to go.
 */
public class FiguraGUIFramebuffer {

    private int fbo = -1;
    private int colorAttachment = -1;
    private int depthStencilAttachment = -1;

    private int width, height;

    /**
     * Attempts to adjust the framebuffer to match a given size
     *
     * @param nWidth  The new width
     * @param nHeight The new height
     */
    public void setSize(int nWidth, int nHeight) {

        //Minimized window, we don't even need the framebuffer, so...
        if(nWidth == 0 || nHeight == 0)
            return;

        if (nWidth != width || nHeight != height) {
            width = nWidth;
            height = nHeight;

            if (this.fbo != -1) {
                GlStateManager._glDeleteFramebuffers(this.fbo);
                fbo = -1;
            }
            if (this.colorAttachment != -1) {
                TextureUtil.releaseTextureId(this.colorAttachment);
                this.colorAttachment = -1;
            }
            if (this.depthStencilAttachment != -1) {
                TextureUtil.releaseTextureId(this.depthStencilAttachment);
                this.depthStencilAttachment = -1;
            }

            this.fbo = GlStateManager.glGenFramebuffers();
            this.colorAttachment = TextureUtil.generateTextureId();
            this.depthStencilAttachment = TextureUtil.generateTextureId();

            GlStateManager._bindTexture(this.depthStencilAttachment);
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH24_STENCIL8, width, height, 0, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8, (IntBuffer)null);

            GlStateManager._bindTexture(this.colorAttachment);
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (IntBuffer)null);

            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
            GlStateManager._bindTexture(this.colorAttachment);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.colorAttachment, 0);

            GlStateManager._bindTexture(this.depthStencilAttachment);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthStencilAttachment, 0);
        }
    }

    public int getFbo() {
        return fbo;
    }

    public void drawToScreen(MatrixStack stack, int viewWidth, int viewHeight) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, width, height);

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Shader shader = minecraftClient.gameRenderer.blitScreenShader;
        shader.addSampler("DiffuseSampler", colorAttachment);
        //shader.addSampler("DiffuseSampler", MinecraftClient.getInstance().getFramebuffer().getColorAttachment());
        //shader.addSampler("DiffuseSampler", MinecraftClient.getInstance().getTextureManager().getTexture(ClickableWidget.WIDGETS_TEXTURE).getGlId());
        Matrix4f matrix4f = Matrix4f.projectionMatrix((float)width, (float)(-height), 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(Matrix4f.translate(0.0F, 0.0F, -2000.0F));
        }

        if (shader.projectionMat != null) {
            shader.projectionMat.set(matrix4f);
        }

        shader.bind();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, (double)height, 0.0D).texture(0.0F, 0).color(255, 255, 255, 255).next();
        bufferBuilder.vertex((double)width, (double)height, 0.0D).texture(1, 0).color(255, 255, 255, 255).next();
        bufferBuilder.vertex((double)width, 0.0D, 0.0D).texture(1, 1).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, 1).color(255, 255, 255, 255).next();
        bufferBuilder.end();
        BufferRenderer.postDraw(bufferBuilder);
        shader.unbind();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }
}
