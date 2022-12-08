package engine.scene;

import engine.renderEngine.Window;

public class SceneManager {

    private static String currentScene;

    public static void loadScene(String scenePath) {
        Window.get().changeScene(new LevelEditorSceneInitializer(scenePath));
        currentScene = scenePath;
    }

    public static String getCurrentScene() { return currentScene; }
}
