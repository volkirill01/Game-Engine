package engine.renderEngine.particles.particleSystemComponents_PSC;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ParticleSystemComponent {

    public static final List<ParticleSystemComponent> allComponents = new ArrayList<>(){{
        add(new PSC_ColorOverTime());
        add(new PSC_RandomVelocity());
    }};

    public abstract void update(float time, Random random, Vector3f velocity);

    public abstract void imgui();

    public abstract String getComponentName();
}
