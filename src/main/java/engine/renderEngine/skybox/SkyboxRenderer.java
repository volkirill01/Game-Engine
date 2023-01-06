package engine.renderEngine.skybox;

import engine.entities.EditorCamera;
import engine.renderEngine.Loader;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.toolbox.Maths;
import engine.toolbox.Time;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class SkyboxRenderer {

    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static String[] TEXTURE_FILES = {
            "engineFiles/images/defaultSkybox/right.png",
            "engineFiles/images/defaultSkybox/left.png",
            "engineFiles/images/defaultSkybox/top.png",
            "engineFiles/images/defaultSkybox/bottom.png",
            "engineFiles/images/defaultSkybox/back.png",
            "engineFiles/images/defaultSkybox/front.png" };

    private RawModel cube;
    private int texture;
    private SkyboxShader shader;

    public SkyboxRenderer(Matrix4f projectionMatrix) {
        cube = Loader.get().loadToVAO(VERTICES, 3);
        texture = Loader.get().loadCubeMap(TEXTURE_FILES);
        shader = new SkyboxShader("engineFiles/shaders/skybox/skyboxVertexShader.glsl", "engineFiles/shaders/skybox/skyboxFragmentShader.glsl");
        shader.start();
        shader.loadUniformMatrix("projectionMatrix", projectionMatrix);
        shader.stop();
    }

    private float rotation = 0;
    private float SKYBOX_ROTATION_SPEED = 1.0f;
    public void render(EditorCamera editorCamera) {
        shader.start();

        Matrix4f viewMatrix = Maths.createViewMatrix(editorCamera);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        rotation += SKYBOX_ROTATION_SPEED * Time.deltaTime();
        viewMatrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0));
        shader.loadUniformMatrix("viewMatrix", viewMatrix);

        shader.loadUniformColor("backgroundColor", PostProcessing.getBackgroundColor());
        shader.loadUniformColor("fogColor", PostProcessing.getFogColor());
        shader.loadUniformFloat("fogDensity", PostProcessing.isUseFog() ? PostProcessing.getFogDensity() : 0);

        glBindVertexArray(cube.getVaoID());
        glEnableVertexAttribArray(0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glDrawArrays(GL_TRIANGLES, 0, cube.getVertexCount());

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.stop();
    }

    public int getTexture() { return this.texture; }
}
