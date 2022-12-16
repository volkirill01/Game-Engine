package engine.assets.assetsTypes;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.textures.Texture;
import engine.renderEngine.textures.TextureFilterMode;
import engine.renderEngine.textures.TextureSliceMode;

import java.util.Map;

public class Asset_Image extends Asset {

    public Asset_Image(String assetPath, String assetName, Map<String, Object> data, Texture fileIcon) {
        super(assetPath, assetName, data, AssetType.Image, fileIcon);
    }

    @Override
    public void imgui() {
        this.data.replace("repeatHorizontally", EditorImGui.field_Boolean("Repeat Horizontally", Boolean.parseBoolean(this.data.get("repeatHorizontally").toString())));
        this.data.replace("repeatVertically", EditorImGui.field_Boolean("Repeat Vertically", Boolean.parseBoolean(this.data.get("repeatVertically").toString())));

        if (EditorImGui.checkbox("Use Mipmaps", Boolean.parseBoolean(this.data.get("useMipmaps").toString()))) {
            this.data.replace("useMipmaps", !Boolean.parseBoolean(this.data.get("useMipmaps").toString()));
            this.data.replace("useAnisotropicFiltering", false);
        }

        if (EditorImGui.checkbox("Use Anisotropic Filtering", Boolean.parseBoolean(this.data.get("useAnisotropicFiltering").toString()))) {
            this.data.replace("useMipmaps", false);
            this.data.replace("useAnisotropicFiltering", !Boolean.parseBoolean(this.data.get("useAnisotropicFiltering").toString()));
        }

        this.data.replace("sliceMode", EditorImGui.field_Enum("Slice Mode", Enum.valueOf(TextureSliceMode.class, this.data.get("sliceMode").toString())));
        this.data.replace("filterMode", EditorImGui.field_Enum("Filter Mode", Enum.valueOf(TextureFilterMode.class, this.data.get("filterMode").toString())));
        super.saveMeta();
    }
}
