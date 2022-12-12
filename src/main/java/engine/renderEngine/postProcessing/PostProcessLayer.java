package engine.renderEngine.postProcessing;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessLayer {

    protected PostProcessShader shader;
    private boolean isActive = true;

    protected ImageRenderer renderer;

    public PostProcessLayer() { }

    public PostProcessLayer(PostProcessShader shader) {
        this.shader = shader;
        this.shader.start();
        this.shader.connectTextures();
        this.shader.stop();
        this.renderer = new ImageRenderer(shader.getWidth(), shader.getHeight());
    }

    public void render(int texture) {
        this.shader.start();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        this.renderer.renderQuad();
        this.shader.stop();
    }

    public void loadVariables() {
        this.shader.start();
        this.shader.loadVariables();
        this.shader.stop();
    }

    public int getOutputTexture() { return this.renderer.getOutputTexture(); }

    public String getPostEffectName() { return this.shader.getPostEffectName(); }

    public void imgui(boolean isActive, String additionToId) { this.shader.imgui(isActive, additionToId); }

    public void cleanUp() {
        this.renderer.cleanUp();
        this.shader.cleanUp();
    }

//    public void start() { this.shader.start(); }
//
//    public void stop() { this.shader.stop(); }
//
//    public PostProcessShader getShader() { return this.shader; }

    public boolean isActive() { return this.isActive; }

    public void setActive(boolean active) { this.isActive = active; }

    public PostProcessLayer copy() { return new PostProcessLayer(this.shader.copy()); }

    public void reset() {
        this.shader.reset();
    }
}
