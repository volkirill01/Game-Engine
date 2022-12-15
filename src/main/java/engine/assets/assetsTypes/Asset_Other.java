package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;

import java.util.Map;

public class Asset_Other extends Asset {

    public Asset_Other(String assetPath, String assetName, Map<String, Object> data) {
        super(assetPath, assetName, data, AssetType.Other, Loader.get().loadTexture("engineFiles/images/icons/icon=file-circle-question-(256x256).png"));
    }

    @Override
    public void imgui() {
        super.saveMeta();
    }
}
