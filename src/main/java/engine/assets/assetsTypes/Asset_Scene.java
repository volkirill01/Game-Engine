package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;

import java.util.Map;

public class Asset_Scene extends Asset {

    public Asset_Scene(String assetPath, String assetName, Map<String, Object> sceneParams) {
        super(assetPath, assetName, sceneParams, AssetType.Scene, Loader.get().loadTexture("engineFiles/images/icons/icon=scene-solid-(256x256).png"));
    }

    @Override
    public void imgui() {
        super.saveMeta();
    }
}
