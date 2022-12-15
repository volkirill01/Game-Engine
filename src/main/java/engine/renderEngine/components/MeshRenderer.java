package engine.renderEngine.components;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.TextureSliceMode;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class MeshRenderer extends ObjectRenderer {

    private TexturedModel model;
    private int textureIndex;

    public MeshRenderer(TexturedModel model) {
        this.model = model;
        this.textureIndex = 1;
    }

    public MeshRenderer(TexturedModel model, int textureIndex) {
        this.model = model;
        this.textureIndex = textureIndex;
    }

    public Vector2f getTextureOffset() {
        int column = textureIndex % model.getMaterial().getNumberOfRows();
        int row = textureIndex / model.getMaterial().getNumberOfColumns();

        return new Vector2f(
                (float) column / (float) model.getMaterial().getNumberOfRows(),
                (float) row / (float) model.getMaterial().getNumberOfColumns());
    }

    public TexturedModel getModel() { return this.model; }

    public void setModel(TexturedModel model) { this.model = model; }

    @Override
    public void start() { }

    @Override
    public void update() { }

    @Override
    public void editorUpdate() { this.model.getRawModel().update(); }

    @Override
    public void imgui() {
        if (this.model != null)
            if (this.model.getMaterial().getTexture().getSliceMode() == TextureSliceMode.Multiple)
                this.textureIndex = EditorImGui.field_Int_WithButtons("Texture Index", this.textureIndex);

        this.model = (TexturedModel) EditorImGui.field_Asset("Model", this.model, Asset.AssetType.Model);

        if (this.model != null)
            model.getMaterial().imgui();
    }

    @Override
    public void reset() {
        this.model = null;
        this.textureIndex = 0;
    }
}
