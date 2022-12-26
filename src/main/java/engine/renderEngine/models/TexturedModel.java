package engine.renderEngine.models;

import engine.renderEngine.textures.Material;

import java.util.ArrayList;
import java.util.List;

public class TexturedModel {

    private Mesh mesh;
    private List<Material> materials;

    public TexturedModel(Mesh model, Material material) {
        this.mesh = model;
        this.materials = new ArrayList<>(){{ add(material); }};
    }

    public TexturedModel(Mesh model, List<Material> materials) {
        this.mesh = model;
        this.materials = materials;
    }

    public void update() {
        this.mesh.update();

        for (Material mat : this.materials)
            mat.update();
    }

    public String getFilepath() { return mesh.getFilepath(); }

    public Mesh getMesh() { return this.mesh; }

    public void setMaterials(List<Material> materials) { this.materials = materials; }

    public void setMaterial(Material material, int index) { this.materials.set(index, material); }

    public List<Material> getMaterials() { return materials; }

    public TexturedModel copy() { return new TexturedModel(this.mesh, this.materials); }
}
