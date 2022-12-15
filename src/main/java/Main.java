import engine.TestFieldsWindow;
import engine.entities.Camera;
import engine.entities.GameObject;
import engine.entities.Light;
import engine.entities.Player;
import engine.renderEngine.Window;
import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.font.fontMeshCreator.FontType;
import engine.renderEngine.font.fontRendering.TextMaster;
import engine.renderEngine.guis.GuiRenderer;
import engine.renderEngine.guis.GuiTexture;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.particles.*;
import engine.renderEngine.postProcessing.Fbo;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.renderEngine.renderer.MasterRenderer;
import engine.renderEngine.renderer.RenderCullSide;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.TerrainTexture;
import engine.renderEngine.textures.TerrainTexturePack;
import engine.terrain.Terrain;
import engine.toolbox.KeyListener;
import engine.toolbox.MouseListener;
import engine.toolbox.MousePicker;
import engine.toolbox.customVariables.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main { // TODO FIX SAVE AND LOAD META FILES

    public static void main(String[] args) {

        Window.createDisplay();
        TextMaster.init();

        RawModel playerModel = OBJLoader.loadOBJ("Assets/Super_Mario/Super_Mario.obj");
        TexturedModel playerStaticModel = new TexturedModel(playerModel,
                new Material(Loader.get().loadTexture("Assets/Super_Mario/tex/mariobodyfix_alb.png")));

        Player player = new Player(playerStaticModel,
                new Vector3f(-100, 20, -50), new Vector3f(0), new Vector3f(0.6f));

        Camera camera = new Camera(player);
        Window.get().getScene().setCamera(camera);

        MasterRenderer renderer = new MasterRenderer(camera);
        ParticleMaster.init(renderer.getProjectionMatrix());

        FontType font = new FontType(Loader.get().loadTexture("Assets/fonts/candara.png", false).getTextureID(), new File("Assets/fonts/candara.fnt"));
//        GUIText text = new GUIText("Text", 5, font, new Vector2f(0.6f, 0.5f), 0.4f, true); // maxLineLength is horizontal size of text rect (1 - all screen width, 0.5 half of the screen)
//        text.setFontParams(new Color(20, 20, 20), 7, 0.5f, 0.1f);
//        text.setDropShadow(new Color(0, 0, 0, 60), new Vector2f(-0.001f, -0.001f), 0.04f, 0.2f);
//        text.setBorder(new Color(40, 40, 40, 255), 0.1f, 0.1f);

        float[] vertices = {
                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,0.5f,-0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,-0.5f,0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                0.5f,0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                -0.5f,-0.5f,0.5f,
                -0.5f,0.5f,0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,0.5f,-0.5f,
                0.5f,0.5f,-0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,-0.5f,0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f
        };

        float[] textureCoords = {
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0
        };

        int[] indices = {
                0,1,3,
                3,1,2,
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22
        };

        // Entities
        RawModel dragonModel = OBJLoader.loadOBJ("Assets/dragon.obj");
        TexturedModel dragonStaticModel = new TexturedModel(dragonModel,
                new Material(Loader.get().loadTexture("Assets/whitePixel.png")));

        Material dragonTexture = dragonStaticModel.getMaterial();
        dragonTexture.setColor(new Color(0, 255, 0));

        GameObject dragonGameObject = new GameObject("Dragon");
        dragonGameObject.addComponent(new MeshRenderer(dragonStaticModel));
        dragonGameObject.transform.set(new Vector3f(-106.0f, 18.0f, -21.0f), new Vector3f(0.0f), new Vector3f(1.0f));
        Window.get().currentScene.addGameObjectToScene(dragonGameObject);


        RawModel dragonModel2 = OBJLoader.loadOBJ("Assets/dragon.obj");
        TexturedModel dragonStaticModel2 = new TexturedModel(dragonModel2,
                new Material(Loader.get().loadTexture("Assets/whitePixel.png")));
        Material texture2 = dragonStaticModel2.getMaterial();
        texture2.setColor(new Color(237, 145, 8));
        texture2.setShineDumper(14.0f);
        texture2.setReflectivity(3.0f);
        texture2.setCullSided(RenderCullSide.Both);

        GameObject dragonGameObject2 = new GameObject("Dragon2");
        dragonGameObject2.addComponent(new MeshRenderer(dragonStaticModel2));
        dragonGameObject2.transform.set(new Vector3f(-91.0f, 18.0f, -21.0f), new Vector3f(0.0f), new Vector3f(1.0f));
        Window.get().currentScene.addGameObjectToScene(dragonGameObject2);


        RawModel grassModel = OBJLoader.loadOBJ("Assets/grass/grass.obj");
        TexturedModel grassStaticModel = new TexturedModel(grassModel,
                new Material(Loader.get().loadTexture("Assets/grass/grass.png")));
        Material grassTexture = grassStaticModel.getMaterial();

        grassTexture.setAlphaClip(0.05f);
        grassTexture.setUseFakeLighting(true);
        grassTexture.setCullSided(RenderCullSide.Both);

        GameObject grassGameObject = new GameObject("Grass");
        grassGameObject.addComponent(new MeshRenderer(grassStaticModel));
        grassGameObject.transform.set(new Vector3f(-100, 22, -57), new Vector3f(0.0f), new Vector3f(0.01f));
        Window.get().currentScene.addGameObjectToScene(grassGameObject);


        RawModel fernModel = OBJLoader.loadOBJ("Assets/res/fern.obj");
        TexturedModel fernStaticModel = new TexturedModel(fernModel,
                new Material(Loader.get().loadTexture("Assets/res/fern.png")));
        Material fernTexture = fernStaticModel.getMaterial();

        fernTexture.setNumberOfColumns(2);
        fernTexture.setNumberOfRows(2);

        fernTexture.setAlphaClip(0.06f);
        fernTexture.setUseFakeLighting(true);
        fernTexture.setCullSided(RenderCullSide.Both);

        GameObject fernGameObject = new GameObject("Fern");
        fernGameObject.addComponent(new MeshRenderer(fernStaticModel));
        fernGameObject.transform.set(new Vector3f(-93, 19, -50), new Vector3f(0.0f), new Vector3f(0.5f));
        Window.get().currentScene.addGameObjectToScene(fernGameObject);


        GameObject fern2GameObject = new GameObject("Fern2");
        fern2GameObject.addComponent(new MeshRenderer(fernStaticModel.copy(), 3));
        fern2GameObject.transform.set(new Vector3f(-93, 19, -42), new Vector3f(0.0f), new Vector3f(0.5f));
        Window.get().currentScene.addGameObjectToScene(fern2GameObject);


        RawModel barrelModel = OBJLoader.loadOBJ("Assets/res/barrel/barrel.obj");
        TexturedModel barrelStaticModel = new TexturedModel(barrelModel,
                new Material(Loader.get().loadTexture("Assets/res/barrel/barrel.png")));
        Material barrelTexture = barrelStaticModel.getMaterial();

        barrelTexture.setSpecularIntensity(1f);
        barrelTexture.setShineDumper(10);
        barrelTexture.setReflectivity(1);
        barrelTexture.setSpecularMap(Loader.get().loadTexture("Assets/res/barrel/barrelS.png"));

        GameObject barrelGameObject = new GameObject("Barrel");
        barrelGameObject.addComponent(new MeshRenderer(barrelStaticModel));
        barrelGameObject.transform.set(new Vector3f(-110, 27, -70), new Vector3f(0.0f), new Vector3f(0.5f));
        Window.get().currentScene.addGameObjectToScene(barrelGameObject);


        RawModel testSphereModel = OBJLoader.loadOBJ("Assets/pbr-sphere-test/pbr-sphere-test.obj");
        TexturedModel testSphereStaticModel = new TexturedModel(testSphereModel,
//                new Material(Loader.get().loadTexture("Assets/pbr-sphere-test/rusted-metall/rustediron2_basecolor.png")));
                new Material(Loader.get().loadTexture("Assets/pbr-sphere-test/sphere_Base_Color.png")));
        Material testSphereTexture = testSphereStaticModel.getMaterial();

        testSphereTexture.setMetallicMap(Loader.get().loadTexture("Assets/pbr-sphere-test/sphere_Metallic.png"));
//        testSphereTexture.setMetallicMap(Loader.get().loadTexture("Assets/pbr-sphere-test/rusted-metall/rustediron2_metallic.png"));
        testSphereTexture.setMetallicIntensity(1.0f);

        testSphereTexture.setSpecularIntensity(1.0f);
        testSphereTexture.setShineDumper(10.0f);
        testSphereTexture.setReflectivity(1.0f);
        testSphereTexture.setSpecularMap(Loader.get().loadTexture("Assets/pbr-sphere-test/sphere_Roughness.png"));
//        testSphereTexture.setSpecularMap(Loader.get().loadTexture("Assets/whitePixel.png"));

        GameObject testSphereGameObject = new GameObject("Test Sphere");
        testSphereGameObject.addComponent(new MeshRenderer(testSphereStaticModel));
        testSphereGameObject.transform.set(new Vector3f(-110, 28, -48), new Vector3f(0.0f), new Vector3f(3.0f));
        Window.get().currentScene.addGameObjectToScene(testSphereGameObject);


        RawModel lanternModel = OBJLoader.loadOBJ("Assets/res/lantern/lantern.obj");
        TexturedModel lanternStaticModel = new TexturedModel(lanternModel,
                new Material(Loader.get().loadTexture("Assets/res/lantern/lantern.png")));
        Material lanternTexture = lanternStaticModel.getMaterial();

        lanternTexture.setEmissionIntensity(1.3f);
        lanternTexture.setEmissionMap(Loader.get().loadTexture("Assets/res/lantern/lanternS.png"));
        lanternTexture.setUseAlbedoEmission(true);

        GameObject lanternGameObject = new GameObject("Lantern");
        lanternGameObject.addComponent(new MeshRenderer(lanternStaticModel));
        lanternGameObject.transform.set(new Vector3f(-90, 25, -70), new Vector3f(0.0f), new Vector3f(0.5f));
        Window.get().currentScene.addGameObjectToScene(lanternGameObject);


        RawModel model = Loader.get().loadToVAO(vertices, textureCoords, new float[0], indices, "_Generated(Cube)");
        TexturedModel staticModel = new TexturedModel(model,
                new Material(Loader.get().loadTexture("Assets/metalTexture.png")));

        GameObject hardCode_cubeGameObject = new GameObject("HardCode Cube");
        hardCode_cubeGameObject.transform.set(new Vector3f(-79.0f, 18.8f, -22.1f), new Vector3f(0.0f), new Vector3f(5.0f));
        MeshRenderer goMeshRenderer = new MeshRenderer(staticModel);
        goMeshRenderer.getModel().getMaterial().setCullSided(RenderCullSide.Both);
        goMeshRenderer.getModel().getMaterial().setTiling(new Vector2f(1.0f, 1.0f));
        hardCode_cubeGameObject.addComponent(goMeshRenderer);
        Window.get().currentScene.addGameObjectToScene(hardCode_cubeGameObject);
//        // Entities

        // normalMapEntity
        List<GameObject> normalMapEntities = new ArrayList<>();
//
//        TexturedModel barrelStaticModel = new TexturedModel(
//                NormalMappedObjLoader.loadOBJ("Assets/normalMapObjects/barrel.obj", loader),
//                new ModelTexture(loader.loadTexture("Assets/normalMapObjects/barrel.png")));
//
//        ModelTexture barrelTexture = barrelStaticModel.getTexture();
//        barrelTexture.setShineDumper(10);
//        barrelTexture.setReflectivity(0.5f);
//
//        Entity barrelEntity = new Entity(barrelStaticModel,
//                new Vector3f(-75, 20, -75), new Vector3f(0), new Vector3f(0.0001f));
//        normalMapEntities.add(barrelEntity);
        // normalMapEntity

        // TERRAIN
        List<Terrain> terrains = new ArrayList<>();

        TerrainTexture backgroundTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/grassy2.png").getTextureID());
        TerrainTexture rTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/mud.png").getTextureID());
        TerrainTexture gTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/grassFlowers.png").getTextureID());
        TerrainTexture bTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/path.png").getTextureID());

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(Loader.get().loadTexture("Assets/res/blendMap.png").getTextureID());

        terrains.add(new Terrain(-1, -1, texturePack, blendMap, "Assets/heightmap1.png"));
        // TERRAIN

        // GUIS
        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(Loader.get().loadTexture("Assets/testGui.png"), new Vector2f(-0.8f, 0.8f), 0, new Vector2f(0.2f, 0.2f));
        guis.add(gui);

        GuiTexture gui2 = new GuiTexture(Loader.get().loadTexture("Assets/testGui.png"), new Vector2f(0.8f, -0.8f), 0, new Vector2f(0.2f, 0.2f));
        guis.add(gui2);

//        GuiTexture shadowMapGui = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.4f, 0.3f), 0, new Vector2f(0.2f, 0.2f));
//        guis.add(shadowMapGui);

//        GuiTexture reflectionsMapGui = new GuiTexture(renderer.getReflectionsMap(), new Vector2f(0.4f, -0.3f), 0, new Vector2f(0.2f, 0.2f));
//        guis.add(reflectionsMapGui);

        GuiRenderer guiRenderer = new GuiRenderer();
        // GUIS

        // Light
        List<Light> lights = new ArrayList<>();

        Light sun = new Light(new Vector3f(100_000.0f, 150_000.0f, 100_000.0f), Color.White, 1.0f);
        lights.add(sun);

//        lights.add(new Light(new Vector3f(-57.0f, 20.0f, -53.0f), Color.Green, 3.5f, new Vector3f(1, 0.01f, 0.002f)));
//        lights.add(new Light(new Vector3f(-53.0f, 20.0f, -57.0f), Color.Red, 3.5f, new Vector3f(1, 0.01f, 0.002f)));

        lights.add(new Light(new Vector3f(-90, 27, -70), Color.Yellow, 1.5f, new Vector3f(1, 0.01f, 0.002f)));
        // Light

        Window.get().currentScene.addGameObjectToScene(player);
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));

        // Particles
