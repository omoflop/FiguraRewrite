package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CardElement extends StencilElement {

    public Identifier textureID = new Identifier("textures/misc/forcefield.png");
    
    public CardElement(){
        
        
        
    }
    

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {


        try {
            
            stencilLayerID = 35;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, textureID);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
            
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

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            {
                setupStencilPrep();
                MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                
                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 150, 5).color(255,0,0,255).texture(0,1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 90, 150, 5).color(255,0,0,255).texture(1,1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 90, 10, 5).color(255,0,0,255).texture(1,0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 10, 5).color(255,0,0,255).texture(0,0).next();
                
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }

            //Draw the card art here
            {
                setupStencil();
                MinecraftClient.getInstance().getTextureManager().bindTexture(textureID);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 150, 7).color(0,255,0,255).texture(0,1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 150, 150, 7).color(0,255,0,255).texture(1,1).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 150, 10, 7).color(0,255,0,255).texture(1,0).next();
                bufferBuilder.vertex(matrixStack.peek().getModel(), 10, 10, 7).color(0,255,0,255).texture(0,0).next();
                
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resetStencil();
    }

    @Override
    public void tick() {

    }

    
}
