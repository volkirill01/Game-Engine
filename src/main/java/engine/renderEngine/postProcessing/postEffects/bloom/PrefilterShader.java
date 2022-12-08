package engine.renderEngine.postProcessing.postEffects.bloom;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class PrefilterShader extends PostProcessShader {

	private float threshold = 1.035f;

	public PrefilterShader(int fboWidth, int fboHeight) {
		super("Prefilter", "engineFiles/shaders/postProcessing/bloom/prefilterFragment.glsl", fboWidth, fboHeight);
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		threshold = EditorImGui.dragFloat("threshold", threshold);
	}

	@Override
	public void loadVariables() {
		super.loadUniformFloat("threshold", threshold);
	}

	@Override
	public PrefilterShader copy() {
		return new PrefilterShader(super.getWidth(), super.getHeight());
	}
}
