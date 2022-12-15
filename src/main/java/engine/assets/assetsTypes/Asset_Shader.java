package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.renderEngine.Loader;

import java.util.Map;

public class Asset_Shader extends Asset {

    public Asset_Shader(String assetPath, String assetName, Map<String, Object> data) {
        super(assetPath, assetName, data, AssetType.Shader, Loader.get().loadTexture("engineFiles/images/icons/icon=shader-file-solid-(256x256).png"));
    }

    @Override
    public void imgui() {
        super.saveMeta();
    }
}
