package engine.renderEngine.textures;

public class Texture {

    private int textureID;
    private String filepath;

    private int width;
    private int height;

    private TextureSliceMode sliceMode = TextureSliceMode.Single;

    public Texture(int textureID, String filepath, int width, int height) {
        this.textureID = textureID;
        this.filepath = filepath;
        this.width = width;
        this.height = height;
    }

    public int getTextureID() { return this.textureID; }

    public String getFilepath() { return this.filepath; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public void setTextureID(int newID) { this.textureID = newID; } // TODO DELETE THIS

    public TextureSliceMode getSliceMode() { return this.sliceMode; }

    public void setSliceMode(TextureSliceMode sliceMode) { this.sliceMode = sliceMode; }
}
