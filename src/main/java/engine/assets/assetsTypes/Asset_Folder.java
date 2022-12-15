package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;
import imgui.ImGui;

import java.util.Map;

public class Asset_Folder extends Asset {

    private float size;

    public Asset_Folder(String assetPath, String assetName, Map<String, Object> data, boolean isEmpty) {
        super(assetPath, assetName, data, AssetType.Folder, isEmpty ? Loader.get().loadTexture("engineFiles/images/icons/icon=folder-open-regular-(256x256).png") : Loader.get().loadTexture("engineFiles/images/icons/icon=folder-solid-(256x256).png"));

        this.size = (float) data.get("size");
    }

    @Override
    public void imgui() {
        ImGui.text("Size: " + size + "mb");
        super.saveMeta();
    }
}
