package engine.renderEngine.shaders;

import engine.entities.Light;
import engine.renderEngine.renderer.RenderCullSide;
import engine.toolbox.customVariables.Color;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class StaticShader extends ShaderProgram {

    private static final int MAX_LIGHTS_COUNT = 4;
    public ShaderVariable[] lightPositionVariables;
    public ShaderVariable[] lightRotationVariables;
    public ShaderVariable[] lightColorVariables;
    public ShaderVariable[] lightIntensityVariables;
    public ShaderVariable[] lightAttenuationVariables;
    public ShaderVariable[] lightRangeVariables;

    public StaticShader(String VERTEX_FILE, String FRAGMENT_FILE) {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    public void getAllUniforms() {
        lightPositionVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        lightRotationVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        lightColorVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        lightIntensityVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        lightAttenuationVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        lightRangeVariables = new ShaderVariable[MAX_LIGHTS_COUNT];
        for (int i = 0; i < MAX_LIGHTS_COUNT; i++) {
            lightPositionVariables[i] = new ShaderVariable(
                    "lightPosition[" + i + "]",
                    super.getUniformLocation("lightPosition[" + i + "]"));

            lightRotationVariables[i] = new ShaderVariable(
                    "lightRotation[" + i + "]",
                    super.getUniformLocation("lightRotation[" + i + "]"));

            lightColorVariables[i] = new ShaderVariable(
                    "lightColor[" + i + "]",
                    super.getUniformLocation("lightColor[" + i + "]"));

            lightIntensityVariables[i] = new ShaderVariable(
                    "lightIntensity[" + i + "]",
                    super.getUniformLocation("lightIntensity[" + i + "]"));

            lightAttenuationVariables[i] = new ShaderVariable(
                    "attenuation[" + i + "]",
                    super.getUniformLocation("attenuation[" + i + "]"));

            lightRangeVariables[i] = new ShaderVariable(
                    "lightRange[" + i + "]",
                    super.getUniformLocation("lightRange[" + i + "]"));
        }
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS_COUNT; i++) {
            super.loadVector(lightPositionVariables[i].location, new Vector3f(0.0f));
            super.loadVector(lightRotationVariables[i].location, new Vector3f(0.0f));
            super.loadColor(lightColorVariables[i].location, Color.Black);
            super.loadFloat(lightIntensityVariables[i].location, 0.0f);
            super.loadVector(lightAttenuationVariables[i].location, new Vector3f(1.0f, 0.0f, 0.0f));

            try {
                if (i < lights.size() && lights.get(i).isActive()) {
                    super.loadVector(lightPositionVariables[i].location, lights.get(i).gameObject.transform.position);
                    super.loadVector(lightRotationVariables[i].location, new Vector3f(lights.get(i).gameObject.transform.rotation).add(new Vector3f(0.001f)));
                    super.loadColor(lightColorVariables[i].location, lights.get(i).getColor());
                    super.loadFloat(lightIntensityVariables[i].location, lights.get(i).getIntensity());
                    super.loadVector(lightAttenuationVariables[i].location, lights.get(i).getAttenuation());
//                    super.loadUniformFloat(lightRangeVariables[i].location, lights.get(i).getRange());
                } else {
                    super.loadVector(lightPositionVariables[i].location, new Vector3f(0.0f));
                    super.loadVector(lightRotationVariables[i].location, new Vector3f(0.0f));
                    super.loadColor(lightColorVariables[i].location, Color.Black);
                    super.loadFloat(lightIntensityVariables[i].location, 0.0f);
                    super.loadVector(lightAttenuationVariables[i].location, new Vector3f(1.0f, 0.0f, 0.0f));
//                    super.loadUniformFloat(lightRangeVariables[i].location, 0.0f);
                }
            } catch (NullPointerException e) {
//                throw new RuntimeException(e);
            }
        }
    }
}
