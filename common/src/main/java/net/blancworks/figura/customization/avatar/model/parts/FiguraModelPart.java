package net.blancworks.figura.customization.avatar.model.parts;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.blancworks.figura.customization.avatar.model.FiguraModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * The FiguraModelPart class represents a single "part" of a model, which is more or less a collection of
 * vertices to be pushed into the render pipeline.
 * <p>
 * Vertices for model parts are comprised of 3 components:
 * Position (float3)
 * UV (float2)
 * Color (float4)
 * <p>
 * Vertices are compiled into a quickly-accessible FloatArrayList so that rendering can be as fast as possible.
 */
public class FiguraModelPart {

    /***
     * The model that this part belongs to.
     */
    private FiguraModel parentModel;

    /***
     * The part that this part is parented to, null if it's a root part.
     */
    private FiguraModelPart parentPart;

    /***
     * List of all sub-parts for this part.
     */
    private List<FiguraModelPart> children = new ArrayList<>();
    

    private FloatArrayList vertexData = new FloatArrayList();

    /***
     * Pushes the vertices for the current part.
     * @param stack The Matrix Stack for the part.
     */
    public void render(MatrixStack stack) {
        if (vertexData == null) return;
        

        //Apply transforms
        
        //Get final matrix
        Matrix4f modelMatrix = stack.peek().getModel();
        Matrix3f normalMatrix = stack.peek().getNormal();

        
        //Render this part.
        //9 floats = 1 vert
        //4 verts = 1 face
        int faceCount = (int)Math.floor((float)vertexData.size() / 9.0f / 4.0f);

        int vertDataIndex = 0;
        
        //We mainly have this double-for loop set up so that we don't waste time rendering incomplete faces, and it's
        //easy to read. We get the the complete face count, then only loop over the verts for those faces.
        for(int face = 0; face < faceCount; face++){
            for(int vert = 0; vert < 4; vert++){
                
                float x = vertexData.getFloat(vertDataIndex++);
                float y = vertexData.getFloat(vertDataIndex++);
                float z = vertexData.getFloat(vertDataIndex++);

                float u = vertexData.getFloat(vertDataIndex++);
                float v = vertexData.getFloat(vertDataIndex++);

                float r = vertexData.getFloat(vertDataIndex++);
                float g = vertexData.getFloat(vertDataIndex++);
                float b = vertexData.getFloat(vertDataIndex++);
                float a = vertexData.getFloat(vertDataIndex++);

                //Transform vertex.
                Vector4f pos = new Vector4f(x,y,z,1);
                pos.transform(modelMatrix);
                
                
                
            }
        }

        //Render children.
        for (FiguraModelPart child : children) {
            child.render(stack);
        }
    }


    //----Vertex data----
    /***
     * Adds a vertex to the part.
     * Note that parts only ever render in sets of 4 vertices, and will skip partially-complete faces.
     */
    public void addVertex(Vec3f position, float u, float v, Vector4f color) {
        vertexData.add(position.getX());
        vertexData.add(position.getY());
        vertexData.add(position.getZ());

        vertexData.add(u);
        vertexData.add(v);

        vertexData.add(color.getX());
        vertexData.add(color.getY());
        vertexData.add(color.getZ());
        vertexData.add(color.getW());

    }
    
    public void clearVertices(){
        vertexData.clear();
    }

    //----Field Accessors----


    //Parent model accessors
    public void setParentModel(FiguraModel model){
        this.parentModel = model;

        for (FiguraModelPart child : children) {
            child.setParentModel(model);
        }
    }
    
    public FiguraModel getParentModel(){
        return parentModel;
    }
    
    
    //Parent part accessors
    public void setParentPart(FiguraModelPart part){
        parentPart = part;
    }
    
    public FiguraModelPart getParentPart(){
        return parentPart;
    }
    
}
