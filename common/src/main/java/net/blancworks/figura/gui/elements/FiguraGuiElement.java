package net.blancworks.figura.gui.elements;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public abstract class FiguraGuiElement extends DrawableHelper {
    public Vector2f position = new Vector2f(0, 0);
    public Vec3f rotation = new Vec3f(0, 0, 0);
    public Vector2f scale = new Vector2f(1,1);

    protected Vec3f rotationCurrent = new Vec3f(0, 0, 0);
    protected Vector2f positionCurrent = new Vector2f(0, 0);
    protected Vector2f scaleCurrent = new Vector2f(1,1);

    public float smoothingFactor = 1;
    
    //Arbitrary boolean that can be used by other things to determine if the card is "ready" to be displayed.
    public boolean isReady = false;

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta){
        positionCurrent = new Vector2f(
                MathHelper.lerp(smoothingFactor, positionCurrent.getX(), position.getX()),
                MathHelper.lerp(smoothingFactor, positionCurrent.getY(), position.getY())
        );

        rotationCurrent = new Vec3f(
                MathHelper.lerp(smoothingFactor,rotationCurrent.getX(), rotation.getX()),
                MathHelper.lerp(smoothingFactor,rotationCurrent.getY(), rotation.getY()),
                MathHelper.lerp(smoothingFactor,rotationCurrent.getZ(), rotation.getZ())
        );

        scaleCurrent = new Vector2f(
                MathHelper.lerp(smoothingFactor,scaleCurrent.getX(), scale.getX()),
                MathHelper.lerp(smoothingFactor,scaleCurrent.getY(), scale.getY())
        );
    }

    public void tick() {
        
    }

    public abstract Vector2f getSize();

    public void setupTransforms(MatrixStack stack) {
        stack.translate(positionCurrent.getX(), positionCurrent.getY(), 0);
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationCurrent.getX()));
        stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationCurrent.getY()));
        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationCurrent.getZ()));
        stack.scale(scaleCurrent.getX(), scaleCurrent.getY(), 1);
    }
    
    public void setTransformsToTarget(){
        positionCurrent = position;
        rotationCurrent = rotation;
        scaleCurrent = scale;
    }
}
