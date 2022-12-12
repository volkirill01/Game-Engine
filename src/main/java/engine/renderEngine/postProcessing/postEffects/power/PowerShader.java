package engine.renderEngine.postProcessing.postEffects.power;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class PowerShader extends PostProcessShader {

	private float power = 0.03f;
	private int fboWidth;
	private int fboHeight;

	public PowerShader(int fboWidth, int fboHeight) {
		super("Power", "engineFiles/shaders/postProcessing/power/powerFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		power = EditorImGui.field_Float("Power", power, 0.04f);
	}

	@Override
	public void loadVariables() {
		super.loadUniformFloat("power", power);
	}

	@Override
	public PowerShader copy() {
		return new PowerShader(this.fboWidth, this.fboHeight);
	}

	@Override
	public void reset() {
		this.power = 0.03f;
	}
}
