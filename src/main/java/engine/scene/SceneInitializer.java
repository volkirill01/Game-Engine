package engine.scene;

public abstract class SceneInitializer {
    public String scenePath;

    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imgui();

    public String getScenePath() { return scenePath; }
}
