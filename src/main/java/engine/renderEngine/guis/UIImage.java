package engine.renderEngine.guis;

import engine.assets.Asset;
import engine.components.Component;
import engine.imGui.EditorImGui;
import engine.renderEngine.textures.Texture;

public class UIImage extends Component {

    private Texture texture;

    public UIImage(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() { return this.texture; }

    public void setTexture(Texture texture) { this.texture = texture; }

    @Override
    public void imgui() {
        this.texture = (Texture) EditorImGui.field_Asset("Texture", this.texture, Asset.AssetType.Texture);
    }

    @Override
    public void reset() { this.texture = null; }
}
