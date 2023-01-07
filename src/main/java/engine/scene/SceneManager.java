package engine.scene;

import engine.renderEngine.Window;

import javax.swing.*;

public class SceneManager {

    private static String currentScene;

    public static void loadScene(String scenePath) { loadScene(scenePath, true); }

    public static void loadScene(String scenePath, boolean showConfirmDialog) {
        if (showConfirmDialog) {
            JPanel panel = new JPanel();
            panel.add(new JLabel("Save Scene before open another?"));

            int res = JOptionPane.showConfirmDialog(null, panel, "Save Scene",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (res == JOptionPane.CANCEL_OPTION)
                return;

            if (res == JOptionPane.YES_OPTION || res == JOptionPane.NO_OPTION)
                load(scenePath);
        } else
            load(scenePath);
    }

    private static void load(String scenePath) {
        Window.get().changeScene(new EditorSceneInitializer(scenePath));
        currentScene = scenePath;
    }

    public static String getCurrentScene() { return currentScene; }
}
