package engine.renderEngine.particles;

public class ParticleTexture {

    private int textureID;
    private int numberOfRows;
    private int numberOfColumns;

    private boolean additive;

    public ParticleTexture(int textureID, int numberOfRows, int numberOfColumns) {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
    }

    public int getTextureID() { return this.textureID; }

    public int getNumberOfRows() { return this.numberOfRows; }

    public int getNumberOfColumns() { return this.numberOfColumns; }

    public boolean isAdditive() { return this.additive; }

    public void setAdditive(boolean additive) { this.additive = additive; }
}
