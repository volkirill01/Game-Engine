import engine.entities.EditorCamera;
import engine.entities.GameObject;
import engine.gizmo.GizmoSystem;
import engine.renderEngine.Window;
import engine.renderEngine.Loader;
import engine.renderEngine.font.fontRendering.TextMaster;
import engine.renderEngine.guis.UIRenderer;
import engine.renderEngine.particles.*;
import engine.renderEngine.postProcessing.Fbo;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.renderEngine.renderer.MasterRenderer;
import engine.renderEngine.shadows2.ShadowMapRenderer;
import engine.terrain.Terrain;
import engine.toolbox.GameObject_Manager;
import engine.toolbox.Keyboard_EditorActions;
import engine.toolbox.Mouse_EditorActions;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.MouseListener;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    public static void main(String[] args) {

        Window.createDisplay();
        TextMaster.init();

        EditorCamera editorCamera = new EditorCamera();
        Window.get().getScene().setCamera(editorCamera);

        MasterRenderer renderer = new MasterRenderer(editorCamera);
        ParticleMaster.init(renderer.getProjectionMatrix());

//        FontType font = new FontType(Loader.get().loadTexture("Assets/fonts/candara.png", false).getTextureID(), new File("Assets/fonts/candara.fnt"));
//        GUIText text = new GUIText("Text", 5, font, new Vector2f(0.6f, 0.5f), 0.4f, true); // maxLineLength is horizontal size of text rect (1 - all screen width, 0.5 half of the screen)
//        text.setFontParams(new Color(20, 20, 20), 7, 0.5f, 0.1f);
//        text.setDropShadow(new Color(0, 0, 0, 60), new Vector2f(-0.001f, -0.001f), 0.04f, 0.2f);
//        text.setBorder(new Color(40, 40, 40, 255), 0.1f, 0.1f);

//        // normalMapEntity
        List<GameObject> normalMapEntities = new ArrayList<>();
////
////        TexturedModel barrelStaticModel = new TexturedModel(
////                NormalMappedObjLoader.loadOBJ("Assets/normalMapObjects/barrel.obj", loader),
////                new ModelTexture(loader.loadTexture("Assets/normalMapObjects/barrel.png")));
////
////        ModelTexture barrelTexture = barrelStaticModel.getTexture();
////        barrelTexture.setShineDumper(10);
////        barrelTexture.setReflectivity(0.5f);
////
////        Entity barrelEntity = new Entity(barrelStaticModel,
////                new Vector3f(-75, 20, -75), new Vector3f(0), new Vector3f(0.0001f));
////        normalMapEntities.add(barrelEntity);
//        // normalMapEntity
//
//        // TERRAIN
        List<Terrain> terrains = new ArrayList<>();
//
//        TerrainTexture backgroundTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/grassy2.png").getTextureID());
//        TerrainTexture rTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/mud.png").getTextureID());
//        TerrainTexture gTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/grassFlowers.png").getTextureID());
//        TerrainTexture bTexture = new TerrainTexture(Loader.get().loadTexture("Assets/res/path.png").getTextureID());
//
//        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//        TerrainTexture blendMap = new TerrainTexture(Loader.get().loadTexture("Assets/res/blendMap.png").getTextureID());
//
//        terrains.add(new Terrain(-1, -1, texturePack, blendMap, "Assets/heightmap1.png"));
//        // TERRAIN
//
//        // GUIS
////        GuiTexture shadowMapGui = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.4f, 0.3f), 0, new Vector2f(0.2f, 0.2f));
////        guis.add(shadowMapGui);
//
////        GuiTexture reflectionsMapGui = new GuiTexture(renderer.getReflectionsMap(), new Vector2f(0.4f, -0.3f), 0, new Vector2f(0.2f, 0.2f));
////        guis.add(reflectionsMapGui);

        UIRenderer UIRenderer = new UIRenderer();
//        // GUIS

//        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));

        Fbo multisampleSceneFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), true, false);
        Fbo outputFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo uiFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), Fbo.NONE);
        Fbo shadowFbo = new Fbo((int) Window.getWidth(), (int) Window.getHeight(), Fbo.DEPTH_TEXTURE);

        PostProcessing.init();

