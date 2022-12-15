package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;

import java.util.Map;

public class Asset_Sound extends Asset {

    public Asset_Sound(String assetPath, String assetName, Map<String, Object> data) {
        super(assetPath, assetName, data, AssetType.Sound, Loader.get().loadTexture("engineFiles/images/icons/icon=volume-high-solid-(256x256).png"));
    }

    @Override
    public void imgui() {
        super.saveMeta();
    }
}
