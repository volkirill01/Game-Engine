package engine.renderEngine.postProcessing.postEffects.bloom;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;

public class CompositingShader extends PostProcessShader {

    private float bloomIntensity = 0.3f;

    public CompositingShader(int fboWidth, int fboHeight) {
        super("Compositing", "engineFiles/shaders/postProcessing/bloom/compositingFragment.glsl", fboWidth, fboHeight);
    }

    @Override
    public void connectTextures() {
        super.loadUniformInt("colourTexture", 0);
        super.loadUniformInt("bloomTexture", 1);
    }

    @Override
    public void loadVariables() {
        super.loadUniformFloat("bloomIntensity", bloomIntensity);
    }

    @Override
    public void imgui(boolean isLayerActive, String additionToId) {
        this.bloomIntensity = EditorImGui.field_Float("Bloom Intensity", bloomIntensity, 0.02f, 0);
    }

    @Override
    public CompositingShader copy() {
        return new CompositingShader(this.getWidth(), this.getHeight());
    }

    @Override
    public void reset() {
        this.bloomIntensity = 0.3f;
    }
}
