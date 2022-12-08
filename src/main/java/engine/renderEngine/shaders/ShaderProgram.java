package engine.renderEngine.shaders;

import engine.toolbox.customVariables.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import theleo.jstruct.Struct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public static final String simpleVertexShader = "engineFiles/shaders/util/simpleVertex.glsl";
    public static final String simpleFragmentShader = "engineFiles/shaders/util/simpleFragment.glsl";

    @Struct
    public static class ShaderVariable {
        public int location;
        public String variableName;

        public ShaderVariable(String variableName, int location) {
            this.location = location;
            this.variableName = variableName;
        }
    }

    private List<ShaderVariable> variables = new ArrayList<>();

    public ShaderProgram() { init(simpleVertexShader, simpleFragmentShader); }

    public ShaderProgram(String vertexFile, String fragmentFile) { init(vertexFile, fragmentFile); }

    public void init(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
        getAllUniforms();
    }

    protected void getAllUniforms() { }

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    public void start() { glUseProgram(programID); }

    public void stop() { glUseProgram(0); }

    public void cleanUp() {
        stop();
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) { glBindAttribLocation(programID, attribute, variableName); }

    protected void loadFloat(int location, float value) { glUniform1f(location, value); }

    protected void loadInt(int location, int value) { glUniform1i(location, value); }

    protected void loadVector(int location, Vector2f vector) { glUniform2f(location, vector.x, vector.y); }

    protected void loadVector(int location, Vector3f vector) { glUniform3f(location, vector.x, vector.y, vector.z); }

    protected void loadVector(int location, Vector4f vector) { glUniform4f(location, vector.x, vector.y, vector.z, vector.w); }

    protected void loadColor(int location, Color color) {
        Vector3f percentColor = color.toPercentColor().toVector3();
        glUniform3f(location, percentColor.x, percentColor.y, percentColor.z);
    }

    protected void loadColorWithAlpha(int location, Color color) {
        Vector4f percentColor = color.toPercentColor().toVector4();
        glUniform4f(location, percentColor.x, percentColor.y, percentColor.z, percentColor.w);
    }

    protected void loadBoolean(int location, boolean value) { glUniform1f(location, value ? 1 : 0); }

    protected void loadMatrix(int location, Matrix4f matrix) {
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matBuffer);
        glUniformMatrix4fv(location, false, matBuffer);
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(shaderId, 500));
            System.out.println("Could not compile shader.");
            System.exit(-1);
        }
        return shaderId;
    }

    public void loadUniformFloat(String variableName, float value) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadFloat(variable.location, value);
//        System.out.println("(Float) " + variableName + " - location(" + variable.location + ") value: '" + value + "'");
    }

    public void loadUniformInt(String variableName, int value) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadInt(variable.location, value);
//        System.out.println("(Float) " + variableName + " - location(" + variable.location + ") value: '" + value + "'");
    }

    public void loadUniformVector2(String variableName, Vector2f vector) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadVector(variable.location, vector);
//        System.out.println("(Vector2) " + variableName + " - location(" + variable.location + ") value: '" + vector + "'");
    }

    public void loadUniformVector3(String variableName, Vector3f vector) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadVector(variable.location, vector);
//        System.out.println("(Vector3) " + variableName + " - location(" + variable.location + ") value: '" + vector + "'");
    }

    public void loadUniformVector4(String variableName, Vector4f vector) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadVector(variable.location, vector);
//        System.out.println("(Vector3) " + variableName + " - location(" + variable.location + ") value: '" + vector + "'");
    }

    public void loadUniformColor(String variableName, Color color) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadColor(variable.location, color);
//        System.out.println("(Color) " + variableName + " - location(" + variable.location + ") value: '" + color + "'");
    }

    public void loadUniformColorWithAlpha(String variableName, Color color) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadColorWithAlpha(variable.location, color);
//        System.out.println("(Color) " + variableName + " - location(" + variable.location + ") value: '" + color + "'");
    }

    public void loadUniformBoolean(String variableName, boolean value) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadBoolean(variable.location, value);
//        System.out.println("(Boolean) " + variableName + " - location(" + variable.location + ") value: '" + value + "'");
    }

    public void loadUniformMatrix(String variableName, Matrix4f matrix) {
        addVariableIfNotExists(variableName);

        StaticShader.ShaderVariable variable = getVariable(variableName);
        loadMatrix(variable.location, matrix);
//        System.out.println("(Matrix4) " + variableName + " - location(" + variable.location + ") value: '" + matrix + "'");
    }

    private void addVariableIfNotExists(String variableName) {
        if (getVariable(variableName, false).location == -1) {
            variables.add(new StaticShader.ShaderVariable(variableName, getUniformLocation(variableName)));
//            System.out.println("add " + variableName);
        }
    }

    private StaticShader.ShaderVariable getVariable(String variableName) { return getVariable(variableName, true); }

    private StaticShader.ShaderVariable getVariable(String variableName, boolean printError) {
        for (StaticShader.ShaderVariable variable : variables)
            if (variable.variableName.equals(variableName))
                return variable;

        if (printError)
            System.err.println("No shader variable found with this name: '" + variableName + "'");
        return new StaticShader.ShaderVariable(null, -1); // null
    }
}
