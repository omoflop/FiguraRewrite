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
import org.lwjgl.opengl.GL30;

public class CardElement extends StencilElement {

    public final Identifier BACKGROUND;
    public final Identifier BACKGROUND_OVERLAY = new Identifier("figura", "textures/cards/background_overlay.png");
    public final Identifier BACK = new Identifier("figura", "textures/cards/back.png");
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

        Vec2f rotation = new Vec2f(((-mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 150), (-mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 150);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotation.x));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotation.y));

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        try {
            //center rotation
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            //enable stencil
            GL30.glEnable(GL30.GL_STENCIL_TEST);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,  96, 0).color(0xff, 0x72, 0xb7, 0xff).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64, 96, 0).color(0xff, 0x72, 0xb7, 0xff).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 64,  0, 0).color(0xff, 0x72, 0xb7, 0xff).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 0,   0, 0).color(0xff, 0x72, 0xb7, 0xff).texture(0, 0).next();

                bufferBuilder.end();
                setupStencilWrite();
                BufferRenderer.draw(bufferBuilder);
            }

            //stencil allowed area
            {
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(),0,  96, 0).color(0, 0xff, 0, 0).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(),64, 96, 0).color(0, 0xff, 0, 0).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(),64,  0, 0).color(0, 0xff, 0, 0).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(),0,   0, 0).color(0, 0xff, 0, 0).texture(0, 0).next();

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
                drawEntity(32, 48, 30, rotation.x, rotation.y, ENTITY, matrixStack);
                matrixStack.pop();

                RenderSystem.disableDepthTest();
            }

            //disable stencil
            GL30.glDisable(GL30.GL_STENCIL_TEST);

            //render back art
            {
                RenderSystem.setShaderTexture(0, BACK);

                matrixStack.push();
                matrixStack.translate(64f, 0f,0f);
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
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

    public static void drawEntity(int x, int y, int scale, float pitch, float yaw, LivingEntity livingEntity, MatrixStack matrixStack) {
        //rotation
        float h = Float.isNaN(yaw) ? 0f : (float) Math.atan(yaw / 40f);
        float l = Float.isNaN(pitch) ? 0f : (float) Math.atan(pitch / 40f);

        //apply matrix transformers
        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(1f, 1f, -1f);
        matrixStack.scale((float) scale, (float) scale, (float) scale);

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0f);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        quaternion2.conjugate();

        //backup entity variables
        float bodyYaw = livingEntity.bodyYaw;
        float entityYaw = livingEntity.getYaw();
        float entityPitch = livingEntity.getPitch();
        float prevHeadYaw = livingEntity.prevHeadYaw;
        float headYaw = livingEntity.headYaw;

        //apply entity rotation
        livingEntity.bodyYaw = 180f + h * 20f;
        livingEntity.setYaw(180f + h * 40f);
        livingEntity.setPitch(-l * 20f);
        livingEntity.headYaw = livingEntity.getYaw();
        livingEntity.prevHeadYaw = livingEntity.getYaw();

        //setup entity renderer
        RenderSystem.setShaderLights(Vec3f.ZERO, Vec3f.ZERO);
        //DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        boolean renderHitboxes = entityRenderDispatcher.shouldRenderHitboxes();
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.setRotation(quaternion2);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        //render
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0d, -1d, 0d, 0f, 1f, matrixStack, immediate, 0xf000f0));
        immediate.draw();

        //restore entity rendering data
        entityRenderDispatcher.setRenderHitboxes(renderHitboxes);
        entityRenderDispatcher.setRenderShadows(true);

        //restore entity data
        livingEntity.bodyYaw = bodyYaw;
        livingEntity.setYaw(entityYaw);
        livingEntity.setPitch(entityPitch);
        livingEntity.prevHeadYaw = prevHeadYaw;
        livingEntity.headYaw = headYaw;

        //pop matrix
        matrixStack.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
