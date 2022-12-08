package engine.renderEngine.particles;

import engine.renderEngine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

public class ParticleSystem {

    private float particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength, averageScale;

    private float speedError, lifeError, scaleError = 0;
    private boolean randomRotation = false;
    private Vector3f direction;
    private float directionDeviation = 0;
    private boolean useBlend = false;

    private ParticleTexture texture;

    private Random random = new Random();

    public ParticleSystem(ParticleTexture texture, float particlesPerSecond, float speed, float gravityComplient, float lifeLength, float scale) {
        this.particlesPerSecond = particlesPerSecond;
        this.averageSpeed = speed;
        this.gravityComplient = gravityComplient;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
        this.texture = texture;
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

    public void generateParticles(Vector3f systemCenter) {
        float delta = Window.getDelta();
        float particlesToCreate = particlesPerSecond * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;
        for (int i = 0; i < count; i++) {
            emitParticle(systemCenter);
        }
        if (Math.random() < partialParticle) {
            emitParticle(systemCenter);
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
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    private float generateRotation() {
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
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
        return new Vector3f(x, y, z);
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
