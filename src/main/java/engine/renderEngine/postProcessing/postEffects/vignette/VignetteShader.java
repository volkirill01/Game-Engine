package engine.renderEngine.postProcessing.postEffects.vignette;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;
import org.joml.Vector2f;

public class VignetteShader extends PostProcessShader {

	private int fboWidth;
	private int fboHeight;

	private boolean isSquared = true;
	private boolean isRounded = true;
	private Vector2f center = new Vector2f(0.5f);
	private float intensity = 1.0f;
	private float radius = 2.0f;
	private float softness = 0.01f;

	public VignetteShader(int fboWidth, int fboHeight) {
		super("Vignette", "engineFiles/shaders/postProcessing/vignette/vignetteFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
		super.start();
		super.loadUniformFloat("targetWidth", this.fboWidth);
		super.loadUniformFloat("targetHeight", this.fboHeight);
		super.stop();
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		isSquared = EditorImGui.field_Boolean("Is Squared", isSquared);

		if (isSquared) {
			intensity = EditorImGui.field_Float("Intensity", intensity, 0.005f, 0.0f, 1.0f);
			radius = EditorImGui.field_Float("Radius", radius, 0.05f, 0.0f, 10.0f);
			softness = EditorImGui.field_Float("Softness", softness, 0.01f, 0.001f, 1.0f);
		} else {
			isRounded = EditorImGui.field_Boolean("Is Rounded", isRounded);

			EditorImGui.field_Vector2f("Center", center, new Vector2f(0.5f));

			intensity = EditorImGui.field_Float("Intensity", intensity, 0.005f, 0.0f, 1.0f);
			radius = EditorImGui.field_Float("Radius", radius, 0.05f, 0.0f, 3.0f);
			softness = EditorImGui.field_Float("Softness", softness, 0.05f, 0.001f, 3.0f);
		}
	}

	@Override
	public void loadVariables() {
		super.loadUniformBoolean("isSquared", this.isSquared);
		super.loadUniformBoolean("isRounded", this.isRounded);
		if (center != null)
			super.loadUniformVector2("center", this.center);
		super.loadUniformFloat("intensity", this.intensity);
		super.loadUniformFloat("radius", this.radius);
		super.loadUniformFloat("softness", this.softness);
	}

	@Override
	public VignetteShader copy() {
		return new VignetteShader(this.fboWidth, this.fboHeight);
	}

	@Override
	public void reset() {
		this.isSquared = true;
		this.isRounded = true;
		this.center = new Vector2f(0.5f);
		this.intensity = 1.0f;
		this.radius = 2.0f;
		this.softness = 0.01f;
	}
}
