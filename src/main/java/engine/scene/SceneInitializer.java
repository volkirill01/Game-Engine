package engine.scene;

public abstract class SceneInitializer {
    public String scenePath;

    public abstract void init(Scene scene);

    public String getScenePath() { return scenePath; }
}
