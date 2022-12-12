package engine.renderEngine.postProcessing;

import engine.renderEngine.shaders.ShaderProgram;

public abstract class PostProcessShader extends ShaderProgram {

    private String postEffectName;
    private int fboWidth;
    private int fboHeight;

    public PostProcessShader(String postEffectName, String vertexShader, String fragmentShader, int fboWidth, int fboHeight) {
        super(vertexShader, fragmentShader);
        this.postEffectName = postEffectName;

        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
        loadVariables();
    }

    public PostProcessShader(String postEffectName, String fragmentShader, int fboWidth, int fboHeight) {
        super(simpleVertexShader, fragmentShader);
        this.postEffectName = postEffectName;

        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
        loadVariables();
    }

    public PostProcessShader(String postEffectName, int fboWidth, int fboHeight) {
        this.postEffectName = postEffectName;
        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
    }

    public void connectTextures() {
        super.loadUniformInt("colourTexture", 0);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public int getWidth() { return this.fboWidth; }

    public int getHeight() { return this.fboHeight; }

    public String getPostEffectName() { return this.postEffectName; }

    public abstract void loadVariables();

    public abstract void imgui(boolean isLayerActive, String additionToId);

    public abstract PostProcessShader copy();

    public abstract void reset();
}
