package engine.entities;

import engine.toolbox.customVariables.Color;
import org.joml.Vector3f;

public class Light {

    private Vector3f position;
    private Color color;
    private float intensity;
    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Vector3f position, Color color, float intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    public Light(Vector3f position, Color color, float intensity, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.attenuation = attenuation;
    }

    public Vector3f getPosition() { return this.position; }

    public void setPosition(Vector3f position) { this.position = position; }

    public Color getColor() { return this.color; }

    public void setColor(Color color) { this.color = color; }

    public float getIntensity() { return this.intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    public Vector3f getAttenuation() { return this.attenuation; }

    public void setAttenuation(Vector3f attenuation) { this.attenuation = attenuation; }
}
