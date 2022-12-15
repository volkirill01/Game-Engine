package engine.entities;

import engine.components.Component;
import engine.toolbox.customVariables.Color;
import org.joml.Vector3f;

public class Light extends Component {

    private Color color;
    private float intensity;
    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Light(Color color, float intensity, Vector3f attenuation) {
        this.color = color;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color color) { this.color = color; }

    public float getIntensity() { return this.intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    public Vector3f getAttenuation() { return this.attenuation; }

    public void setAttenuation(Vector3f attenuation) { this.attenuation = attenuation; }

    @Override
    public void reset() {
        this.color = Color.White;
        this.intensity = 1.0f;
        this.attenuation = new Vector3f(1, 0, 0);
    }
}
