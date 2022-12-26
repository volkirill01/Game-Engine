package engine.renderEngine.models;

public class RawModel {

    private transient int vaoID;
    private transient int vertexCount;
    private transient String materialGroup;

    public RawModel(int vaoID, int vertexCount, String materialGroup) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.materialGroup = materialGroup;
    }

    public int getVaoID() { return this.vaoID; }

    public int getVertexCount() { return this.vertexCount; }

    public String getMaterialGroup() { return this.materialGroup; }
}
