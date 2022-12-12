package engine.renderEngine.postProcessing.postEffects.tonemapping;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;
import imgui.type.ImInt;
import org.jetbrains.annotations.NotNull;

public class TonemappingShader extends PostProcessShader {

	private String[] tonemapMethods = new String[]{
		"Aces",
		"Filmic",
		"Lottes",
		"Reinhard",
		"Reinhard 2",
		"Uchimura",
		"Uncharted 2",
		"Unreal"
	};

//	private int tonemapIndex = 0;
	private ImInt currentTonemapMethod = new ImInt(0);
	private int fboWidth;
	private int fboHeight;

	public TonemappingShader(int fboWidth, int fboHeight) {
		super("Tonemapping", "engineFiles/shaders/postProcessing/tonemapping/tenemappingFragment.glsl", fboWidth, fboHeight);
		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;
	}

	@Override
	public void imgui(boolean isLayerActive, String additionToId) {
		EditorImGui.enumCombo("ToneMapping Method", currentTonemapMethod, tonemapMethods, tonemapMethods.length);}

	@Override
	public void loadVariables() {
		if (this.currentTonemapMethod != null)
			super.loadUniformInt("tonemappingIndex", currentTonemapMethod.get());
	}

	@Override
	public TonemappingShader copy() {
		return new TonemappingShader(this.fboWidth, this.fboHeight);
	}

	@Override
	public void reset() {
		this.currentTonemapMethod.set(0);
	}
}
