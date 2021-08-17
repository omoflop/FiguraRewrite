package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class CardElement extends StencilElement {

    public static final Identifier BACK_ART = new Identifier("figura", "textures/cards/back.png");
    public static final Identifier VIEWPORT = new Identifier("figura", "textures/cards/viewport.png");
    public static SpriteAtlasTexture EFFECT_ATLAS_TEXTURE;

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

        smoothingFactor = 0.1f;
    }

    public enum CardBackground {
        DEBUG(new Identifier("figura", "textures/cards/backgrounds/debug.png")),
        BLUE(new Identifier("figura", "textures/cards/backgrounds/blue.png")),
        CLOUDS(
                new Identifier("figura", "textures/cards/backgrounds/clouds/background.png"),
                new Identifier("figura", "textures/cards/backgrounds/clouds/moon.png"),
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);

        matrixStack.push();

        setupTransforms(matrixStack);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        try {
            //center rotation
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                setupStencilWrite();

                RenderSystem.setShaderTexture(0, VIEWPORT);
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
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
                drawEntity(32, 48, 30, 0, 0, entity, matrixStack);
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
                matrixStack.translate(64f, 0f, 0f);
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
                drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
            }

            //render effect
            {
                //drawSprite(matrixStack, -16, -16, 0, 96, 128, EFFECT_ATLAS_TEXTURE.getSprite(CardEffects.LINES.id));
            }

            //render texts
            {
                //name
                matrixStack.push();
                matrixStack.translate(3f, 3f, 2f); //3px offset
                String nameString = client.textRenderer.trimToWidth(name.getString(), 59); // 64 - 3 - 2
                drawStringWithShadow(matrixStack, client.textRenderer, nameString, 0, 0, 0xffffff);
                matrixStack.pop();

                //author
                matrixStack.push();
                matrixStack.translate(3f, 11f, 2f); //3px offset + 7px above text + 1px spacing
                matrixStack.scale(0.75f, 0.75f,1f);
                String authorString = client.textRenderer.trimToWidth(author.getString(), 75); //64 + 64 * 0.75 - 3 - 2
                drawStringWithShadow(matrixStack, client.textRenderer, authorString, 0, 0, 0xffffff);
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

    public void renderBackground(MatrixStack matrixStack, CardBackground background) {
        //prepare render
        matrixStack.push();
        matrixStack.translate(-48f, -32f, 0f);

        float strength = 1.5f;
        for (int i = 0; i < background.ids.length; i++, strength -= 0.25f) {
            //prepare background
            RenderSystem.setShaderTexture(0, background.ids[i]);
            matrixStack.push();

            //fake parallax effect - thx wolfy
            float x = MathHelper.clamp(((-this.rotation.getY() * strength) / 90) * 48, -48, 48);
            float y = MathHelper.clamp(((this.rotation.getX() * strength) / 90) * 32, -32, 32);
            matrixStack.translate(x, y, 0);

            //drawTexture(matrices, x, y, x size, y size, u offset, v offset, u size, v size, texture width, texture height)
            drawTexture(matrixStack, 0, 0, 160, 160, 0, 0, 160, 160, 160, 160);

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(64 * scaleCurrent.getX(), 94 * scaleCurrent.getY());
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
