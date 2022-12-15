package engine.renderEngine.textures;

import engine.renderEngine.Loader;

import java.util.Map;

public class Texture {

    private int textureID;
    private String filepath;

    private String metaFilepath;
    private Map<String, Object> metaData;

    private int width;
    private int height;

    private TextureSliceMode sliceMode = TextureSliceMode.Single;
    private FilterMode filterMode = FilterMode.Bilinear;

    private boolean useMipmaps = false;
    private boolean useAnisotropicFiltering = true;

    public Texture(int textureID, String filepath, int width, int height) {
        this.textureID = textureID;
        this.filepath = filepath;
        this.width = width;
        this.height = height;

        this.metaFilepath = this.filepath;
        this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultImage.meta");
    }

    public void update() {
        Loader.get().updateTexture(this);
        if (!this.filepath.equals("_Generated"))
            this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultImage.meta");
    }

    public int getTextureID() { return this.textureID; }

    public String getFilepath() { return this.filepath; }

    public int getWidth() { return this.width; }

    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return this.height; }

    public void setHeight(int height) { this.height = height; }

    public void setTextureID(int newID) { this.textureID = newID; } // TODO DELETE THIS

    public TextureSliceMode getSliceMode() { return this.sliceMode; }

    public void setSliceMode(TextureSliceMode sliceMode) { this.sliceMode = sliceMode; }

    public FilterMode getFilterMode() { return this.filterMode; }

    public void setFilterMode(FilterMode filterMode) { this.filterMode = filterMode; }

    public boolean isUseMipmaps() { return this.useMipmaps; }

    public void setUseMipmaps(boolean useMipmaps) {
        this.useAnisotropicFiltering = !useMipmaps;
        this.useMipmaps = useMipmaps;
    }

    public boolean isUseAnisotropicFiltering() { return this.useAnisotropicFiltering; }

    public void setUseAnisotropicFiltering(boolean useAnisotropicFiltering) {
        this.useMipmaps = !useAnisotropicFiltering;
        this.useAnisotropicFiltering = useAnisotropicFiltering;
    }

    public boolean isRepeatHorizontally() { return Boolean.parseBoolean(this.metaData.get("repeatHorizontally").toString()); }

    public void setRepeatHorizontally(boolean repeatHorizontally) { this.metaData.replace("repeatHorizontally", repeatHorizontally); }

    public boolean isRepeatVertically() { return Boolean.parseBoolean(this.metaData.get("repeatHorizontally").toString()); }

    public void setRepeatVertically(boolean repeatVertically) { this.metaData.replace("repeatVertically", repeatVertically); }
}
