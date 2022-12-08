package engine.renderEngine.particles;

import engine.entities.Camera;
import engine.renderEngine.Loader;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {

    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Matrix4f projectionMatrix) { renderer = new ParticleRenderer(projectionMatrix); }

    public static void update(Camera camera) {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
            List<Particle> batch = entry.getValue();

            Iterator<Particle> iterator = batch.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();

                    if (batch.isEmpty())
                        mapIterator.remove();
                }
            }
            if (!entry.getKey().isAdditive())
                InsertionSort.sortHighToLow(batch);
        }
    }

    public static void renderParticles(Camera camera) { renderer.render(particles, camera); }

    public static void cleanUp() { renderer.cleanUp(); }

    public static void addParticle(Particle particle) {
        List<Particle> batch = particles.get(particle.getTexture());
        if (batch == null) {
            batch = new ArrayList<>();
            particles.put(particle.getTexture(), batch);
        }
        batch.add(particle);
    }
}
