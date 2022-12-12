package engine.renderEngine.postProcessing.postEffects.bloom;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;
import imgui.ImGui;

public class UpsampleShader extends PostProcessShader {

    private float filterRadius = 0.005f;

    public UpsampleShader(int fboWidth, int fboHeight) {
        super("Upsample", "engineFiles/shaders/postProcessing/bloom/samplingVertex.glsl", "engineFiles/shaders/postProcessing/bloom/upsampleFragment.glsl", fboWidth, fboHeight);
    }

    @Override
    public void connectTextures() {
        super.loadUniformInt("srcTexture", 0);
        super.loadUniformInt("previousTexture", 1);
    }

    @Override
    public void loadVariables() {
        super.loadUniformFloat("filterRadius", filterRadius);
    }

    @Override
    public void imgui(boolean isLayerActive, String additionToId) {
        ImGui.pushID("Upsample" + additionToId);
        this.filterRadius = EditorImGui.field_Float("Filter Radius", filterRadius, 0.02f);
        ImGui.popID();
    }

    @Override
    public UpsampleShader copy() {
        return new UpsampleShader(this.getWidth(), this.getHeight());
    }

    @Override
    public void reset() {
        this.filterRadius = 0.005f;
    }
}
