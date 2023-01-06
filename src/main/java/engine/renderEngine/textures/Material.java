package engine.renderEngine.textures;

import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.renderer.RenderCullSide;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public class Material {

    private String filepath;
    private transient Map<String, Object> data;

    private transient Texture texture;
    private transient Vector2f tiling = new Vector2f(1.0f, 1.0f);
    private transient Color color = Color.White;

    private transient Texture metallicMap;
    private transient float metallicIntensity = 0.0f;

    private transient Texture specularMap;
    private transient float specularIntensity = 0.0f;
    private transient float shineDumper = 10.0f;

    private transient Texture emissionMap;
    private transient float emissionIntensity = 0.0f;
    private transient boolean useAlbedoEmission = false;

    private transient float alphaClip = 0.0f;
    private transient RenderCullSide renderCullSide = RenderCullSide.Front;
    private transient boolean useFakeLighting = false;

    public String getFilepath() { return this.filepath; }

    public Map<String, Object> getData() { return this.data; }

    public Material(String filepath, Map<String, Object> data) {
        this.filepath = filepath;
        this.data = data;
    }

    public void update() {
        this.texture.update();
        this.data.replace("albedo", this.texture.getFilepath());
        this.data.replace("tiling", this.tiling.x + ", " + this.tiling.y);
        this.data.replace("color", this.color.r + ", " + this.color.g + ", " +this.color.b + ", " + this.color.a);

        this.metallicMap.update();
        this.data.replace("metallicMap", this.metallicMap.getFilepath());
        this.data.replace("metallicIntensity", this.metallicIntensity);

        this.specularMap.update();
        this.data.replace("specularMap", this.specularMap.getFilepath());
        this.data.replace("specularIntensity", this.specularIntensity);
        this.data.replace("shineDumper", this.shineDumper);

        this.emissionMap.update();
        this.data.replace("emissionMap", this.emissionMap.getFilepath());
        this.data.replace("emissionIntensity", this.emissionIntensity);
        this.data.replace("useAlbedoEmission", this.useAlbedoEmission);

        this.data.replace("alphaClip", this.alphaClip);
        this.data.replace("renderCullSide", this.renderCullSide);
        this.data.replace("useFakeLighting", this.useFakeLighting);
        Loader.get().saveMaterial(this.filepath, this.data);
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

    public RenderCullSide getCullSide() { return this.renderCullSide; }

    public void setCullSided(RenderCullSide cullSide) { this.renderCullSide = cullSide; }

    public boolean isUseFakeLighting() { return this.useFakeLighting; }

    public void setUseFakeLighting(boolean useFakeLighting) { this.useFakeLighting = useFakeLighting; }

    public void imgui(float xOffset) {
        if (EditorImGui.collapsingHeader("Albedo", xOffset, true)) {
            EditorImGui.field_Color("Color", color);
            this.texture = EditorImGui.field_Texture("Albedo", this.texture, this.tiling, new Vector2f());

            ImGui.separator();
        }

        if (EditorImGui.collapsingHeader("Metallic", xOffset, true)) {
            List<Object> tmpMetallic = EditorImGui.field_Texture("Metallic Map", this.metallicMap, new Vector2f(), new Vector2f(), this.metallicIntensity);
            this.metallicMap = (Texture) tmpMetallic.get(0);
            this.metallicIntensity = (float) tmpMetallic.get(1);

            ImGui.separator();
        }

        if (EditorImGui.collapsingHeader("Specular", xOffset, true)) {
            List<Object> tmpSpecular = EditorImGui.field_Texture("Specular Map", this.specularMap, new Vector2f(), new Vector2f(), this.specularIntensity);
            this.specularMap = (Texture) tmpSpecular.get(0);
            this.specularIntensity = (float) tmpSpecular.get(1);
            this.shineDumper = EditorImGui.field_Float("Shine Dumper", this.shineDumper, 0.02f, 0);

            ImGui.separator();
        }

        if (EditorImGui.collapsingHeader("Emission", xOffset, true)) {
            List<Object> tmpEmission = EditorImGui.field_Texture("Emission Map", this.emissionMap, new Vector2f(), new Vector2f(), this.emissionIntensity);
            this.emissionMap = (Texture) tmpEmission.get(0);
            this.emissionIntensity = (float) tmpEmission.get(1);
            this.useAlbedoEmission = EditorImGui.field_Boolean("Use Albedo Emission", this.useAlbedoEmission);

            ImGui.separator();
        }

        if (EditorImGui.collapsingHeader("Other", xOffset, true)) {
            this.alphaClip = EditorImGui.field_Float("Alpha Clip", this.alphaClip, 0.02f, 0.0f, 1.0f);
            this.renderCullSide = (RenderCullSide) EditorImGui.field_Enum("Render Cull Side", this.renderCullSide);
            this.useFakeLighting = EditorImGui.field_Boolean("Use Fake Lighting", this.useFakeLighting);
        }
    }
}
