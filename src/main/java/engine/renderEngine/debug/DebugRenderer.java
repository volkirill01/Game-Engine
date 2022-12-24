package engine.renderEngine.debug;

import engine.entities.GameObject;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.shaders.StaticShader;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class DebugRenderer {

    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/vertexShader.glsl", "engineFiles/shaders/entity/fragmentShader.glsl");
    private Map<TexturedModel, List<GameObject>> lines = new HashMap<>();

    public DebugRenderer(Matrix4f projectionMatrix) {
        this.shader.start();
        this.shader.loadUniformMatrix("projectionMatrix", projectionMatrix);
        this.shader.stop();
    }

    public void render() {
        for (TexturedModel model : lines.keySet()) {
            prepareTexturedModel(model);
            List<GameObject> batch = lines.get(model);
            for (GameObject gameObject : batch) {
                if (!gameObject.getComponent(MeshRenderer.class).isActive())
                    continue;

                Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                        gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale);
                shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
                glDrawElements(GL_LINES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                unbindTexturedModel();
            }
        }
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);

        shader.loadUniformColor("color", model.getMaterial().getColor());
    }

    private void unbindTexturedModel() {
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
