package engine.renderEngine.postProcessing.postEffects.whiteBalance;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class WhiteBalanceShader extends PostProcessShader {

	private float temperature = 0;
	private float tint = 0;

	private int fboWidth;
	private int fboHeight;

	public WhiteBalanceShader(int fboWidth, int fboHeight) {
		super("White Balance", "engineFiles/shaders/postProcessing/whiteBalance/whiteBalanceFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		temperature = EditorImGui.dragFloat("Temperature", temperature, 0.05f, -1.7f, 1.7f);
		tint = EditorImGui.dragFloat("Tint", tint, 0.05f, -1.7f, 1.7f);
	}

	@Override
	public void loadVariables() {
		super.loadUniformFloat("temperature", temperature);
		super.loadUniformFloat("tint", tint);
	}

	@Override
	public WhiteBalanceShader copy() {
		return new WhiteBalanceShader(this.fboWidth, this.fboHeight);
	}
}
