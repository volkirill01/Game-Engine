package engine.renderEngine.renderer;

import engine.renderEngine.models.RawModel;
import engine.renderEngine.shaders.StaticShader;
import engine.renderEngine.shadows.ShadowBox;
import engine.renderEngine.shadows.ShadowMapMasterRenderer;
import engine.renderEngine.textures.TerrainTexturePack;
import engine.terrain.Terrain;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer {

    private StaticShader shader;

    public TerrainRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        this.shader.start();
        this.shader.loadUniformMatrix("projectionMatrix", projectionMatrix);
        this.shader.loadUniformInt("backgroundTexture", 0);
        this.shader.loadUniformInt("rTexture", 1);
        this.shader.loadUniformInt("gTexture", 2);
        this.shader.loadUniformInt("bTexture", 3);
        this.shader.loadUniformInt("blendMap", 4);
        this.shader.loadUniformInt("shadowMap", 5);
        this.shader.loadUniformFloat("shadowMapSize", ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
        this.shader.stop();
    }

    public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {
        shader.loadUniformMatrix("toShadowMapSpace", toShadowSpace);
        shader.loadUniformFloat("shadowDistance", ShadowBox.SHADOW_DISTANCE);
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            glDrawElements(GL_TRIANGLES,terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            unbindTerrain();
        }
    }

    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();
        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        shader.loadUniformVector2("tiling", terrain.getTiling());

        shader.loadUniformFloat("shineDamper", 1);
        shader.loadUniformFloat("reflectivity", 0);

        bindTextures(terrain);
    }

    private void bindTextures(Terrain terrain) {
        TerrainTexturePack texturePack = terrain.getTexturePack();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texturePack.getRTexture().getTextureID());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, texturePack.getGTexture().getTextureID());
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, texturePack.getBTexture().getTextureID());
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTerrain() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), 0.0f, terrain.getZ()),
                new Vector3f(), new Vector3f(1.0f));
        shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
    }
}
