package engine.renderEngine.postProcessing.postEffects.gaussianBlur;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.PostProcessShader;
import imgui.ImGui;

public class GaussianBlurShader extends PostProcessShader {

    private float size = 1.0f;
    private int targetWidth;
    private int targetHeight;
    private float width;
    private float height;

    public GaussianBlurShader(int fboWidth, int fboHeight) {
        super("Gaussian Blur", "engineFiles/shaders/postProcessing/gaussianBlur/blurVertex.glsl",
                "engineFiles/shaders/postProcessing/gaussianBlur/blurFragment.glsl", fboWidth, fboHeight);

        this.targetWidth = fboWidth;
        this.targetHeight = fboHeight;
        this.width = this.targetWidth;
        this.height = this.targetHeight;
    }

    @Override
    public void imgui(boolean isLayerActive, String additionToId) {
        ImGui.pushID("GaussianBlurShader" + additionToId);
        this.size = EditorImGui.field_Float("Blur size", size, 0.05f, 0.05f);
        ImGui.popID();
    }

    @Override
    public PostProcessShader copy() {
        return new GaussianBlurShader(this.targetWidth, this.targetHeight);
    }

    @Override
    public void loadVariables() {
        this.width = (float) this.targetWidth * size;
        this.height = (float) this.targetWidth * size;

        super.loadUniformFloat("targetWidth", this.width);
        super.loadUniformFloat("targetHeight", this.height);
    }

    public void setSize(float newSize) { this.size = newSize; }

    @Override
    public void reset() {
        this.size = 1.0f;
    }
}
