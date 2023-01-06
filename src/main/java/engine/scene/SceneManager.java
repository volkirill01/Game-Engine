package engine.scene;

import engine.renderEngine.Window;

public class SceneManager {

    private static String currentScene;

    public static void loadScene(String scenePath) {
        Window.get().changeScene(new EditorSceneInitializer(scenePath));
        currentScene = scenePath;
    }

    public static String getCurrentScene() { return currentScene; }
}
