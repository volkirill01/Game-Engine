package engine.renderEngine.textures;

import engine.renderEngine.renderer.RenderCullSide;
import engine.toolbox.customVariables.Color;
import org.joml.Vector2f;

public class Material {

    private int textureID;
    private int textureWidth = 64; // TODO REPLACE THIS WITH TEXTURE CLASS
    private int textureHeight = 64;

    private int specularMap = 0;
    private float specularIntensity = 0.0f;

    private int emissionMap = 0;
    private float emissionIntensity = 0.0f;
    private boolean useAlbedoEmission = false;

    private Vector2f tiling = new Vector2f(1.0f, 1.0f);

    private Color color = new Color(255, 255, 255);
    private float alphaClip = 0.0f;

    private float shineDumper = 1.0f;
    private float reflectivity = 0.0f;

    private RenderCullSide renderCullSide = RenderCullSide.Front;

    private boolean useFakeLighting = false;

    private int numberOfRows = 1;
    private int numberOfColumns = 1;

    public Material(int id) { this.textureID = id; }

    public void setSpecularMap(int specularMap) {
        this.specularMap = specularMap;
        if (this.specularIntensity <= 0)
            this.specularIntensity = 0.5f;
    }

    public boolean hasSpecular() { return this.specularMap != 0 && this.specularIntensity > 0; }

    public void setSpecularIntensity(float specularIntensity) { this.specularIntensity = specularIntensity; }

    public float getSpecularIntensity() { return this.specularIntensity; }

    public int getSpecularMap() { return this.specularMap; }

    public void setEmissionMap(int emissionMap) {
        this.emissionMap = emissionMap;
        if (this.emissionIntensity <= 0)
            this.emissionIntensity = 0.5f;
    }

    public boolean hasEmission() { return this.emissionMap != 0 && this.emissionIntensity > 0; }

    public void setEmissionIntensity(float emissionIntensity) { this.emissionIntensity = emissionIntensity; }

    public float getEmissionIntensity() { return this.emissionIntensity; }

    public int getEmissionMap() { return this.emissionMap; }

    public boolean isUseAlbedoEmission() { return this.useAlbedoEmission; }

    public void setUseAlbedoEmission(boolean useAlbedoEmission) { this.useAlbedoEmission = useAlbedoEmission; }

    public int getNumberOfRows() { return this.numberOfRows; }

    public void setNumberOfRows(int numberOfRows) { this.numberOfRows = numberOfRows; }

    public int getNumberOfColumns() { return this.numberOfColumns; }

    public void setNumberOfColumns(int numberOfColumns) { this.numberOfColumns = numberOfColumns; }

    public int getID() { return this.textureID; }

    public void setTexture(int newTexture) { this.textureID = newTexture; }

    public int getTextureWidth() { return this.textureWidth; }

    public int getTextureHeight() { return this.textureHeight; }

    public Vector2f getTiling() { return this.tiling; }

    public void setTiling(Vector2f tiling) { this.tiling = tiling; }

    public Color getColor() { return this.color; }

    public void setColor(Color color) { this.color = color; }

    public float getAlphaClip() { return this.alphaClip; }

    public void setAlphaClip(float alphaClip) { this.alphaClip = alphaClip; }

    public float getShineDumper() { return this.shineDumper; }

    public void setShineDumper(float shineDumper) { this.shineDumper = shineDumper; }

    public float getReflectivity() { return this.reflectivity; }

    public void setReflectivity(float reflectivity) { this.reflectivity = reflectivity; }

    public RenderCullSide getCullSide() { return this.renderCullSide; }

    public void setCullSided(RenderCullSide cullSide) { this.renderCullSide = cullSide; }

    public boolean isUseFakeLighting() { return this.useFakeLighting; }

    public void setUseFakeLighting(boolean useFakeLighting) { this.useFakeLighting = useFakeLighting; }
}
