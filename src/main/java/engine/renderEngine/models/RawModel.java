package engine.renderEngine.models;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private String filepath;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filepath = "_Generated";
    }

    public RawModel(int vaoID, int vertexCount, String filepath) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filepath = filepath;
    }

    public int getVaoID() { return this.vaoID; }

    public int getVertexCount() { return this.vertexCount; }

    public String getFilepath() { return this.filepath; }

    public float getSizeMultiplayer() { return 1.0f; }
}
