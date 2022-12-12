package engine.renderEngine.postProcessing.postEffects.startPostProcess;

import engine.renderEngine.postProcessing.PostProcessShader;

public class StartPostProcessShader extends PostProcessShader {

	private float gamma = 2.2f;
	private float exposure = 0.5f;

	public StartPostProcessShader(int fboWidth, int fboHeight) {
		super("StartPostProcess", "engineFiles/shaders/postProcessing/startPostProcess/startPostProcessFragment.glsl", fboWidth, fboHeight);
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) { }

	@Override
	public void loadVariables() {
		super.loadUniformFloat("gamma", gamma);
		super.loadUniformFloat("exposure", exposure);
	}

	@Override
	public StartPostProcessShader copy() {
		return new StartPostProcessShader(super.getWidth(), super.getHeight());
	}

	@Override
	public void reset() {
		this.gamma = 2.2f;
		this.exposure = 0.5f;
	}
}
