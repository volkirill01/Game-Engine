package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.textures.Texture;

import java.util.Map;

public class Asset_Image extends Asset {

    public Asset_Image(String assetPath, String assetName, Map<String, Object> data, Texture fileIcon) {
        super(assetPath, assetName, data, AssetType.Image, fileIcon);
    }

    @Override
    public void imgui() {
        this.data.replace("repeatHorizontally", EditorImGui.field_Boolean("Repeat Horizontally", Boolean.parseBoolean(this.data.get("repeatHorizontally").toString())));
        this.data.replace("repeatVertically", EditorImGui.field_Boolean("Repeat Vertically", Boolean.parseBoolean(this.data.get("repeatVertically").toString())));
        super.saveMeta();
    }
}
