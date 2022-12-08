package engine.renderEngine.textures;

public class TerrainTexturePack {

    private TerrainTexture backgroundTexture;
    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture getBackgroundTexture() { return this.backgroundTexture; }

    public TerrainTexture getRTexture() { return this.rTexture; }

    public TerrainTexture getGTexture() { return this.gTexture; }

    public TerrainTexture getBTexture() { return this.bTexture; }
}
