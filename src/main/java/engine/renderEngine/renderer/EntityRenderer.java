package engine.renderEngine.renderer;

import engine.entities.GameObject;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.skybox.CubeMap;
import engine.renderEngine.textures.Material;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class EntityRenderer {

    private StaticShader shader;
    private CubeMap reflectionCubeMap;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix, CubeMap reflectionCubeMap) {
        this.reflectionCubeMap = reflectionCubeMap;
        this.shader = shader;
        this.shader.start();
        this.shader.loadUniformMatrix("projectionMatrix", projectionMatrix);
        this.shader.stop();
    }

    public void render(Map<TexturedModel, List<GameObject>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<GameObject> batch = entities.get(model);
            for (GameObject gameObject : batch) {
                prepareInstance(gameObject);
                glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                unbindTexturedModel();
            }
        }
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        Material texture = model.getTexture();
        shader.loadUniformFloat("numberOfRows", texture.getNumberOfRows());
        shader.loadUniformFloat("numberOfColumns", texture.getNumberOfColumns());

        shader.loadUniformVector2("tiling", texture.getTiling());

        shader.loadUniformColor("color", texture.getColor());

        shader.loadUniformFloat("shineDamper", texture.getShineDumper());
        shader.loadUniformFloat("reflectivity", texture.getReflectivity());

        shader.loadUniformFloat("alphaClip", texture.getAlphaClip());
        shader.loadUniformBoolean("useFakeLighting", texture.isUseFakeLighting());

        shader.loadUniformInt("enviromentMap", 1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, reflectionCubeMap.getTexture());

        if (texture.hasSpecular()) {
            shader.loadUniformInt("specularMap", 2);
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, texture.getSpecularMap());
            shader.loadUniformFloat("specularIntensity", texture.getSpecularIntensity());
        } else {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, 0);
            shader.loadUniformFloat("specularIntensity", -1);
        }

        if (texture.hasEmission()) {
            shader.loadUniformInt("emissionMap", 3);
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, texture.getEmissionMap());
            shader.loadUniformFloat("emissionIntensity", texture.getEmissionIntensity());
            shader.loadUniformBoolean("useAlbedoEmission", texture.isUseAlbedoEmission());
        } else {
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, 0);
            shader.loadUniformFloat("emissionIntensity", 0);
        }

        shader.loadUniformInt("textureSampler", 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());
    }

    private void unbindTexturedModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    private void prepareInstance(GameObject gameObject) {
        switch (gameObject.getComponent(MeshRenderer.class).getModel().getTexture().getCullSide()) {
            case Both -> glDisable(GL_CULL_FACE);

            case Front -> {
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
            }

            case Back -> {
                glEnable(GL_CULL_FACE);
                glCullFace(GL_FRONT);
            }

            case None -> { return; }
        }

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale);
        shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
        shader.loadUniformVector2("textureOffset", gameObject.getComponent(MeshRenderer.class).getTextureOffset());
    }
}
