package engine.renderEngine.particles.particleSystemComponents_PSC;

import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.Random;

public class PSC_ColorOverTime extends ParticleSystemComponent {

    @Override
    public void update(float time, Random random, Vector3f velocity, FloatBuffer gravity) {

    }

    @Override
    public void imgui() {

    }

    @Override
    public String getComponentName() { return "Color Over Time"; }
}
