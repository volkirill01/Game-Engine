package engine.renderEngine.models;

import engine.renderEngine.Loader;

import java.util.Map;

public class RawModel {

    private transient int vaoID;
    private transient int vertexCount;
    private String filepath;

    private String metaFilepath;
    private transient Map<String, Object> metaData;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filepath = "_Generated";
    }

    public RawModel(int vaoID, int vertexCount, String filepath) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filepath = filepath;

        this.metaFilepath = this.filepath;
        this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultModel.meta", true);
    }

    public void update() {
        if (!this.filepath.equals("_Generated"))
            this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultModel.meta", true);
    }

    public int getVaoID() { return this.vaoID; }

    public int getVertexCount() { return this.vertexCount; }

    public String getFilepath() { return this.filepath; }

    public float getSizeMultiplayer() {
        if (!this.filepath.equals("_Generated"))
            return Float.parseFloat(this.metaData.get("modelSize").toString());
        return 1.0f;
    }
}
