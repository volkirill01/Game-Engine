package engine.renderEngine.postProcessing.postEffects.tonemapping;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class TonemappingShader extends PostProcessShader {

	private int tonemapIndex = 0;
	private int fboWidth;
	private int fboHeight;

	public TonemappingShader(int fboWidth, int fboHeight) {
		super("Tonemapping", "engineFiles/shaders/postProcessing/tonemapping/tenemappingFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		tonemapIndex = EditorImGui.inputInt("Tonemap index", tonemapIndex, 1, 0, 7);
	}

	@Override
	public void loadVariables() {
		super.loadUniformInt("tonemappingIndex", tonemapIndex);
	}

	@Override
	public TonemappingShader copy() {
		return new TonemappingShader(this.fboWidth, this.fboHeight);
	}
}
