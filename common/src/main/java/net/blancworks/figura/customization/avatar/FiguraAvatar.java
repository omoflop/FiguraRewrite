package net.blancworks.figura.customization.avatar;

import net.blancworks.figura.customization.avatar.model.FiguraModel;
import net.blancworks.figura.customization.avatar.script.FiguraScript;
import net.blancworks.figura.customization.avatar.texture.FiguraTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class FiguraAvatar {
    public FiguraModel model;
    public FiguraTexture texture;
    public FiguraScript script;

    //Vertex consumers that are commonly used by rendering.
    public VertexConsumer mainTextureVC;
    public VertexConsumer mainTextureCullVC;
    public VertexConsumer mainTextureSolidVC;
    public VertexConsumer mainTextureTranslucentVC;
    public VertexConsumer mainTextureTranslucentCullVC;

    /***
     * Called once per frame if whatever this belongs to is being rendered.
     * @param stack The matrix stack for rendering.
     * @param consumerProvider The vertex consumer provider for the current frame.
     */
    public void render(MatrixStack stack, VertexConsumerProvider consumerProvider) {
        //Generate all the vertex consumers we need for the model.
        mainTextureVC = consumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(texture.id));
        mainTextureCullVC = consumerProvider.getBuffer(RenderLayer.getEntityCutout(texture.id));
        mainTextureSolidVC = consumerProvider.getBuffer(RenderLayer.getEntitySolid(texture.id));
        mainTextureTranslucentVC = consumerProvider.getBuffer(RenderLayer.getEntityTranslucent(texture.id));
        mainTextureTranslucentCullVC = consumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(texture.id));

        //Call render on each of the components of the avatar.
        if (texture != null) texture.render();
        if (model != null) model.render(stack);
        if (script != null) script.render();
    }
}
