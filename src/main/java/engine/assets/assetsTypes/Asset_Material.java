package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.Texture;
import engine.toolbox.DefaultMeshes;

import java.util.Map;

public class Asset_Material extends Asset {

    public Asset_Material(String assetPath, String assetName, Map<String, Object> data, Texture fileIcon, boolean isNew) {
        super(assetPath, assetName, data, !isNew ? Asset.AssetType.Material : Asset.AssetType.NewMaterial, fileIcon);
    }

    @Override
    public void imgui() {
        Material mat = Loader.get().loadMaterial(assetPath);
        if (!mat.getFilepath().equals(DefaultMeshes.getDefaultMaterialPath()))
            mat.imgui();
    }
}
