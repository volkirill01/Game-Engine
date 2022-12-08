package engine.scene;

import engine.entities.GameObject;

public class LevelSceneInitializer extends SceneInitializer {
    public LevelSceneInitializer(String scenePath) { this.scenePath = scenePath; }

    @Override
    public void init(Scene scene) {
        GameObject cameraObject = scene.createGameObject("GameCamera");
//        cameraObject.addComponent(new GameCamera(scene.camera()));
        cameraObject.start();
        scene.addGameObjectToScene(cameraObject);
    }

    @Override
    public void loadResources(Scene scene) {
//        for (GameObject g: scene.getGameObjects()) {
//            if (g.getComponent(SpriteRenderer.class) != null) {
//                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
//                if (spr.getTexture() != null)
//                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
//            }
//
//            if (g.getComponent(AnimationStateMachine.class) != null) {
//                AnimationStateMachine stateMachine = g.getComponent(AnimationStateMachine.class);
//                stateMachine.refreshTextures();
//            }
//        }
    }

    @Override
    public void imgui() { }
}
