package engine.renderEngine.particles;

import engine.renderEngine.shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "engineFiles/shaders/particles/particleVShader.glsl";
	private static final String FRAGMENT_FILE = "engineFiles/shaders/particles/particleFShader.glsl";

	public ParticleShader() { super(VERTEX_FILE, FRAGMENT_FILE); }

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "textureOffsets");
		super.bindAttribute(6, "blendFactor");
	}
}
