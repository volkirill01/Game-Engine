package engine.renderEngine.renderer;

import engine.entities.Camera;
import engine.entities.GameObject;
import engine.entities.Light;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.components.ObjectRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.normalMappingRenderer.NormalMappingRenderer;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.shadows.ShadowMapMasterRenderer;
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

    private Matrix4f projectionMatrix;

    // PBR
    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/pbrEntityVertexShader.glsl", "engineFiles/shaders/entity/pbrEntityFragmentShader.glsl");

    // Default
//    private StaticShader shader = new StaticShader("engineFiles/shaders/entity/vertexShader.glsl", "engineFiles/shaders/entity/fragmentShader.glsl");

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

    public MasterRenderer(Camera camera) {
        createProjectionMatrix();
        skyboxRenderer = new SkyboxRenderer(projectionMatrix);
        entityRenderer = new EntityRenderer(shader, projectionMatrix, new CubeMap(skyboxRenderer.getTexture()));
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }

    private void render(List<Light> lights, Camera camera) {
        prepare();

        Color fogColor = PostProcessing.getFogColor().toPercentColor();
        glClearColor(fogColor.r, fogColor.g, fogColor.b, 1);

        shader.start();

        shader.loadLights(lights);

        shader.loadUniformColor("fogColor", PostProcessing.getFogColor());
        shader.loadUniformColor("ambientLightColor", PostProcessing.getAmbientLightColor());
        shader.loadUniformFloat("ambientLightIntensity", PostProcessing.getAmbientLightIntensity());
        shader.loadUniformFloat("fogDensity", PostProcessing.getFogDensity());
        shader.loadUniformFloat("fogGradient", PostProcessing.getFogSmoothness());

        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        shader.loadUniformMatrix("viewMatrix", viewMatrix);

        entityRenderer.render(entities);

        shader.stop();
        entities.clear();

        // NormalMap entities
        normalMappingRenderer.render(normalMapEntities, lights, camera);
        // NormalMap entities

        // TERRAIN
        terrainShader.start();

        terrainShader.loadLights(lights);

        terrainShader.loadUniformColor("fogColor", PostProcessing.getFogColor());
        terrainShader.loadUniformColor("ambientLightColor", PostProcessing.getAmbientLightColor());
        terrainShader.loadUniformFloat("ambientLightIntensity", PostProcessing.getAmbientLightIntensity());
        terrainShader.loadUniformFloat("fogDensity", PostProcessing.getFogDensity());
        terrainShader.loadUniformFloat("fogGradient", PostProcessing.getFogSmoothness());

        terrainShader.loadUniformMatrix("viewMatrix", viewMatrix);

        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

        terrainShader.stop();
        terrains.clear();
        // TERRAIN

        normalMapEntities.clear();

        skyboxRenderer.render(camera);
    }

    private void processEntity(GameObject gameObject) {
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

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());
    }

    public void renderScene(List<GameObject> entities, List<GameObject> normalMapEntities, List<Terrain> terrains, List<Light> lights, Camera camera) {
        for (Terrain terrain : terrains)
            processTerrain(terrain);

        for (GameObject gameObject : entities) {
            if (gameObject.getComponent(ObjectRenderer.class) == null)
                continue;
            processEntity(gameObject);
        }

        for (GameObject normalMapGameObject : normalMapEntities)
            processNormalMapEntity(normalMapGameObject);

        render(lights, camera);
    }

    public void renderShadowMap(List<GameObject> gameObjectList, Light sun) {
        for (GameObject gameObject : gameObjectList) {
            if (gameObject.getComponent(ObjectRenderer.class) == null)
                continue;
            processEntity(gameObject);
        }

        shadowMapRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() { return shadowMapRenderer.getShadowMap(); }

    public void cleanUp() {
        this.shader.cleanUp();
        this.terrainShader.cleanUp();
        normalMappingRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = Window.getWidth() / Window.getHeight();
        float y_scale = (float) ((1.0f / Math.tan(Math.toRadians(FOV / 2.0f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(-((2.0f * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0.0f);
    }

    public Matrix4f getProjectionMatrix() { return this.projectionMatrix; }

    public int getReflectionsMap() { return skyboxRenderer.getTexture(); }
}
