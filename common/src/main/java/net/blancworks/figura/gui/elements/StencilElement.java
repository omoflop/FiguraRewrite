package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public abstract class StencilElement extends FiguraGuiElement{
    
    public int stencilLayerID = 1;
    
    public void setupStencilPrep(){
        
        //Stencil fail = keep (never happens)
        //Depth fail = keep
        //Both success = replace with layer ID
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        //Always write the stencil ID for drawing prep phase.
        GlStateManager._stencilFunc(GL11.GL_ALWAYS, stencilLayerID, 255);
    }

    public void setupStencil(){
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL30.glStencilMask(0x00);
        
        //Test against the stencil layer ID.
        GlStateManager._stencilFunc(GL11.GL_EQUAL, stencilLayerID, 255);
    }
    
    public void resetStencil(){
        //Clear stencil buffer now that we're done rendering this stencil element
        //GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT, false);
        
        GlStateManager._enableDepthTest();
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, 255);
        //GlStateManager._stencilMask(0x00);
        GL30.glDisable(GL30.GL_STENCIL_TEST);
    }
}
