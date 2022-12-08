package engine.renderEngine.postProcessing.postEffects.brightnessContrastSaturation;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class BrightnessContrastSaturationShader extends PostProcessShader {

	private int fboWidth;
	private int fboHeight;

	private float brightness = 0.0f;
	private float contrast = 1.0f;
	private float saturation = 1.0f;

	public BrightnessContrastSaturationShader(int fboWidth, int fboHeight) {
		super("Brightness Contrast Saturation", "engineFiles/shaders/postProcessing/brightnessContrastSaturation/brightnessContrastSaturationFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		brightness = EditorImGui.dragFloat("Brightness", brightness, 0.005f);
		contrast = EditorImGui.dragFloat("Contrast", contrast, 0.005f);
		saturation = EditorImGui.dragFloat("Saturation", saturation, 0.1f, 0.0f);
	}

	@Override
	public void loadVariables() {
		super.loadUniformFloat("brightness", this.brightness);
		super.loadUniformFloat("contrast", this.contrast);
		super.loadUniformFloat("saturation", this.saturation);
	}

	@Override
	public BrightnessContrastSaturationShader copy() {
		return new BrightnessContrastSaturationShader(this.fboWidth, this.fboHeight);
	}
}
