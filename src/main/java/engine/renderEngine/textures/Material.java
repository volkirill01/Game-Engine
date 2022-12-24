package engine.renderEngine.textures;

import engine.assets.Asset;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.renderer.RenderCullSide;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class Material {

    private String filepath;
    private Map<String, Object> data = new HashMap<>();

    private Texture texture;

    private Texture metallicMap;
    private float metallicIntensity = 0.0f;

    private Texture specularMap;
    private float specularIntensity = 0.0f;
    private float shineDumper = 10.0f;
    private float reflectivity = 0.0f;

    private Texture emissionMap;
    private float emissionIntensity = 0.0f;
    private boolean useAlbedoEmission = false;

    private Vector2f tiling = new Vector2f(1.0f, 1.0f);

    private Color color = new Color(255, 255, 255);
    private float alphaClip = 0.0f;

    private RenderCullSide renderCullSide = RenderCullSide.Front;

    private boolean useFakeLighting = false;

    public String getFilepath() { return this.filepath; }

    public Map<String, Object> getData() { return this.data; }

    public Material(Texture texture) { this.texture = texture; }

    public Material(Map<String, Object> data) { this.data = data; }

    public void update() {
        this.texture.update();
        Loader.get().saveMaterial(this); // TODO SAVE IN INSPECTOR
    }

    public void setMetallicMap(Texture metallicMap) {
        this.metallicMap = metallicMap;
        if (this.metallicIntensity <= 0)
            this.metallicIntensity = 0.5f;
    }

    public boolean hasMetallic() {
        if (this.metallicMap == null)
            return false;
        return this.metallicMap.getTextureID() != 0 && this.metallicIntensity > 0;
    }

    public void setMetallicIntensity(float metallicIntensity) { this.metallicIntensity = metallicIntensity; }

    public float getMetallicIntensity() { return this.metallicIntensity; }

    public int getMetallicMap() { return this.metallicMap.getTextureID(); }

    public void setSpecularMap(Texture specularMap) {
        this.specularMap = specularMap;
        if (this.specularIntensity <= 0)
            this.specularIntensity = 0.5f;
    }

    public boolean hasSpecular() {
        if (this.specularMap == null)
            return false;
        return this.specularMap.getTextureID() != 0 && this.specularIntensity > 0;
    }

    public void setSpecularIntensity(float specularIntensity) { this.specularIntensity = specularIntensity; }

    public float getSpecularIntensity() { return this.specularIntensity; }

    public Texture getSpecularMap() { return this.specularMap; }

    public void setEmissionMap(Texture emissionMap) {
        this.emissionMap = emissionMap;
        if (this.emissionIntensity <= 0)
            this.emissionIntensity = 0.5f;
    }

    public boolean hasEmission() {
        if (this.emissionMap == null)
            return false;
        return this.emissionMap.getTextureID() != 0 && this.emissionIntensity > 0;
    }

    public void setEmissionIntensity(float emissionIntensity) { this.emissionIntensity = emissionIntensity; }

    public float getEmissionIntensity() { return this.emissionIntensity; }

    public Texture getEmissionMap() { return this.emissionMap; }

    public boolean isUseAlbedoEmission() { return this.useAlbedoEmission; }

    public void setUseAlbedoEmission(boolean useAlbedoEmission) { this.useAlbedoEmission = useAlbedoEmission; }

    public Texture getTexture() { return this.texture; }

    public void setTexture(Texture newTexture) { this.texture = newTexture; }

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

    public void imgui() {
        if (EditorImGui.collapsingHeader("Material")) {
            EditorImGui.header("Albedo");
            EditorImGui.filed_Color("Color", color);
            this.texture = (Texture) EditorImGui.field_Asset("Albedo", this.texture, Asset.AssetType.Image);

            ImGui.separator();
            EditorImGui.header("Metallic");
            this.metallicMap = (Texture) EditorImGui.field_Asset("Metallic Map", this.metallicMap, Asset.AssetType.Image);
            this.metallicIntensity = EditorImGui.field_Float("Metallicness", this.metallicIntensity, 0.02f, 0.0f, 1.0f);

            ImGui.separator();
            EditorImGui.header("Specular");
            this.specularMap = (Texture) EditorImGui.field_Asset("Specular Map", this.specularMap, Asset.AssetType.Image);
            this.specularIntensity = EditorImGui.field_Float("Specular", this.specularIntensity, 0.02f, 0.0f, 1.0f);
            this.shineDumper = EditorImGui.field_Float("Shine Dumper", this.shineDumper, 0.02f, 0);
            this.reflectivity = EditorImGui.field_Float("Reflectivity", this.reflectivity, 0.02f, 0);

            ImGui.separator();
            EditorImGui.header("Emission");
            this.emissionMap = (Texture) EditorImGui.field_Asset("Emission Map", this.emissionMap, Asset.AssetType.Image);
            this.emissionIntensity = EditorImGui.field_Float("Emission", this.emissionIntensity, 0.02f, 0.0f);
            this.useAlbedoEmission = EditorImGui.field_Boolean("Use Albedo Emission", this.useAlbedoEmission);

            ImGui.separator();
            EditorImGui.header("Other");
            this.alphaClip = EditorImGui.field_Float("Alpha Clip", this.alphaClip, 0.02f, 0.0f, 1.0f);

            this.tiling = EditorImGui.field_Vector2f("Tiling", tiling, new Vector2f(1.0f));

            this.renderCullSide = (RenderCullSide) EditorImGui.field_Enum("Render Cull Side", this.renderCullSide);

            this.useFakeLighting = EditorImGui.field_Boolean("Use Fake Lighting", this.useFakeLighting);
        }
    }
}
