package engine.renderEngine.postProcessing.postEffects.bloom;

import engine.imGui.EditorImGui;
import engine.renderEngine.postProcessing.ImageRenderer;
import engine.renderEngine.postProcessing.PostProcessLayer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class BloomLayer extends PostProcessLayer {

    private int targetWidth;
    private int targetHeight;

    boolean showBloomMask = false;

    private int startTexture;
    int previewLayer = 8;

    List<DownsampleShader> downsampleShaders = new ArrayList<>();
    List<UpsampleShader> upsampleShaders = new ArrayList<>();

    private List<ImageRenderer> downsampleRenderers = new ArrayList<>();
    private List<ImageRenderer> upsampleRenderers = new ArrayList<>();

    CompositingShader compositingShader;
    private ImageRenderer compositingRenderer;

    public BloomLayer(int targetFboWidth, int targetFboHeight) {
        this.targetWidth = targetFboWidth;
        this.targetHeight = targetFboHeight;

        // first stage (prefilter)
        this.shader = new PrefilterShader(targetFboWidth, targetFboHeight);
        this.shader.start();
        this.shader.connectTextures();
        this.shader.stop();

        this.renderer = new ImageRenderer(shader.getWidth(), shader.getHeight());

        // second stage (blur)
        generateShaders(2);
        generateShaders(4);
        generateShaders(8);
//        generateShaders(16);

        // third stage (compositing)
        this.compositingShader = new CompositingShader(targetFboWidth, targetFboHeight);
        this.compositingRenderer = new ImageRenderer(compositingShader.getWidth(), compositingShader.getHeight());
    }

    private void generateShaders(int index) {
        // second stage (downsample)
        DownsampleShader downsampleShader = new DownsampleShader(this.targetWidth / index, this.targetHeight / index);
        downsampleShader.start();
        downsampleShader.connectTextures();
        downsampleShader.stop();

        this.downsampleShaders.add(downsampleShader);

        // third stage (upsample)
        UpsampleShader upsampleShader = new UpsampleShader(this.targetWidth / (index - 1), this.targetHeight / (index - 1));
        upsampleShader.start();
        upsampleShader.connectTextures();
        upsampleShader.stop();

        this.upsampleShaders.add(upsampleShader);

        generateRenderers(index);
    }

    private void generateRenderers(int index) {
        ImageRenderer downsampleRenderer = new ImageRenderer(this.targetWidth / index, this.targetHeight / index);
        this.downsampleRenderers.add(downsampleRenderer);
        ImageRenderer upsampleRenderer = new ImageRenderer(this.targetWidth / (index - 1), this.targetHeight / (index - 1));
        this.upsampleRenderers.add(upsampleRenderer);
    }

    @Override
    public void render(int texture) {
        this.startTexture = texture;

        this.shader.start();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        this.renderer.renderQuad();
        this.shader.stop();

        if (this.showBloomMask)
            return;

        int lastImage = renderer.getOutputTexture();

        for (int i = 0; i < downsampleRenderers.size(); i++) {
            this.downsampleShaders.get(i).start();
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, lastImage);
            this.downsampleRenderers.get(i).renderQuad();
            this.downsampleShaders.get(i).stop();

            lastImage = this.downsampleRenderers.get(i).getOutputTexture();
        }

        int previousImage;

        for (int i = 0; i < upsampleRenderers.size(); i++) {
            previousImage = lastImage;

            this.upsampleShaders.get(i).start();
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, lastImage);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, previousImage);
            this.upsampleRenderers.get(i).renderQuad();
            this.upsampleShaders.get(i).stop();

            lastImage = this.upsampleRenderers.get(i).getOutputTexture();
        }

        compose(texture, lastImage);
    }

    private void compose(int originalTexture, int bloomTexture) {
        compositingShader.start();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, originalTexture);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, bloomTexture);

        compositingShader.connectTextures();

        compositingRenderer.renderQuad();
        compositingShader.stop();
    }

    @Override
    public void loadVariables() {
        this.shader.start();
        this.shader.loadVariables();
        this.shader.stop();

        for (int i = 0; i < upsampleShaders.size(); i++) {
            this.downsampleShaders.get(i).start();
            this.downsampleShaders.get(i).loadVariables();
            this.downsampleShaders.get(i).stop();

            this.upsampleShaders.get(i).start();
            this.upsampleShaders.get(i).loadVariables();
            this.upsampleShaders.get(i).stop();
        }

        this.compositingShader.start();
        this.compositingShader.loadVariables();
        this.compositingShader.stop();
    }

    @Override
    public int getOutputTexture() {
        if (showBloomMask)
            return this.renderer.getOutputTexture();  // Prefilter

        return switch (previewLayer) {
            case 0 -> this.startTexture;                                    // Start texture
            case 1 -> this.renderer.getOutputTexture();                     // Prefilter

            case 2 -> this.downsampleRenderers.get(0).getOutputTexture();   // Blur
            case 3 -> this.downsampleRenderers.get(1).getOutputTexture();   // Blur
            case 4 -> this.downsampleRenderers.get(2).getOutputTexture();   // Blur
//            case 5 -> this.downsampleRenderers.get(3).getOutputTexture();   // Blur
            case 5 -> this.upsampleRenderers.get(0).getOutputTexture();     // Blur
            case 6 -> this.upsampleRenderers.get(1).getOutputTexture();     // Blur
            case 7 -> this.upsampleRenderers.get(2).getOutputTexture();     // Blur
//            case 9 -> this.upsampleRenderers.get(3).getOutputTexture();    // Blur

            case 8 -> this.compositingRenderer.getOutputTexture();         // Compositing
            default -> throw new IllegalStateException("Unexpected value: " + previewLayer);
        };

//        return this.compositingRenderer.getOutputTexture();
    }

    @Override
    public String getPostEffectName() { return "Bloom"; }

    @Override
    public void cleanUp() {
        this.renderer.cleanUp();
        this.shader.cleanUp();

        for (int i = 0; i < downsampleRenderers.size(); i++) {
            this.downsampleRenderers.get(i).cleanUp();
            this.downsampleShaders.get(i).cleanUp();

            this.upsampleRenderers.get(i).cleanUp();
            this.upsampleShaders.get(i).cleanUp();
        }

        this.compositingRenderer.cleanUp();
        this.compositingShader.cleanUp();
    }

    @Override
    public BloomLayer copy() { return new BloomLayer(this.shader.getWidth(), this.shader.getHeight()) {
        @Override
        public String getPostEffectName() { return "Bloom"; }

        @Override
        public void imgui(boolean isActive, String additionToId) {
            if (EditorImGui.checkbox("Show Bloom Mask", this.showBloomMask))
                this.showBloomMask = !this.showBloomMask;

            this.previewLayer = EditorImGui.inputInt("Preview Layer Index", this.previewLayer, 1, 0, 2 + downsampleShaders.size() + upsampleShaders.size());

            this.shader.imgui(isActive, additionToId);

//            for (int i = 0; i < upsampleRenderers.size(); i++) {
//                this.downsampleShaders.get(i).imgui(isActive, "Down" + i);
//                this.upsampleShaders.get(i).imgui(isActive, "Up" + i);
//            }

            this.compositingShader.imgui(isActive, additionToId);
        }
    }; }
}