//        ParticleSystem simpleParticleSystem = new ParticleSystem(50, 25, 0.3f, 4);
        ParticleTexture particleTexture = new ParticleTexture(Loader.get().loadTexture("Assets/fireParticles.png").getTextureID(), 6, 6);
        particleTexture.setAdditive(true);
        ParticleSystem complexParticleSystem = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
        complexParticleSystem.randomizeRotation();
        complexParticleSystem.setUseBlend(true);
        complexParticleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
        complexParticleSystem.setLifeError(0.1f);
        complexParticleSystem.setSpeedError(0.4f);
        complexParticleSystem.setScaleError(0.8f);
        // Particles

        Fbo multisampleSceneFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), false);
        Fbo outputFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), Fbo.DEPTH_TEXTURE);

        PostProcessing.init();

//        GuiTexture cameraOutputGui = new GuiTexture(0, new Vector2f(0.45f, -0.25f), 0, new Vector2f(0.3f, 0.3f));
//        guis.add(cameraOutputGui); // TODO this is camera output image

        StaticShader pickingShader = new StaticShader("engineFiles/shaders/util/pickingVertexShader.glsl", "engineFiles/shaders/util/pickingFragmentShader.glsl");

        while (!Window.isClosed()) {
            // Poll events
            glfwPollEvents();

            // Put update logic before rendering
            player.move(terrains.get(0));
            camera.move();
            picker.update();

            // Test
//            particleSystem.generateParticles(player.getPosition());
            complexParticleSystem.generateParticles(player.transform.position); // Same particle system, in multiple places
            complexParticleSystem.generateParticles(new Vector3f(-100, 30, -100));
//            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
//                new Particle(new Vector3f(player.getPosition()), new Vector3f(0, 30, 0), 1, 4, 0, 1);
//            }
            // Test

            // Render pass 1. Render to picking texture
//            glDisable(GL_BLEND);
            Window.get().pickingTexture.enableWriting();

            glViewport(0, 0, (int) Window.getWidth(), (int) Window.getHeight());
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            pickingShader.start();
            Window.get().currentScene.render();
            pickingShader.stop();

            Window.get().pickingTexture.disableWriting();
//            glEnable(GL_BLEND);

            ParticleMaster.update(camera);

            renderer.renderShadowMap(Window.get().currentScene.getGameObjects(), sun);

            if (Window.get().runtimePlaying)
                Window.get().currentScene.update();
            else
                Window.get().currentScene.editorUpdate();

            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (KeyListener.keyBeginPress(GLFW_KEY_E))
                if (terrainPoint != null) {
                    hardCode_cubeGameObject.transform.position = terrainPoint.add(new Vector3f(0, 2.0f, 0));
            }

            dragonGameObject.transform.increaseRotation(new Vector3f(0, 1, 0));
            dragonGameObject2.transform.increaseRotation(new Vector3f(0, 1, 0));
            grassGameObject.transform.increaseRotation(new Vector3f(0, -0.3f, 0));
            hardCode_cubeGameObject.transform.increaseRotation(new Vector3f(1, 1, 0));
            barrelGameObject.transform.increaseRotation(new Vector3f(1, 1, 0));

            gui2.getTexture().setTextureID(TestFieldsWindow.testInts[0]);

//            shadowMapGui.setRotation(shadowMapGui.getRotation() + 1);

            multisampleSceneFbo.bindFrameBuffer(); // all inside this affected by postProcessing

            renderer.renderScene(Window.get().currentScene.getGameObjects(), normalMapEntities, terrains, lights, camera);
            ParticleMaster.renderParticles(camera);

//            cameraOutputGui.setTexture(PostProcessing.getFinalImage());

            // render gui after all worlds
            guiRenderer.render(guis);
            TextMaster.render();

            multisampleSceneFbo.unbindFrameBuffer(); // all inside this affected by postProcessing

            multisampleSceneFbo.resolveToFbo(GL_COLOR_ATTACHMENT0, outputFbo);
            if (false)
                Window.setScreenImage(outputFbo.getColourTexture()); // Not PostProcessing at all
            else {
                PostProcessing.doPostProcessing(outputFbo.getColourTexture());
                Window.setScreenImage(PostProcessing.getFinalImage());
            }

            Window.get().getImGuiLayer().update(Window.getDelta(), Window.get().currentScene);

            Window.updateDisplay();

            MouseListener.endFrame();
            KeyListener.endFrame();
        }

        PostProcessing.cleanUp();
        multisampleSceneFbo.cleanUp();
        outputFbo.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        Loader.get().cleanUp();
        Window.closeDisplay();
    }
}
