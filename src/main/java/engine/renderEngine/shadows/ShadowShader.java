package engine.renderEngine.shadows;

import engine.renderEngine.shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "engineFiles/shaders/shadow/shadowVertexShader.glsl";
	private static final String FRAGMENT_FILE = "engineFiles/shaders/shadow/shadowFragmentShader.glsl";

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
	}
}
