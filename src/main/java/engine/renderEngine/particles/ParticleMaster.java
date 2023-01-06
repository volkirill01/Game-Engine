package engine.renderEngine.particles;

import engine.entities.EditorCamera;
import engine.renderEngine.textures.Texture;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {

    private static Map<Texture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Matrix4f projectionMatrix) { renderer = new ParticleRenderer(projectionMatrix); }

    public static void update(EditorCamera editorCamera) {
        Iterator<Map.Entry<Texture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<Texture, List<Particle>> entry = mapIterator.next();
            List<Particle> batch = entry.getValue();

            Iterator<Particle> iterator = batch.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(editorCamera);
                if (!stillAlive) {
                    iterator.remove();

                    if (batch.isEmpty())
                        mapIterator.remove();
                }
            }
//            if (!entry.getKey().isAdditive())
//                InsertionSort.sortHighToLow(batch);
        }
    }

    public static void renderParticles(EditorCamera editorCamera) { renderer.render(particles, editorCamera); }

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
