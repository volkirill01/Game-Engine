package engine.components;

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
    private float range = 1.0f;

    public Light(Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
        this.lightType = LightType.Directional;
    }

    public int getLightTypeInt() {
        return switch (this.lightType) {
            case Directional -> 0;
            case Point -> 1;
        };
    }

    public LightType getLightType() { return this.lightType; }

    public void setLightType(LightType lightType) { this.lightType = lightType; }

    public Color getColor() { return this.color; }

    public void setColor(Color color) { this.color = color; }

    public float getIntensity() { return this.intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    public float getRange() { return this.range; }

    public void setRange(float range) { this.range = range; }

    @Override
    public void imgui() {
        EditorImGui.filed_Color("Color", this.color);
        this.intensity = EditorImGui.field_Float("Intensity", this.intensity, 0.02f, 0.0f);
        this.lightType = (LightType) EditorImGui.field_Enum("Light Type", this.lightType);

//        if (this.lightType == LightType.Point)
//            this.range = EditorImGui.field_Float("Range", this.range, 0.02f);
    }

    @Override
    public void reset() {
        this.color = Color.White;
        this.intensity = 1.0f;
        this.range = 1.0f;
    }
}
