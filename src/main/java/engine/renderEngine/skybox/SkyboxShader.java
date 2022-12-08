package engine.renderEngine.skybox;

import engine.renderEngine.shaders.ShaderProgram;

public class SkyboxShader extends ShaderProgram {

    public SkyboxShader(String VERTEX_FILE, String FRAGMENT_FILE) { super(VERTEX_FILE, FRAGMENT_FILE); }

    @Override
    protected void bindAttributes() { super.bindAttribute(0, "position"); }
}
