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

    public Texture(int textureID, String filepath, int width, int height) {
        this.textureID = textureID;
        this.filepath = filepath;
        this.width = width;
        this.height = height;

        this.metaFilepath = this.filepath;
        this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultImage.meta");
    }

    public void update() {
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

    public TextureSliceMode getSliceMode() { return TextureSliceMode.valueOf(this.metaData.get("sliceMode").toString()); }

    public void setSliceMode(TextureSliceMode sliceMode) { this.metaData.replace("sliceMode", sliceMode); }

    public TextureFilterMode getFilterMode() { return TextureFilterMode.valueOf(this.metaData.get("filterMode").toString()); }

    public void setFilterMode(TextureFilterMode filterMode) { this.metaData.replace("filterMode", filterMode);; }

    public boolean isUseMipmaps() { return Boolean.parseBoolean(this.metaData.get("useMipmaps").toString()); }

    public void setUseMipmaps(boolean useMipmaps) {
        this.metaData.replace("useAnisotropicFiltering", !useMipmaps);
        this.metaData.replace("useMipmaps", useMipmaps);
    }

    public boolean isUseAnisotropicFiltering() { return Boolean.parseBoolean(this.metaData.get("useAnisotropicFiltering").toString()); }

    public void setUseAnisotropicFiltering(boolean useAnisotropicFiltering) {
        this.metaData.replace("useMipmaps", !useAnisotropicFiltering);
        this.metaData.replace("useAnisotropicFiltering", useAnisotropicFiltering);
    }

    public boolean isRepeatHorizontally() { return Boolean.parseBoolean(this.metaData.get("repeatHorizontally").toString()); }

    public void setRepeatHorizontally(boolean repeatHorizontally) { this.metaData.replace("repeatHorizontally", repeatHorizontally); }

    public boolean isRepeatVertically() { return Boolean.parseBoolean(this.metaData.get("repeatVertically").toString()); }

    public void setRepeatVertically(boolean repeatVertically) { this.metaData.replace("repeatVertically", repeatVertically); }

    public int getNumberOfRows() { return Integer.parseInt(this.metaData.get("numberOfRows").toString()); }

    public void setNumberOfRows(int numberOfRows) { this.metaData.replace("numberOfRows", numberOfRows); }

    public int getNumberOfColumns() { return Integer.parseInt(this.metaData.get("numberOfColumns").toString()); }

    public void setNumberOfColumns(int numberOfColumns) { this.metaData.replace("numberOfColumns", numberOfColumns); }
}
