package net.blancworks.figura.customization.avatar.model;

import net.blancworks.figura.customization.avatar.FiguraAvatar;
import net.blancworks.figura.customization.avatar.model.parts.FiguraModelPart;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/***
 * FiguraModels are more or less a mimic of vanilla models with some extra functionality like tracking complexity,
 * and supporting custom parent states.
 * 
 * Also, instead of a FiguraModel being rendered multiple times for different layers, FiguraModels will render to all
 * layers in one iteration.
 */
public class FiguraModel {

    /***
     * This is the list of root parts for the model.
     */
    private List<FiguraModelPart> modelPartList = new ArrayList<>();

    /***
     * The avatar that this model belongs to.
     */
    public FiguraAvatar parentAvatar;
    
    /***
     * Renders the model using the provided data.
     * @param stack The Matrix Stack for the render operation.
     */
    public void render(MatrixStack stack){
        for (FiguraModelPart part : modelPartList) {
            part.render(stack);
        }
    }

    /***
     * Adds a given model part to this model
     * @param part The part to add to the model.
     */
    public void addPart(FiguraModelPart part){
        part.setParentModel(this);
        
        modelPartList.add(part);
    }

    /***
     * Removes the first occurance of a part in the model.
     * @param part The part to remove
     * @return True if an instance was removed, false otherwise.
     */
    public boolean removePart(FiguraModelPart part){
        return modelPartList.remove(part);
    }
}
