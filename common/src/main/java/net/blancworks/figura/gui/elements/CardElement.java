package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

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

        matrixStack.translate(150, 150, 0);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((-mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 80));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((-mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 80));

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

                bufferBuilder.vertex(matrixStack.peek().getModel(), -25, 25, 0).color(255, 0, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 25, 25, 0).color(255, 0, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 25, -25, 0).color(255, 0, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), -25, -25, 0).color(255, 0, 0, 255).texture(0, 0).next();

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

                bufferBuilder.vertex(matrixStack.peek().getModel(), -500, 500, -30).color(0, 255, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 500, 500, -30).color(0, 255, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 500, -500, -30).color(0, 255, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), -500, -500, -30).color(0, 255, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                GlStateManager._clear(GL11.GL_DEPTH_BUFFER_BIT, false);
                //RenderSystem.disableDepthTest();
                setupStencil();
                BufferRenderer.draw(bufferBuilder);

                RenderSystem.enableDepthTest();

            }

            matrixStack.translate(0,30,-15);
            drawEntity(0, 0, 30, 0, 0, MinecraftClient.getInstance().player, matrixStack);


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

    public static void drawEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity, MatrixStack matrixStack) {
        float h = (float)Math.atan((double)(f / 40.0F));
        float l = (float)Math.atan((double)(g / 40.0F));
        matrixStack.push();
        matrixStack.translate((double)i, (double)j, 0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        matrixStack.translate(0.0D, 0.0D, 0.0D);
        matrixStack.scale((float)k, (float)k, (float)k);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(l * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        float m = livingEntity.bodyYaw;
        float n = livingEntity.getYaw();
        float o = livingEntity.getPitch();
        float p = livingEntity.prevHeadYaw;
        float q = livingEntity.headYaw;
        livingEntity.bodyYaw = 180.0F + h * 20.0F;
        livingEntity.setYaw(180.0F + h * 40.0F);
        livingEntity.setPitch(-l * 20.0F);
        livingEntity.headYaw = livingEntity.getYaw();
        livingEntity.prevHeadYaw = livingEntity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        livingEntity.bodyYaw = m;
        livingEntity.setYaw(n);
        livingEntity.setPitch(o);
        livingEntity.prevHeadYaw = p;
        livingEntity.headYaw = q;
        matrixStack.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
