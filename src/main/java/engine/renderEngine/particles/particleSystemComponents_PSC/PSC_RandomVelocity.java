package engine.renderEngine.particles.particleSystemComponents_PSC;

import engine.imGui.EditorImGui;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.Random;

public class PSC_RandomVelocity extends ParticleSystemComponent {

    private float speed = 1.0f;

    @Override
    public void update(float time, Random random, Vector3f velocity, FloatBuffer gravity) {
        generateRandomUnitVector(random, velocity);
    }

    private void generateRandomUnitVector(Random random, Vector3f velocity) {
        float theta = (float) (random.nextFloat() * 2.0f * Math.PI);
        float z = (random.nextFloat() * 2.0f) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        if (velocity != null) {
            velocity.x = x * speed;
            velocity.y = y * speed;
            velocity.z = z * speed;
        }
    }

    @Override
    public void imgui() {
        this.speed = EditorImGui.field_Float("Speed", this.speed, 0.005f);
    }

    @Override
    public String getComponentName() { return "Random Velocity"; }
}
