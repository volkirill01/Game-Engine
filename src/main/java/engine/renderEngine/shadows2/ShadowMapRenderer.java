package engine.renderEngine.shadows2;

import engine.components.Light;
import engine.entities.EditorCamera;
import engine.entities.GameObject;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.renderer.EntityRenderer;
import engine.renderEngine.shadows.ShadowBox;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class ShadowMapRenderer {

    private ShadowShader2 shader;

    private Vector3f tmpScale = new Vector3f(0.0f);

    private Map<TexturedModel, List<GameObject>> entities = new HashMap<>();

    public static final int SHADOW_MAP_SIZE = 2048;
    private ShadowBox shadowBox;
    private Matrix4f lightViewMatrix = new Matrix4f();
    private Matrix4f projectionViewMatrix = new Matrix4f();
    private Matrix4f offset = createOffset();

    private Matrix4f projectionMatrix;

    public ShadowMapRenderer(Matrix4f projectionMatrix, EditorCamera editorCamera) {
        this.shader = new ShadowShader2();
        shadowBox = new ShadowBox(lightViewMatrix, editorCamera);
        shader.start();
        shader.loadUniformMatrix("projectionMatrix", projectionMatrix);
        shader.stop();

        this.projectionMatrix = projectionMatrix;
    }

//    public void prepare() {
////        glEnable(GL_DEPTH_TEST);
////        glClearColor(0, 0, 0, 1);
////        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//    }

    public Matrix4f getToShadowMapSpaceMatrix() { return new Matrix4f(offset.mul(projectionViewMatrix)); }

    private void prepare(Vector3f lightDirection, ShadowBox box) {
        updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
        updateLightViewMatrix(lightDirection, box.getCenter());
        projectionViewMatrix = new Matrix4f(projectionMatrix.mul(lightViewMatrix));
//		shadowFbo.bindFrameBuffer();
    }

    private void updateOrthoProjectionMatrix(float width, float height, float length) {
        projectionMatrix = new Matrix4f().identity();
        projectionMatrix.m00(2f / width);
        projectionMatrix.m11(2f / height);
        projectionMatrix.m22(-2f / length);
        projectionMatrix.m33(1);
    }
    private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
        direction.normalize();
        center.negate();
        lightViewMatrix = new Matrix4f().identity();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        lightViewMatrix = lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        lightViewMatrix = lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix = lightViewMatrix.translate(center);
    }


    public void render(List<GameObject> gameObjects, EditorCamera editorCamera, EntityRenderer entityRenderer, Light sun) {
        shadowBox.update();

        Vector3f sunDirection = new Vector3f(0.0f);
        if (sun != null)
            sunDirection = sun.gameObject.transform.localRotation;
        prepare(sunDirection, shadowBox);

        for (GameObject gameObject : gameObjects) {
            if (!Window.get().runtimePlaying) {
                if (!gameObject.isVisible())
                    continue;

                if (gameObject.transform.mainParent != null && !gameObject.transform.mainParent.isVisible())
                    continue;
                if (gameObject.transform.parent != null && !gameObject.transform.parent.isVisible())
                    continue;
            }

            if (gameObject.hasComponent(MeshRenderer.class))
                processEntity(gameObject);
        }

//        prepare();
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        shader.start();
        Matrix4f viewMatrix = Maths.createViewMatrix(editorCamera);
        shader.loadUniformMatrix("viewMatrix", viewMatrix);

        entityRenderer.render(entities, getToShadowMapSpaceMatrix());

        shader.stop();
        entities.clear();

//        shadowMapRenderer2.render(gameObjects);

//        prepare();
//
//        shader.start();
//
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//
//        for (GameObject gameObject : gameObjects)
//            if (gameObject.hasComponent(MeshRenderer.class))
//                processEntity(gameObject);
//
//        for (TexturedModel model : entities.keySet()) {
//            for (int i = 0; i < model.getMesh().getModels().size(); i++) {
//                glBindVertexArray(model.getMesh().getModels().get(i).getVaoID());
//                List<GameObject> batch = entities.get(model);
//                for (GameObject gameObject : batch) {
//                    if (!gameObject.getComponent(MeshRenderer.class).isActive())
//                        continue;
//
//                    prepareInstance(gameObject);
//                    glDrawElements(GL_TRIANGLES, model.getMesh().getModels().get(i).getVertexCount(), GL_UNSIGNED_INT, 0);
//                }
//            }
//        }
//
//        shader.stop();
//
//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
//        glBindVertexArray(0);
    }

    private void processEntity(GameObject gameObject) {
        if (gameObject.getComponent(MeshRenderer.class).getModel() == null)
            return;

        TexturedModel entityModel = gameObject.getComponent(MeshRenderer.class).getModel();
        List<GameObject> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(gameObject);
        } else {
            List<GameObject> newBatch = new ArrayList<>();
            newBatch.add(gameObject);
            entities.put(entityModel, newBatch);
        }
    }

    private static Matrix4f createOffset() {
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
        offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        return offset;
    }
}
