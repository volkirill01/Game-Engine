package engine.renderEngine.guis;

import engine.renderEngine.textures.Texture;
import org.joml.Vector2f;

public class GuiTexture {

    private Texture texture;
    private Vector2f position;
    private float rotation;
    private Vector2f scale;

    public GuiTexture(Texture texture, Vector2f position, float rotation, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Texture getTexture() { return this.texture; }

    public void setTexture(Texture texture) { this.texture = texture; }

    public Vector2f getPosition() { return this.position; }

    public void setPosition(Vector2f position) { this.position = position; }

    public float getRotation() { return this.rotation; }

    public void setRotation(float rotation) { this.rotation = rotation; }

    public Vector2f getScale() { return this.scale; }

    public void setScale(Vector2f scale) { this.scale = scale; }
}
