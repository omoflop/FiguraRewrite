package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

public class CardElement extends StencilElement {

    public final Identifier BACKGROUND;
    public final Identifier BACKGROUND_OVERLAY = new Identifier("figura", "textures/cards/background_overlay.png");
    public final Text NAME;
    public final Text AUTHOR;

    public MinecraftClient client;

    public CardElement(CardBackground background, Text name, Text author) {
        this.BACKGROUND = new Identifier(background.namespace, background.path);
        this.NAME = name;
        this.AUTHOR = author;

        this.client = MinecraftClient.getInstance();
    }

    public enum CardBackground {
        BLUE("figura", "textures/cards/backgrounds/blue.png"),
        CLOUDS("figura", "textures/cards/backgrounds/clouds.png");

        public final String namespace;
        public final String path;
        CardBackground(String namespace, String path) {
            this.namespace = namespace;
            this.path = path;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {

        matrixStack.push();

        float mouseX = (float) MinecraftClient.getInstance().mouse.getX();
        float mouseY = (float) MinecraftClient.getInstance().mouse.getY();
        mouseX -= MinecraftClient.getInstance().getWindow().getWidth() / 2.0f;
        mouseY -= MinecraftClient.getInstance().getWindow().getHeight() / 2.0f;

        matrixStack.translate(100, 100, 0);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((-mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 120));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((-mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 120));

        try {
            //Draw the main body of the card
            /*{
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
             }*/

            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 96, 0).color(255, 0, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 96, 0).color(255, 0, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 0, 0).color(255, 0, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 0, 0).color(255, 0, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                setupStencilWrite();
                BufferRenderer.draw(bufferBuilder);
            }

            //Draw the card art here
            {
                //render bg
                RenderSystem.setShaderTexture(0, BACKGROUND);
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

                //stencil magic
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 96, 0).color(0, 255, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 96, 0).color(0, 255, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 0, 0).color(0, 255, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 0, 0, 0).color(0, 255, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                GlStateManager._clear(GL11.GL_DEPTH_BUFFER_BIT, false);
                setupStencilTest();
                BufferRenderer.draw(bufferBuilder);
                RenderSystem.enableDepthTest();

                //render overlay
                RenderSystem.enableBlend();

                RenderSystem.setShaderTexture(0, BACKGROUND_OVERLAY);
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

                RenderSystem.disableBlend();

                //render text
                matrixStack.push();
                matrixStack.translate(3f, 3f,0f); //3px offset
                drawTextWithShadow(matrixStack, client.textRenderer, NAME, 0, 0, 0xFFFFFF);
                matrixStack.pop();

                matrixStack.push();
                matrixStack.translate(3f, 11f,0f); //3px offset + 7px above text + 1px spacing
                matrixStack.scale(0.75f, 0.75f,1f);
                drawTextWithShadow(matrixStack, client.textRenderer, AUTHOR, 0, 0, 0xFFFFFF);
                matrixStack.pop();
            }

            //render model
            matrixStack.translate(0,0,-15);
            RabbitEntity entity = new RabbitEntity(EntityType.RABBIT, client.world);
            entity.setRabbitType(99);

            drawEntity(32, 48, 30, 0, 0, entity, matrixStack);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrixStack.pop();
    }

    @Override
    public void tick() {

    }

    public static void drawEntity(int x, int y, int scale, float yaw, float pitch, LivingEntity livingEntity, MatrixStack matrixStack) {
        float h = (float)Math.atan(yaw / 40.0F);
        float l = (float)Math.atan(pitch / 40.0F);
        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        matrixStack.translate(0.0D, 0.0D, 0.0D);
        matrixStack.scale((float) scale, (float) scale, (float) scale);
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
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880));
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
