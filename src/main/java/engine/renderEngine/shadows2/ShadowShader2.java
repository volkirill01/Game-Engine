package engine.renderEngine.shadows2;

import engine.renderEngine.shaders.ShaderProgram;

public class ShadowShader2 extends ShaderProgram {

    public ShadowShader2() { super("engineFiles/shaders/shadows2/shadow2VertexShader.glsl", "engineFiles/shaders/shadows2/shadow2FragmentShader.glsl"); }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }

    @Override
    public void getAllUniforms() { }
}
