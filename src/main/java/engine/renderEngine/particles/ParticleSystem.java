package engine.renderEngine.particles;

import engine.assets.Asset;
import engine.components.Component;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.textures.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

public class ParticleSystem extends Component {

    private float particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength, averageScale;

    private float speedError, lifeError, scaleError = 0;
    private boolean randomRotation = false;
    private Vector3f direction;
    private float directionDeviation = 0;
    private boolean useBlend = false;

    private transient Texture texture;

    private transient Random random;

    public ParticleSystem(Texture texture, float particlesPerSecond, float speed, float gravityComplient, float lifeLength, float scale, boolean randomRotation, boolean useBlend) {
        this.particlesPerSecond = particlesPerSecond;
        this.averageSpeed = speed;
        this.gravityComplient = gravityComplient;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
        this.texture = Loader.get().loadTexture(texture.getFilepath());
        this.randomRotation = randomRotation;
        this.useBlend = useBlend;
    }

    /**
     * @param direction - The average direction in which particles are emitted.
     * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
     */
    public void setDirection(Vector3f direction, float deviation) {
        this.direction = new Vector3f(direction);
        this.directionDeviation = (float) (deviation * Math.PI);
    }

    public void randomizeRotation() {
        randomRotation = true;
    }

    /**
     * @param error
     *            - A number between 0 and 1, where 0 means no error margin.
     */
    public void setSpeedError(float error) {
        this.speedError = error * averageSpeed;
    }

    /**
     * @param error
     *            - A number between 0 and 1, where 0 means no error margin.
     */
    public void setLifeError(float error) {
        this.lifeError = error * averageLifeLength;
    }

    /**
     * @param error
     *            - A number between 0 and 1, where 0 means no error margin.
     */
    public void setScaleError(float error) {
        this.scaleError = error * averageScale;
    }

    public boolean isUseBlend() { return this.useBlend; }

    public void setUseBlend(boolean useBlend) { this.useBlend = useBlend; }

    @Override
    public void editorUpdate() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this.gameObject)
            generateParticles();
    }

    @Override
    public void update() {
        if (Window.get().runtimePlaying)
            generateParticles();
    }

    private void generateParticles() {
        float delta = Window.getDelta();
        float particlesToCreate = particlesPerSecond * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;
        for (int i = 0; i < count; i++) {
            emitParticle(gameObject.transform.position);
        }
        if (Math.random() < partialParticle) {
            emitParticle(gameObject.transform.position);
        }
    }

    private void emitParticle(Vector3f center) {
        Vector3f velocity;
        if (direction != null)
            velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
        else
            velocity = generateRandomUnitVector();

        velocity.normalize();
        velocity.mul(generateValue(averageSpeed, speedError));
        float scale = generateValue(averageScale, scaleError);
        float lifeLength = generateValue(averageLifeLength, lifeError);
        new Particle(texture, useBlend, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale);
    }

    private float generateValue(float average, float errorMargin) {
        if (this.random == null)
            this.random = new Random();
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    private float generateRotation() {
        if (this.random == null)
            this.random = new Random();
        if (randomRotation) {
            return random.nextFloat() * 360f;
        } else {
            return 0;
        }
    }

    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
        float cosAngle = (float) Math.cos(angle);
        Random random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        Vector4f direction = new Vector4f(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            Vector3f rotateAxis = coneDirection.cross(new Vector3f(0, 0, 1));
            rotateAxis.normalize();
            float rotateAngle = (float) Math.acos(coneDirection.dot(new Vector3f(0, 0, 1)));
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            direction = rotationMatrix.transform(direction);
        } else if (coneDirection.z == -1) {
            direction.z *= -1;
        }
        return new Vector3f(direction.x, direction.y, direction.z);
    }

    private Vector3f generateRandomUnitVector() {
        if (this.random == null)
            this.random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
        return new Vector3f(x, y, z);
    }

    @Override
    public void imgui() {
        this.particlesPerSecond = EditorImGui.field_Float("particlesPerSecond", this.particlesPerSecond);
        this.averageSpeed = EditorImGui.field_Float("averageSpeed", this.averageSpeed);
        this.gravityComplient = EditorImGui.field_Float("gravityComplient", this.gravityComplient);
        this.averageLifeLength = EditorImGui.field_Float("averageLifeLength", this.averageLifeLength);
        this.averageScale = EditorImGui.field_Float("averageScale", this.averageScale);

        this.speedError = EditorImGui.field_Float("speedError", this.speedError);
        this.lifeError = EditorImGui.field_Float("lifeError", this.lifeError);
        this.scaleError = EditorImGui.field_Float("scaleError", this.scaleError);

        this.randomRotation = EditorImGui.field_Boolean("randomRotation", this.randomRotation);
        if (direction != null)
            this.direction = EditorImGui.field_Vector3f("direction", this.direction);

        this.directionDeviation = EditorImGui.field_Float("directionDeviation", this.directionDeviation);
        this.useBlend = EditorImGui.field_Boolean("useBlend", this.useBlend);

        this.texture = (Texture) EditorImGui.field_Asset("texture", this.texture, Asset.AssetType.Texture);
    }

    @Override
    public void reset() {

    }

    // Simple Particle system
//    private float particlesPerSecond;
//    private float speed;
//    private float gravityComplient;
//    private float lifeLength;
//
//    public ParticleSystem(float particlesPerSecond, float speed, float gravityComplient, float lifeLength) {
//        this.particlesPerSecond = particlesPerSecond;
//        this.speed = speed;
//        this.gravityComplient = gravityComplient;
//        this.lifeLength = lifeLength;
//    }
//
//    public void generateParticles(Vector3f systemCenter){
//        float delta = DisplayManager.getDelta();
//        float particlesToCreate = particlesPerSecond * delta;
//        int count = (int) Math.floor(particlesToCreate);
//        float partialParticle = particlesToCreate % 1;
//        for(int i=0;i<count;i++){
//            emitParticle(systemCenter);
//        }
//        if(Math.random() < partialParticle){
//            emitParticle(systemCenter);
//        }
//    }
//
//    private void emitParticle(Vector3f center){
//        float dirX = (float) Math.random() * 2f - 1f;
//        float dirZ = (float) Math.random() * 2f - 1f;
//        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
//        velocity.normalize();
//        velocity.mul(speed);
//        new Particle(new Vector3f(center), velocity, gravityComplient, lifeLength, 0, 1);
//    }
}
