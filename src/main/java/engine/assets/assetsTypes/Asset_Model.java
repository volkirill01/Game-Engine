package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.textures.Texture;

import java.util.Map;

public class Asset_Model extends Asset {

    public Asset_Model(String assetPath, String assetName, Map<String, Object> data, Texture fileIcon) {
        super(assetPath, assetName, data, AssetType.Model, fileIcon);
    }

    @Override
    public void imgui() {
        this.data.replace("modelSize", EditorImGui.field_Float("Model Size", Float.parseFloat(this.data.get("modelSize").toString())));
    }
}
