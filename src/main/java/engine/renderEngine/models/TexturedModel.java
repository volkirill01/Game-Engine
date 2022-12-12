package engine.renderEngine.models;

import engine.renderEngine.textures.Material;

public class TexturedModel {

    private RawModel rawModel;
    private Material material;

    public TexturedModel(RawModel model, Material texture) {
        this.rawModel = model;
        this.material = texture;
    }

    public RawModel getRawModel() { return rawModel; }

    public void setMaterial(Material material) { this.material = material; }

    public Material getMaterial() { return material; }

    public TexturedModel copy() { return new TexturedModel(this.rawModel, this.material); }
}
