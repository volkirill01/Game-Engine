package engine.renderEngine.components;

import engine.renderEngine.models.TexturedModel;
import org.joml.Vector2f;

public class MeshRenderer extends ObjectRenderer {

    private TexturedModel model;
    private int textureIndex;

    public MeshRenderer(TexturedModel model) {
        this.model = model;
        this.textureIndex = 1;
    }

    public MeshRenderer(TexturedModel model, int textureIndex) {
        this.model = model;
        this.textureIndex = textureIndex;
    }

    public Vector2f getTextureOffset() {
        int column = textureIndex % model.getTexture().getNumberOfRows();
        int row = textureIndex / model.getTexture().getNumberOfColumns();

        return new Vector2f(
                (float) column / (float) model.getTexture().getNumberOfRows(),
                (float) row / (float) model.getTexture().getNumberOfColumns());
    }

    public TexturedModel getModel() { return this.model; }

    public void setModel(TexturedModel model) { this.model = model; }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }
}
