package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

public class CardElement extends StencilElement {

    public final Identifier BACKGROUND;
    public final Identifier BACKGROUND_OVERLAY = new Identifier("figura", "textures/cards/background_overlay.png");
    public final Text NAME;
    public final Text AUTHOR;
    public final LivingEntity ENTITY;

    public MinecraftClient client;

    public CardElement(CardBackground background, Text name, Text author, LivingEntity entity, int stencilLayerID) {
        this.BACKGROUND = new Identifier(background.namespace, background.path);
        this.NAME = name;
        this.AUTHOR = author;
        this.ENTITY = entity;

        this.client = MinecraftClient.getInstance();
        this.stencilLayerID = stencilLayerID;
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

        matrixStack.translate(150, 150, 0);

        Vec2f rotation = new Vec2f(((-mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 120), (-mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 120);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotation.x));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotation.y));

        //reset render properties
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        try {
            //center rotation
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,  96, 0).color(255, 0, 0, 255).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 96, 0).color(255, 0, 0, 255).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64,  0, 0).color(255, 0, 0, 255).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,   0, 0).color(255, 0, 0, 255).texture(0, 0).next();

                bufferBuilder.end();
                setupStencilWrite();
                BufferRenderer.draw(bufferBuilder);
            }

            //Draw the card art here
            {
                //stencil magic
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,  96, 0).color(0, 255, 0, 0).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 96, 0).color(0, 255, 0, 0).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64,  0, 0).color(0, 255, 0, 0).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,   0, 0).color(0, 255, 0, 0).texture(0, 0).next();

                bufferBuilder.end();
                setupStencilTest();
                BufferRenderer.draw(bufferBuilder);
            }

            //background
            {
                RenderSystem.setShaderTexture(0, BACKGROUND);

                matrixStack.push();
                matrixStack.translate(-16, -24, 16);
                matrixStack.scale(1.5f, 1.5f, 1.5f);
                //matrices, x, y, x size, y size, u offset, v offset, u size, v size, texture width, texture height
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
            }

            //render model
            {
                RenderSystem.enableDepthTest();

                matrixStack.push();
                matrixStack.translate(0, 0, -16);
                drawEntity(32, 48, 30, rotation.y, rotation.x, ENTITY, matrixStack);
                matrixStack.pop();

                RenderSystem.disableDepthTest();
            }

            //render overlay
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, BACKGROUND_OVERLAY);

                matrixStack.push();
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
            }

            //render texts
            {
                //name
                matrixStack.push();
                matrixStack.translate(3f, 3f,0f); //3px offset
                drawTextWithShadow(matrixStack, client.textRenderer, NAME, 0, 0, 0xffffff);
                matrixStack.pop();

                //author
                matrixStack.push();
                matrixStack.translate(3f, 11f,0f); //3px offset + 7px above text + 1px spacing
                matrixStack.scale(0.75f, 0.75f,1f);
                drawTextWithShadow(matrixStack, client.textRenderer, AUTHOR, 0, 0, 0xffffff);
                matrixStack.pop();
            }

            matrixStack.pop();

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
        float h = (float)Math.atan(yaw / 40f);
        float l = (float)Math.atan(pitch / 40f);
        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(1f, 1f, -1f);
        matrixStack.translate(0d, 0d, 0d);
        matrixStack.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(l * 20f);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        float m = livingEntity.bodyYaw;
        float n = livingEntity.getYaw();
        float o = livingEntity.getPitch();
        float p = livingEntity.prevHeadYaw;
        float q = livingEntity.headYaw;
        livingEntity.bodyYaw = 180f + h * 20f;
        livingEntity.setYaw(180f + h * 40f);
        livingEntity.setPitch(-l * 20f);
        livingEntity.headYaw = livingEntity.getYaw();
        livingEntity.prevHeadYaw = livingEntity.getYaw();
        //RenderSystem.setShaderLights(Vec3f.ZERO, Vec3f.ZERO);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0d, -1d, 0d, 0f, 1f, matrixStack, immediate, 0xf000f0));
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
