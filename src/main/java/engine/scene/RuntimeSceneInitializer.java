package engine.scene;

public class RuntimeSceneInitializer extends SceneInitializer {
    public RuntimeSceneInitializer(String scenePath) { this.scenePath = scenePath; }

    @Override
    public void init(Scene scene) {
//        GameObject cameraObject = scene.createGameObject("GameCamera");
//        cameraObject.addComponent(new GameCamera(scene.camera()));
//        cameraObject.start();
//        scene.addGameObjectToScene(cameraObject);
    }
}
