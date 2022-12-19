package engine.renderEngine;

import engine.renderEngine.shaders.ShaderProgram;

public class PickingShader extends ShaderProgram {

    public PickingShader() {
        super("engineFiles/shaders/util/pickingVertexShader.glsl", "engineFiles/shaders/util/pickingFragmentShader.glsl");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");

//        super.bindAttribute(0, "aPos");
//        super.bindAttribute(1, "aColor");
//        super.bindAttribute(2, "aTexCoords");
//        super.bindAttribute(3, "aTexId");
//        super.bindAttribute(4, "aEntityId");
    }
}
