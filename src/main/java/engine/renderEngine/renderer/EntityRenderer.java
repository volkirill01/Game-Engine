package engine.renderEngine.renderer;

import engine.entities.GameObject;
import engine.renderEngine.Loader;
import engine.renderEngine.PickingShader;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.skybox.CubeMap;
import engine.renderEngine.textures.Material;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
            for (int i = 0; i < model.getMesh().getModels().size(); i++) {
                prepareTexturedModel(model.getMesh().getModels().get(i), model.getMaterials().get(i));
                List<GameObject> batch = entities.get(model);
                for (GameObject gameObject : batch) {
                    if (!gameObject.getComponent(MeshRenderer.class).isActive())
                        continue;

                    prepareInstance(gameObject);
                    glDrawElements(GL_TRIANGLES, model.getMesh().getModels().get(i).getVertexCount(), GL_UNSIGNED_INT, 0);

                    unbindTexturedModel();
                }
            }
        }
    }

    private void prepareTexturedModel(RawModel model, Material material) {
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        shader.loadUniformFloat("numberOfRows", material.getTexture().getNumberOfRows());
        shader.loadUniformFloat("numberOfColumns", material.getTexture().getNumberOfColumns());

        shader.loadUniformVector2("tiling", material.getTiling());

        shader.loadUniformColor("color", material.getColor());

        shader.loadUniformFloat("shineDamper", material.getShineDumper());

        shader.loadUniformFloat("alphaClip", material.getAlphaClip());
        shader.loadUniformBoolean("useFakeLighting", material.isUseFakeLighting());

//        // PBR
//        if (material.hasMetallic()) {
//            shader.loadUniformInt("metallicMap", 1);
//            glActiveTexture(GL_TEXTURE1);
//            glBindTexture(GL_TEXTURE_2D, material.getMetallicMap());
//            shader.loadUniformFloat("metallicIntensity", material.getMetallicIntensity());
//        } else {
//            glActiveTexture(GL_TEXTURE1);
//            glBindTexture(GL_TEXTURE_2D, 0);
//            shader.loadUniformFloat("metallicIntensity", 0);
//        }
//
//        if (material.hasSpecular()) {
//            shader.loadUniformInt("roughnessMap", 2);
//            glActiveTexture(GL_TEXTURE2);
//            glBindTexture(GL_TEXTURE_2D, material.getSpecularMap().getTextureID());
//            shader.loadUniformFloat("roughnessIntensity", material.getSpecularIntensity());
//        } else {
//            glActiveTexture(GL_TEXTURE2);
//            glBindTexture(GL_TEXTURE_2D, 0);
//            shader.loadUniformFloat("roughnessIntensity", 0);
//        }
//
//        if (material.hasEmission()) {
//            shader.loadUniformInt("emissionMap", 3);
//            glActiveTexture(GL_TEXTURE3);
//            glBindTexture(GL_TEXTURE_2D, material.getEmissionMap().getTextureID());
//            shader.loadUniformFloat("emissionIntensity", material.getEmissionIntensity());
//            shader.loadUniformBoolean("useAlbedoEmission", material.isUseAlbedoEmission());
//        } else {
//            glActiveTexture(GL_TEXTURE3);
//            glBindTexture(GL_TEXTURE_2D, 0);
//            shader.loadUniformFloat("emissionIntensity", 0);
//        }
//
//        shader.loadUniformInt("albedoMap", 0);
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, material.getTexture().getTextureID());

        shader.loadUniformInt("enviromentMap", 1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, reflectionCubeMap.getTexture());

        if (material.hasSpecular()) {
            shader.loadUniformInt("specularMap", 2);
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, material.getSpecularMap().getTextureID());
            Loader.get().updateTexture(material.getSpecularMap());
            shader.loadUniformFloat("specularIntensity", material.getSpecularIntensity());
        } else {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, 0);
            shader.loadUniformFloat("specularIntensity", 0.0f);
        }

        if (material.hasEmission()) {
            shader.loadUniformInt("emissionMap", 3);
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, material.getEmissionMap().getTextureID());
            Loader.get().updateTexture(material.getEmissionMap());
            shader.loadUniformFloat("emissionIntensity", material.getEmissionIntensity());
            shader.loadUniformBoolean("useAlbedoEmission", material.isUseAlbedoEmission());
        } else {
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, 0);
            shader.loadUniformFloat("emissionIntensity", 0.0f);
        }

        shader.loadUniformInt("textureSampler", 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, material.getTexture().getTextureID());
        Loader.get().updateTexture(material.getTexture());

        switch (material.getCullSide()) {
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
    }

    private void unbindTexturedModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    private void prepareInstance(GameObject gameObject) {
        Vector3f sizeMultiplayed = new Vector3f(gameObject.transform.scale);
        sizeMultiplayed = sizeMultiplayed.mul(gameObject.getComponent(MeshRenderer.class).getModel().getMesh().getSizeMultiplayer());

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                gameObject.transform.position, gameObject.transform.rotation, sizeMultiplayed);
        shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
        shader.loadUniformVector2("textureOffset", gameObject.getComponent(MeshRenderer.class).getTextureOffset());
    }
}
