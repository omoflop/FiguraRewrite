package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class CardElement extends StencilElement {

    public Identifier textureID = new Identifier("textures/misc/forcefield.png");

    private float f = 0;

    public CardElement() {


    }


    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {

        this.f += f;
        matrixStack.push();

        float mouseX = (float) MinecraftClient.getInstance().mouse.getX();
        float mouseY = (float) MinecraftClient.getInstance().mouse.getY();
        mouseX -= MinecraftClient.getInstance().getWindow().getWidth() / 2.0f;
        mouseY -= MinecraftClient.getInstance().getWindow().getHeight() / 2.0f;

        matrixStack.translate(50, 75, 0);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 80));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 80));
        matrixStack.translate(-50, -75, 0);

        try {

            stencilLayerID = 35;

            //Draw the main body of the card
            /**{
             MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);

             BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
             bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);

             bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 200, -5).texture(0,1).color(1,1,1,1).next();
             bufferBuilder.vertex(matrixStack.peek().getModel(), 100, 200, -5).texture(1,1).color(1,1,1,1).next();
             bufferBuilder.vertex(matrixStack.peek().getModel(), 100, 0, -5).texture(1,0).color(1,1,1,1).next();
             bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 0, -5).texture(0,0).color(1,1,1,1).next();

             bufferBuilder.end();
             RenderSystem.enableAlphaTest();
             BufferRenderer.draw(bufferBuilder);
             }**/

            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();


            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);

                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                //RenderSystem.setShaderTexture(0, textureID);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 150, 0).color(255, 0, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 90, 150, 0).color(255, 0, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 90, 10, 0).color(255, 0, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 10, 0).color(255, 0, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                setupStencilPrep();
                BufferRenderer.draw(bufferBuilder);
            }

            //Draw the card art here
            {
                MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);

                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 20, 120, 15).color(0, 255, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 80, 120, 15).color(0, 255, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 80, 20, 15).color(0, 255, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 20, 20, 15).color(0, 255, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                RenderSystem.disableDepthTest();
                setupStencil();
                BufferRenderer.draw(bufferBuilder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrixStack.pop();
        resetStencil();
    }

    @Override
    public void tick() {

    }


}
