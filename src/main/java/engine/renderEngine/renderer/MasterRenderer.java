package engine.renderEngine.renderer;

import engine.entities.EditorCamera;
import engine.entities.GameObject;
import engine.components.Light;
import engine.renderEngine.PickingShader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.components.ObjectRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.normalMappingRenderer.NormalMappingRenderer;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.shadows.ShadowMapMasterRenderer;
import engine.renderEngine.shadows2.ShadowMapRenderer;
import engine.renderEngine.skybox.CubeMap;
import engine.renderEngine.skybox.SkyboxRenderer;
import engine.terrain.Terrain;
import engine.toolbox.Maths;
import engine.toolbox.customVariables.Color;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class MasterRenderer {

    public static final float FOV = 70.0f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000.0f;

    private static Matrix4f projectionMatrix;

    // PBR
//    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/pbrEntityVertexShader.glsl", "engineFiles/shaders/entity/pbrEntityFragmentShader.glsl");

    // Default
    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/vertexShader.glsl", "engineFiles/shaders/entity/fragmentShader.glsl");

    private PickingShader pickingShader = new PickingShader();

   // Cell shading
//    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/vertexShader.glsl", "engineFiles/shaders/entity/cellShader/cellFragmentShader.glsl"); // For cell shading
    private EntityRenderer entityRenderer;

    private StaticShader terrainShader = new StaticShader("engineFiles/shaders/terrain/terrainVertexShader.glsl", "engineFiles/shaders/terrain/terrainFragmentShader.glsl");
    private TerrainRenderer terrainRenderer;

    private Map<TexturedModel, List<GameObject>> entities = new HashMap<>();
    private Map<TexturedModel, List<GameObject>> normalMapEntities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    private NormalMappingRenderer normalMappingRenderer;

    private SkyboxRenderer skyboxRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;

    private ShadowMapRenderer shadowMapRenderer2;

    private List<Light> lights = new ArrayList<>();
//    private Light sun;

    public MasterRenderer(EditorCamera editorCamera) {
        projectionMatrix = createProjectionMatrix();
        skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        entityRenderer = new EntityRenderer(shader, pickingShader, projectionMatrix, new CubeMap(skyboxRenderer.getTexture()));
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
        shadowMapRenderer = new ShadowMapMasterRenderer(editorCamera);

        shadowMapRenderer2 = new ShadowMapRenderer(projectionMatrix, editorCamera);
    }

    private void render(EditorCamera editorCamera, boolean renderOnPickingTexture, int shadowMap) {
        prepare(shadowMap);

        if (!renderOnPickingTexture) {
//        Color fogColor = PostProcessing.getFogColor().toPercentColor();
//        glClearColor(fogColor.r, fogColor.g, fogColor.b, 1);

            shader.start();

            shader.loadLights(lights);
            shader.loadUniformFloat("ambientLightIntensity", PostProcessing.getAmbientLightIntensity());
            shader.loadUniformColor("ambientLightColor", PostProcessing.getAmbientLightColor());

            if (PostProcessing.isUseFog()) {
                shader.loadUniformColor("fogColor", PostProcessing.getFogColor());
                shader.loadUniformFloat("fogDensity", PostProcessing.getFogDensity());
                shader.loadUniformFloat("fogGradient", PostProcessing.getFogSmoothness());
            } else {
                shader.loadUniformColor("fogColor", Color.Black);
                shader.loadUniformFloat("fogDensity", 0.0f);
                shader.loadUniformFloat("fogGradient", 0.0f);
            }

            Matrix4f viewMatrix = Maths.createViewMatrix(editorCamera);
            shader.loadUniformMatrix("viewMatrix", viewMatrix);

            entityRenderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());

            shader.stop();
            entities.clear();

            // NormalMap entities
            normalMappingRenderer.render(normalMapEntities, lights, editorCamera);
            normalMapEntities.clear();
            // NormalMap entities

            // TERRAIN
            terrainShader.start();

            terrainShader.loadLights(lights);

            terrainShader.loadUniformColor("ambientLightColor", PostProcessing.getAmbientLightColor());
            terrainShader.loadUniformFloat("ambientLightIntensity", PostProcessing.getAmbientLightIntensity());

            if (PostProcessing.isUseFog()) {
                terrainShader.loadUniformColor("fogColor", PostProcessing.getFogColor());
                terrainShader.loadUniformFloat("fogDensity", PostProcessing.getFogDensity());
                terrainShader.loadUniformFloat("fogGradient", PostProcessing.getFogSmoothness());
            } else {
                terrainShader.loadUniformColor("fogColor", Color.Black);
                terrainShader.loadUniformFloat("fogDensity", 0.0f);
                terrainShader.loadUniformFloat("fogGradient", 0.0f);
            }

            terrainShader.loadUniformMatrix("viewMatrix", viewMatrix);

            terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

            terrainShader.stop();
            terrains.clear();
            // TERRAIN

            skyboxRenderer.render(editorCamera);
        } else {
            pickingShader.start();

            Matrix4f viewMatrix = Maths.createViewMatrix(editorCamera);
            pickingShader.loadUniformMatrix("viewMatrix", viewMatrix);

            entityRenderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());

            pickingShader.stop();
            entities.clear();

            // NormalMap entities
            normalMappingRenderer.render(normalMapEntities, lights, editorCamera);
            normalMapEntities.clear();
            // NormalMap entities

            // TERRAIN
            pickingShader.start();

            pickingShader.loadUniformMatrix("viewMatrix", viewMatrix);

            terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

            pickingShader.stop();
            terrains.clear();
            // TERRAIN
        }
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

    private void processNormalMapEntity(GameObject gameObject) {
//        if (gameObject.getComponent(MeshRenderer.class) == null)
//            return;

        TexturedModel entityModel = gameObject.getComponent(MeshRenderer.class).getModel();
        List<GameObject> batch = normalMapEntities.get(entityModel);
        if (batch != null) {
            batch.add(gameObject);
        } else {
            List<GameObject> newBatch = new ArrayList<>();
            newBatch.add(gameObject);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    private void processTerrain(Terrain terrain) { terrains.add(terrain); }

    public void prepare(int shadowMap) {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glActiveTexture(GL_TEXTURE5);
//        glBindTexture(GL_TEXTURE_2D, shadowMap);
        glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());
    }

    public void renderScene(List<GameObject> entities, List<GameObject> normalMapEntities, List<Terrain> terrains, EditorCamera editorCamera, boolean renderOnPickingTexture, int shadowMap) {
        lights.clear();

        for (Terrain terrain : terrains)
            processTerrain(terrain);

        for (GameObject gameObject : entities) {
            if (!Window.get().runtimePlaying) {
                if (!gameObject.isVisible())
                    continue;

                if (gameObject.transform.mainParent != null && !gameObject.transform.mainParent.isVisible())
                    continue;
                if (gameObject.transform.parent != null && !gameObject.transform.parent.isVisible())
                    continue;
            }

            if (gameObject.hasComponent(Light.class)) {
//                if (sun == null)
//                    sun = gameObject.getComponent(Light.class);
                lights.add(gameObject.getComponent(Light.class));
            }

            if (gameObject.hasComponent(MeshRenderer.class))
                processEntity(gameObject);
        }

        for (GameObject normalMapGameObject : normalMapEntities)
            processNormalMapEntity(normalMapGameObject);

        render(editorCamera, renderOnPickingTexture, shadowMap);
    }

//    public void renderShadowMap(List<GameObject> gameObjectList) {
//        for (GameObject gameObject : gameObjectList) {
//            if (sun == null)
//                if (gameObject.hasComponent(Light.class))
//                    sun = gameObject.getComponent(Light.class);
//
//            if (gameObject.getComponent(ObjectRenderer.class) == null)
//                continue;
//            processEntity(gameObject);
//        }
//
//        shadowMapRenderer.render(entities, sun);
//        entities.clear();
//    }

//    public void renderShadowMap(List<GameObject> gameObjects, EditorCamera editorCamera) {
//        shadowMapRenderer2.render(gameObjects, editorCamera, entityRenderer, sun);
//    }

    public int getShadowMapTexture() { return shadowMapRenderer.getShadowMap(); }

    public void cleanUp() {
        this.shader.cleanUp();
        this.terrainShader.cleanUp();
        normalMappingRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    public static Matrix4f getProjectionMatrix() { return projectionMatrix;}

    public static Matrix4f createProjectionMatrix() {
        Matrix4f matrix = new Matrix4f();
        float aspectRatio = Window.getWidth() / Window.getHeight();
        float y_scale = (float) ((1.0f / Math.tan(Math.toRadians(FOV / 2.0f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        matrix.m00(x_scale);
        matrix.m11(y_scale);
        matrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        matrix.m23(-1.0f);
        matrix.m32(-((2.0f * NEAR_PLANE * FAR_PLANE) / frustum_length));
        matrix.m33(0.0f);

        return matrix;
    }

    public int getReflectionsMap() { return skyboxRenderer.getTexture(); }
}
