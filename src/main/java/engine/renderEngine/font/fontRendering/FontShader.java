package engine.renderEngine.font.fontRendering;

import engine.renderEngine.shaders.ShaderProgram;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "engineFiles/shaders/font/fontVertex.glsl";
	private static final String FRAGMENT_FILE = "engineFiles/shaders/font/fontFragment.glsl";
	
	public FontShader() { super(VERTEX_FILE, FRAGMENT_FILE); }

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
}
