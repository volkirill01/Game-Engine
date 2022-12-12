package engine.renderEngine.postProcessing.postEffects.gaussianBlur;

import engine.renderEngine.postProcessing.ImageRenderer;
import engine.renderEngine.postProcessing.PostProcessLayer;
import engine.renderEngine.postProcessing.postEffects.bloom.BloomLayer;

import static org.lwjgl.opengl.GL30.*;

public class GaussianBlurLayer extends PostProcessLayer {

    private ImageRenderer renderer_horizontal;
    private ImageRenderer renderer_vertical;

    public GaussianBlurLayer(int targetFboWidth, int targetFboHeight) {
        this.shader = new GaussianBlurShader(targetFboWidth, targetFboHeight);
        this.shader.start();
        this.shader.connectTextures();
        this.shader.stop();

        this.renderer_horizontal = new ImageRenderer(shader.getWidth(), shader.getHeight());
        this.renderer_vertical = new ImageRenderer(shader.getWidth(), shader.getHeight());
    }

    @Override
    public void render(int texture) {
        this.shader.start();
        this.shader.loadUniformBoolean("isVertical", false);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        this.renderer_horizontal.renderQuad();

        this.shader.loadUniformBoolean("isVertical", true);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.renderer_horizontal.getOutputTexture());
        this.renderer_vertical.renderQuad();
        this.shader.stop();
    }

    @Override
    public void loadVariables() {
        this.shader.loadVariables();
    }

    @Override
    public int getOutputTexture() { return this.renderer_vertical.getOutputTexture(); }

    @Override
    public void cleanUp() {
        this.renderer_horizontal.cleanUp();
        this.renderer_vertical.cleanUp();
        this.shader.cleanUp();
    }

    @Override
    public BloomLayer copy() { return new BloomLayer(this.shader.getWidth(), this.shader.getHeight()) {
        @Override
        public String getPostEffectName() { return "Blur"; }
    }; }

//    @Override
//    public PostProcessShader getShader() { return this.shader; }
}
