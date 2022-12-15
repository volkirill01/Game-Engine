package engine.entities;

import engine.components.Component;
import engine.imGui.EditorImGui;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector3f;

public class Light extends Component {

    private enum LightType {
        Directional,
        Point
    }

    private LightType lightType;

    private Color color;
    private float intensity;
    private final Vector3f defaultAttenuation = new Vector3f(1, 0, 0);
    private Vector3f attenuation = new Vector3f(defaultAttenuation);

    public Light(Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
        this.lightType = LightType.Directional;
    }

    public Light(Color color, float intensity, Vector3f attenuation) {
        this.color = color;
        this.intensity = intensity;
        this.attenuation = attenuation;
        this.lightType = LightType.Point;
    }

    public LightType getLightType() { return this.lightType; }

    public void setLightType(LightType lightType) { this.lightType = lightType; }

    public Color getColor() { return this.color; }

    public void setColor(Color color) { this.color = color; }

    public float getIntensity() { return this.intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    public Vector3f getAttenuation() { return this.attenuation; }

    public void setAttenuation(Vector3f attenuation) { this.attenuation = attenuation; }

    @Override
    public void update() {
        if (this.lightType == LightType.Directional)
            this.attenuation = defaultAttenuation;
    }

    @Override
    public void editorUpdate() {
        if (this.lightType == LightType.Directional)
            this.attenuation = defaultAttenuation;
    }

    @Override
    public void imgui() {
        this.lightType = (LightType) EditorImGui.field_Enum("Light Type", this.lightType);
        ImGui.spacing();
        this.intensity = EditorImGui.field_Float("Intensity", this.intensity, 0.02f, 0.0f);

        if (this.lightType != LightType.Directional)
            this.attenuation = EditorImGui.field_Vector3f("Attenuation", this.attenuation, new Vector3f(1.0f, 0.0f, 0.0f));
    }

    @Override
    public void reset() {
        this.color = Color.White;
        this.intensity = 1.0f;
        this.attenuation = new Vector3f(1, 0, 0);
    }
}
