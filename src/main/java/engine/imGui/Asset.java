package engine.imGui;

public class Asset {
    public String assetPath;
    public String assetName;
    public AssetType assetType;
    public int fileIcon;

    public Asset(String assetPath, String assetName, AssetType assetType, int fileIcon) {
        this.assetPath = assetPath;
        this.assetName = assetName;
        this.assetType = assetType;
        this.fileIcon = fileIcon;
    }

    public enum AssetType {
        Folder,
        NewFolder,
        Scene,
        NewScene,
        Image,
        Model,
        Sound,
        Font,
        Shader,
        Other
    }
}
