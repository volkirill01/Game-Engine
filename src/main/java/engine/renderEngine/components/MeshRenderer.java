package engine.renderEngine.components;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.TextureSliceMode;
import engine.toolbox.DefaultMeshes;
import org.joml.Vector2f;

import java.util.Collections;

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
//        int column = textureIndex % model.getMaterial().getTexture().getNumberOfRows();
//        int row = textureIndex / model.getMaterial().getTexture().getNumberOfColumns();
//
//        return new Vector2f(
//                (float) column / (float) model.getMaterial().getTexture().getNumberOfRows(),
//                (float) row / (float) model.getMaterial().getTexture().getNumberOfColumns());
        return new Vector2f(0.0f);
    }

    public TexturedModel getModel() { return this.model; }

    public void setModel(TexturedModel model) { this.model = model; }

    @Override
    public void start() { }

    @Override
    public void update() { }

    @Override
    public void editorUpdate() {
        if (model != null) {
            this.model.update();
        }
    }

    @Override
    public void imgui() {
//        if (this.model != null)
//            if (this.model.getMaterial().getTexture().getSliceMode() == TextureSliceMode.Multiple)
//                this.textureIndex = EditorImGui.field_Int_WithButtons("Texture Index", this.textureIndex);

        this.model = (TexturedModel) EditorImGui.field_Asset("Model", this.model, Asset.AssetType.Model);

        if (this.model != null) {
            EditorImGui.field_MaterialsList("Materials", model.getMaterials());

            for (int i = 0; i < model.getMaterials().size(); i++) {
                model.setMaterial((Material) EditorImGui.field_Asset(model.getMesh().getModels().get(i).getMaterialGroup(), model.getMaterials().get(i), Asset.AssetType.Material), i);
                if (!model.getMaterials().get(i).getFilepath().equals(DefaultMeshes.getDefaultMaterialPath())) {
                    if (EditorImGui.collapsingHeader(model.getMesh().getModels().get(i).getMaterialGroup()))
                        model.getMaterials().get(i).imgui();
                }
            }
        }
    }

    @Override
    public void reset() {
        this.model = null;
        this.textureIndex = 0;
    }
}
