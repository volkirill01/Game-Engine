package engine.renderEngine.particles;

import engine.Settings;
import engine.entities.Camera;
import engine.renderEngine.Window;
import engine.renderEngine.textures.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private Texture texture;

    private Vector2f textureOffsetCurrentFrame = new Vector2f();
    private Vector2f textureOffsetNextFrame = new Vector2f();
    private float blendFactor;

    private boolean useBlend;

    private float elapsedTime = 0;
    private float distanceFromCamera;

    private Vector3f change = new Vector3f();

    public Particle(Texture texture, boolean useBlend, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.texture = texture;
        this.useBlend = useBlend;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    public Texture getTexture() { return this.texture; }

    public Vector3f getPosition() { return this.position; }

    public float getRotation() { return this.rotation; }

    public float getScale() { return this.scale; }

    public Vector2f getTextureOffsetCurrentFrame() { return this.textureOffsetCurrentFrame; }

    public Vector2f getTextureOffsetNextFrame() { return this.textureOffsetNextFrame; }

    public float getBlendFactor() { return this.blendFactor; }

    public boolean isUseBlend() { return this.useBlend; }

    public void setUseBlend(boolean useBlend) { this.useBlend = useBlend; }

    public float getDistanceFromCamera() { return this.distanceFromCamera; }

    protected boolean update(Camera camera) {
        if (this.velocity != null) {
            velocity.y += Settings.GRAVITY * (gravityEffect * 0.1f) * Window.getDelta();
            change.set(velocity);
        }
        change.mul(Window.getDelta());
        position.add(change);
        distanceFromCamera = new Vector3f(camera.getPosition()).sub(position).lengthSquared();
        updateTextureCoordsInfo();
        elapsedTime += Window.getDelta();
        return elapsedTime < lifeLength;
    }

    private void updateTextureCoordsInfo() {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfColumns();
        float atlasProgression = lifeFactor * stageCount;
        int currentIndex = (int) Math.floor(atlasProgression);
        int nextIndex = currentIndex < stageCount - 1 ? currentIndex + 1 : currentIndex;
        this.blendFactor = atlasProgression % 1;
        setTextureOffset(textureOffsetCurrentFrame, currentIndex);
        setTextureOffset(textureOffsetNextFrame, nextIndex);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % texture.getNumberOfColumns();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfColumns();
        offset.y = (float) row / texture.getNumberOfRows();
    }
}
