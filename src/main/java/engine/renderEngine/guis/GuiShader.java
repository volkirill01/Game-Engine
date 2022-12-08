package engine.renderEngine.guis;

import engine.renderEngine.shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {

    public GuiShader(String VERTEX_FILE, String FRAGMENT_FILE) { super(VERTEX_FILE, FRAGMENT_FILE); }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
