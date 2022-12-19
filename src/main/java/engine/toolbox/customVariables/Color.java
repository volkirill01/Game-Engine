package engine.toolbox.customVariables;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color {

    public float r, g, b, a;

    public Color(float r, float g, float b) { init(r, g, b, 255); }

    public Color(float r, float g, float b, float a) { init(r, g, b, a); }

    private void init(float red, float green, float blue, float alpha) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    public void set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color toPercentColor() { return new Color(this.r / 255.0f, this.g / 255.0f, this.b / 255.0f, this.a / 255.0f); }

    public Vector3f toVector3() { return new Vector3f(this.r, this.g, this.b); }

    public Vector4f toVector4() { return new Vector4f(this.r, this.g, this.b, this.a); }

    @Override
    public String toString() { return "(" + r + " " + g + " " + b + " " + a + ")"; }

    public static Color Red = new Color(255, 0, 0);

    public static Color Green = new Color(0, 255, 0);

    public static Color Blue = new Color(0, 0, 255);

    public static Color White = new Color(255, 255, 255);

    public static Color Black = new Color(0, 0, 0);

    public static Color Gray = new Color(128, 128, 128);

    public static Color Yellow = new Color(255, 255, 0);

}
