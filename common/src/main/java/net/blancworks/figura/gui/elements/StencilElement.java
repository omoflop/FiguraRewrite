package net.blancworks.figura.gui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class StencilElement extends FiguraGuiElement{
    
    public int stencilLayerID = 1;
    
    public void setupStencilPrep(){
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        //Clear stencil buffer.
        GlStateManager._clear(GL11.GL_STENCIL_BUFFER_BIT, false);

        //Stencil fail = keep (never happens)
        //Depth fail = keep
        //Both success = replace with layer ID
        GlStateManager._stencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        //Always write the stencil ID for drawing prep phase.
        GlStateManager._stencilFunc(GL11.GL_ALWAYS, 100, 255);
    }

    public void setupStencil(){
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        //Test against the stencil layer ID.
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, Integer.MAX_VALUE);
    }
    
    public void resetStencil(){
        //Clear stencil buffer now that we're done rendering this stencil element
        //GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT, false);
        
        GlStateManager._enableDepthTest();
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, Integer.MAX_VALUE);
    }
}
