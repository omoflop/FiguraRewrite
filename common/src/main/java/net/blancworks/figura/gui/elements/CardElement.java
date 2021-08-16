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

    public static final Identifier BACK_ART = new Identifier("figura", "textures/cards/back.png");

    public final CardBackground background;
    public final Identifier backgroundOverlay = new Identifier("figura", "textures/cards/background_overlay.png");
    public final Text name;
    public final Text author;
    public final LivingEntity entity;

    public MinecraftClient client;

    public CardElement(CardBackground background, Text name, Text author, LivingEntity entity, int stencilLayerID) {
        this.background = background;
        this.name = name;
        this.author = author;
        this.entity = entity;

        this.client = MinecraftClient.getInstance();
        this.stencilLayerID = stencilLayerID;
    }

    public enum CardBackground {
        DEBUG(new Identifier("figura", "textures/cards/backgrounds/debug.png")),
        BLUE(new Identifier("figura", "textures/cards/backgrounds/blue.png")),
        CLOUDS(
                new Identifier("figura", "textures/cards/backgrounds/clouds/background.png"),
                new Identifier("figura", "textures/cards/backgrounds/clouds/stars.png"),
                new Identifier("figura", "textures/cards/backgrounds/clouds/clouds.png")
        ),
        FADE(new Identifier("figura", "textures/cards/backgrounds/fade.png")),
        FLAMES(new Identifier("figura", "textures/cards/backgrounds/flames.png")),
        SPACE(
                new Identifier("figura", "textures/cards/backgrounds/space/background.png"),
                new Identifier("figura", "textures/cards/backgrounds/space/stars.png")
        );

        public final Identifier[] ids;
        CardBackground(Identifier... ids) {
            this.ids = ids;
        }
    }

    public enum CardEffects {
        LINES(new Identifier("figura", "lines"), new Identifier("figura", "textures/cards/effects/lines.png"));

        public final Identifier id;
        public final Identifier texture;
        CardEffects(Identifier id, Identifier texture) {
            this.id = id;
            this.texture = texture;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {

        matrixStack.push();

        float mouseX = (float) MinecraftClient.getInstance().mouse.getX();
        float mouseY = (float) MinecraftClient.getInstance().mouse.getY();
        mouseX -= MinecraftClient.getInstance().getWindow().getWidth() / 2.0f;
        mouseY -= MinecraftClient.getInstance().getWindow().getHeight() / 2.0f;

        matrixStack.translate(50, 150, 0);

        Vec2f rotation = new Vec2f(((-mouseY / MinecraftClient.getInstance().getWindow().getHeight()) * 150), (-mouseX / MinecraftClient.getInstance().getWindow().getWidth()) * 150);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotation.x));
        matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(rotation.y));

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        try {
            //center rotation
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                setupStencilWrite();

                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                bufferBuilder.vertex(matrixStack.peek().getModel(),  2, 94, 0).color(0xff, 0x72, 0xb7, 0xff).texture(0, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 62, 94, 0).color(0xff, 0x72, 0xb7, 0xff).texture(1, 1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 62,  2, 0).color(0xff, 0x72, 0xb7, 0xff).texture(1, 0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(),  2,  2, 0).color(0xff, 0x72, 0xb7, 0xff).texture(0, 0).next();

                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }

            //From here on out, we aren't allowed to draw pixels outside the viewport we created above ^
            setupStencilTest();

            //background
            renderBackground(matrixStack, background);

            //render model
            {
                RenderSystem.enableDepthTest();

                matrixStack.push();
                matrixStack.translate(0, 0, -15);
                drawEntity(32, 48, 30, rotation.x, rotation.y, entity, matrixStack);
                matrixStack.pop();

                RenderSystem.disableDepthTest();
            }

            //After this point, the stencil buffer is *effectively* turned off.
            //No values will be written to the stencil buffer, and all objects will render
            //regardless of what's in the buffer.
            resetStencilState();

            //render back art
            {
                RenderSystem.setShaderTexture(0, BACK_ART);

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
                RenderSystem.setShaderTexture(0, backgroundOverlay);

                matrixStack.push();
                //drawTexture(matrices, x, y, x size, y size, u offset, v offset, u size, v size, texture width, texture height)
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
            }

            //render texts
            {
                //name
                matrixStack.push();
                matrixStack.translate(3f, 3f, 2f); //3px offset
                drawTextWithShadow(matrixStack, client.textRenderer, name, 0, 0, 0xffffff);
                matrixStack.pop();

                //author
                matrixStack.push();
                matrixStack.translate(3f, 11f, 2f); //3px offset + 7px above text + 1px spacing
                matrixStack.scale(0.75f, 0.75f,1f);
                drawTextWithShadow(matrixStack, client.textRenderer, author, 0, 0, 0xffffff);
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

    public static void renderBackground(MatrixStack matrixStack, CardBackground background) {
        //prepare render
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        matrixStack.push();
        matrixStack.translate(32f, 48f, 0f);
        float scale = 1.5f;

        for (int i = 0; i < background.ids.length; i++, scale -= 0.15f) {
            //prepare background
            RenderSystem.setShaderTexture(0, background.ids[i]);
            matrixStack.push();
            matrixStack.translate(-32f * scale, -48f * scale, -64f * scale);
            matrixStack.scale(scale, scale, scale);

            //back
            drawTexture(matrixStack, 0, 0, 64, 96, 64, 64, 64, 96, 192, 160);

            //left
            matrixStack.push();
            matrixStack.translate(0f, 0f, 64f);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90f));
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 64, 64, 96, 192, 160);
            matrixStack.pop();

            //right
            matrixStack.push();
            matrixStack.translate(64f, 0f, 0f);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90f));
            drawTexture(matrixStack, 0, 0, 64, 96, 128, 64, 64, 96, 192, 160);
            matrixStack.pop();

            //top
            matrixStack.push();
            matrixStack.translate(0f, 0f, 64f);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));
            drawTexture(matrixStack, 0, 0, 64, 64, 0, 0, 64, 64, 192, 160);
            matrixStack.pop();

            //bottom
            matrixStack.push();
            matrixStack.translate(0f, 96f, 0f);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f));
            drawTexture(matrixStack, 0, 0, 64, 64, 64, 0, 64, 64, 192, 160);
            matrixStack.pop();

            matrixStack.pop();
        }

        matrixStack.pop();
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
