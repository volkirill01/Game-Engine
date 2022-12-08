package engine.renderEngine.postProcessing.postEffects.bloom;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;
import imgui.ImGui;
import org.joml.Vector2f;

public class DownsampleShader extends PostProcessShader {

    private float resolutionMultiplayer = 2.0f;

    public DownsampleShader(int fboWidth, int fboHeight) {
        super("Downsample", "engineFiles/shaders/postProcessing/bloom/samplingVertex.glsl", "engineFiles/shaders/postProcessing/bloom/downsampleFragment.glsl", fboWidth, fboHeight);
    }

    @Override
    public void connectTextures() {
        super.loadUniformInt("srcTexture", 0);
    }

    @Override
    public void loadVariables() {
        if (this.getWidth() > 0 && this.getHeight() > 0 && this.resolutionMultiplayer > 0)
            super.loadUniformVector2("srcResolution", new Vector2f(this.getWidth() / resolutionMultiplayer, this.getHeight() / resolutionMultiplayer));
    }

    @Override
    public void imgui(boolean isLayerActive, String additionToId) {
        ImGui.pushID("Downsample" + additionToId);
        this.resolutionMultiplayer = EditorImGui.dragFloat("Resolution Multiplayer", resolutionMultiplayer, 0.02f);
        ImGui.popID();
    }

    @Override
    public DownsampleShader copy() {
        return new DownsampleShader(this.getWidth(), this.getHeight());
    }
}
