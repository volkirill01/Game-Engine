package engine.renderEngine.models;

public class RawModel {

    private int vaoID;
    private int vertexCount;
    private String filePath;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filePath = "_Generated";
    }

    public RawModel(int vaoID, int vertexCount, String filePath) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.filePath = filePath;
    }

    public int getVaoID() { return this.vaoID; }

    public int getVertexCount() { return this.vertexCount; }

    public String getFilePath() { return this.filePath; }
}
