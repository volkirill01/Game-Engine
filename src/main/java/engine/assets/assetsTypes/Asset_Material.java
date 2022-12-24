package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.Texture;
import org.joml.Vector2f;

import java.util.Map;

public class Asset_Material extends Asset {

    public Asset_Material(String assetPath, String assetName, Map<String, Object> data, Texture fileIcon) {
        super(assetPath, assetName, data, Asset.AssetType.Material, fileIcon);
    }

    @Override
    public void imgui() {
        Material mat = Loader.get().loadMaterial(assetPath);

        Texture texture = EditorImGui.field_Texture("Model Size", mat.getTexture(), new Vector2f(), new Vector2f());
        if (texture != null) {
            this.data.replace("albedo", texture.getFilepath());
            mat.setTexture(texture);
        }
//        super.saveMeta();
        Loader.get().saveMaterial(this.assetPath, this.data);
    }
}
