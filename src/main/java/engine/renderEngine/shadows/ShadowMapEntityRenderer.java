package engine.renderEngine.shadows;

import engine.entities.GameObject;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class ShadowMapEntityRenderer {

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader
	 *            - the simple shader program being used for the shadow render
	 *            pass.
	 * @param projectionViewMatrix
	 *            - the orthographic projection matrix multiplied by the light's
	 *            "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entieis to the shadow map. Each model is first bound and then all
	 * of the entities using that model are rendered to the shadow map.
	 * 
	 * @param entities
	 *            - the entities to be rendered to the shadow map.
	 */
	protected void render(Map<TexturedModel, List<GameObject>> entities) {
		for (TexturedModel model : entities.keySet()) {
			for (RawModel rawModel : model.getMesh().getModels()) {
				bindModel(rawModel);
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, model.getMaterials().get(0).getTexture().getTextureID());
				glDisable(GL_CULL_FACE);
				for (GameObject gameObject : entities.get(model)) {
					prepareInstance(gameObject);
					glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
				}
			}
		}
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 * 
	 * @param rawModel
	 *            - the model to be bound.
	 */
	private void bindModel(RawModel rawModel) {
		glBindVertexArray(rawModel.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the
	 * usual way and then multiplied with the projection and view matrix (often
	 * in the past we've done this in the vertex shader) to create the
	 * mvp-matrix. This is then loaded to the vertex shader as a uniform.
	 * 
	 * @param gameObject
	 *            - the entity to be prepared for rendering.
	 */
	private void prepareInstance(GameObject gameObject) {
		Matrix4f modelMatrix = Maths.createTransformationMatrix(gameObject.transform.localPosition,
				gameObject.transform.localRotation, gameObject.transform.localScale);
		Matrix4f mvpMatrix = new Matrix4f(projectionViewMatrix.mul(modelMatrix));
		shader.loadUniformMatrix("mvpMatrix", mvpMatrix);
	}

}
