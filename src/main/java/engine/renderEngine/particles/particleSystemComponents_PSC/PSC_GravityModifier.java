package engine.renderEngine.particles.particleSystemComponents_PSC;

import engine.imGui.EditorImGui;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.Random;

public class PSC_GravityModifier extends ParticleSystemComponent {

    private float gravity = 0.5f;

    @Override
    public void update(float time, Random random, Vector3f velocity, FloatBuffer gravity) {
        if (gravity != null)
            gravity.put(this.gravity);
    }

    @Override
    public void imgui() {
        this.gravity = EditorImGui.field_Float("Gravity", this.gravity, 0.005f);
    }

    @Override
    public String getComponentName() { return "Gravity"; }
}
