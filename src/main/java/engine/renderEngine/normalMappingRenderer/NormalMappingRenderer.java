package engine.renderEngine.normalMappingRenderer;

import engine.entities.Camera;
import engine.entities.GameObject;
import engine.entities.Light;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class NormalMappingRenderer {

	private NormalMappingShader shader;

	public NormalMappingRenderer(Matrix4f projectionMatrix) {
		this.shader = new NormalMappingShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<GameObject>> entities, List<Light> lights, Camera camera) {
		shader.start();
		prepare(lights, camera);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<GameObject> batch = entities.get(model);
			for (GameObject gameObject : batch) {
				prepareInstance(gameObject);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Material texture = model.getMaterial();
		shader.loadNumberOfRows(texture.getTexture().getNumberOfRows());
		shader.loadNumberOfColumns(texture.getTexture().getNumberOfColumns());
//		if (texture.isHasTransparency()) {
//			MasterRenderer.disableCulling();
//		}
//		shader.loadShineVariables(texture.getShineDumper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getTexture().getTextureID());
	}

	private void unbindTexturedModel() {
//		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(GameObject gameObject) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale);
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(gameObject.getComponent(MeshRenderer.class).getTextureOffset().x, gameObject.getComponent(MeshRenderer.class).getTextureOffset().y);
	}

	private void prepare(List<Light> lights, Camera camera) {
		//need to be public variables in MasterRenderer
		shader.loadSkyColour(1, 1, 1);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}

}