//        GuiTexture cameraOutputGui = new GuiTexture(0, new Vector2f(0.45f, -0.25f), 0, new Vector2f(0.3f, 0.3f));
//        guis.add(cameraOutputGui); // TODO this is camera output image

        GizmoSystem gizmoSystem = new GizmoSystem();
        Window.get().getImGuiLayer().getGameViewWindow().setGizmoSystem(gizmoSystem);

        Keyboard_EditorActions keyboardEditorActions = new Keyboard_EditorActions();
        Mouse_EditorActions mouseEditorActions = new Mouse_EditorActions();

        while (!Window.isClosed()) {
            glfwPollEvents();

            gizmoSystem.update();

            // Put update logic before rendering
            editorCamera.move();

//            shadowFbo.bindFrameBuffer();
//            renderer.renderShadowMap(Window.get().getScene().getGameObjects(), editorCamera); // TODO FINISH THAT
//            shadowFbo.unbindFrameBuffer();

            // Render pass 1. Render to picking texture
            Window.get().pickingTexture.enableWriting();
            glViewport(0, 0, (int) Window.getWidth(), (int) Window.getHeight());
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderer.renderScene(Window.get().getScene().getGameObjects(), normalMapEntities, terrains, editorCamera, true, 0);
            Window.get().pickingTexture.disableWriting();

            keyboardEditorActions.update();
            mouseEditorActions.update();

            ParticleMaster.update(editorCamera);

//            renderer.renderShadowMap(Window.get().getScene().getGameObjects());

            if (Window.get().runtimePlaying)
                Window.get().getScene().update();
            else
                Window.get().getScene().editorUpdate();

//            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//            if (KeyListener.keyBeginPress(KeyCode.E))
//                if (terrainPoint != null) {
//                    hardCode_cubeGameObject.transform.position = terrainPoint.add(new Vector3f(0, 2.0f, 0));
//            }
//
//            gui2.getTexture().setTextureID(TestFieldsWindow.getInts[0]);

//            shadowMapGui.setRotation(shadowMapGui.getRotation() + 1);

            multisampleSceneFbo.bindFrameBuffer(); // all inside this affected by postProcessing

//            glActiveTexture(GL_TEXTURE5);
//            glBindTexture(GL_TEXTURE_2D, shadowFbo.getDepthTexture());
            renderer.renderScene(Window.get().getScene().getGameObjects(), normalMapEntities, terrains, editorCamera, false, shadowFbo.getDepthTexture());
            ParticleMaster.renderParticles(editorCamera);

//            cameraOutputGui.setTexture(PostProcessing.getFinalImage());

            multisampleSceneFbo.unbindFrameBuffer(); // all inside this affected by postProcessing

            uiFbo.bindFrameBuffer();

            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // render gui after all worlds
            UIRenderer.render();
            TextMaster.render();

            uiFbo.unbindFrameBuffer();

            multisampleSceneFbo.resolveToFbo(GL_COLOR_ATTACHMENT0, outputFbo);
            if (false) // Not PostProcessing at all
                Window.setScreenImage(outputFbo.getColourTexture());
            else {
                PostProcessing.doPostProcessing(outputFbo.getColourTexture());
                Window.setScreenImage(PostProcessing.getFinalImage());
            }
            Window.setUIImage(uiFbo.getColourTexture());

//            Window.setScreenImage(Window.get().getImGuiLayer().getInspectorWindow().getPickingTexture().getPickingTextureId()); // Picking Texture Test
//            Window.setScreenImage(renderer.getShadowMapTexture()); // Shadow Map Test

            if (false) { // Render only scene
                outputFbo.resolveToScreen();
            } else {
//                Window.setScreenImage(Window.get().pickingTexture.getPickingTextureId());
//                Window.setScreenImage(renderer.getShadowMapTexture());
//                Window.setScreenImage(shadowFbo.getColourTexture());
                Window.get().getImGuiLayer().update();
            }

            Window.get().updateDisplay();

            MouseListener.endFrame();
            KeyListener.endFrame();
        }

        PostProcessing.cleanUp();
        multisampleSceneFbo.cleanUp();
        outputFbo.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        UIRenderer.cleanUp();
        renderer.cleanUp();
        Loader.get().cleanUp();
        Window.closeDisplay();

        System.exit(0);
    }
}
