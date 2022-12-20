package engine.renderEngine.postProcessing;

import engine.renderEngine.Window;
import engine.renderEngine.Loader;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.postProcessing.postEffects.bloom.BloomLayer;
import engine.renderEngine.postProcessing.postEffects.brightnessContrastSaturation.BrightnessContrastSaturationShader;
import engine.renderEngine.postProcessing.postEffects.power.PowerShader;
import engine.renderEngine.postProcessing.postEffects.gaussianBlur.GaussianBlurLayer;
import engine.renderEngine.postProcessing.postEffects.startPostProcess.StartPostProcessShader;
import engine.renderEngine.postProcessing.postEffects.tonemapping.TonemappingShader;
import engine.renderEngine.postProcessing.postEffects.vignette.VignetteShader;
import engine.renderEngine.postProcessing.postEffects.whiteBalance.WhiteBalanceShader;
import engine.toolbox.customVariables.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;

	private static Color ambientLightColor = new Color(255, 255, 255);
	private static float ambientLightIntensity = 0.05f;

	private static boolean useFog = false;
	private static float fogDensity = 0.001f;
	private static Color fogColor = new Color(128, 128, 128);
	private static float fogSmoothness = 1.5f;

	private static List<PostProcessLayer> allPostProcessLayers = new ArrayList<>(){{
		add(new PostProcessLayer(new PowerShader((int) Window.getWidth(), (int) Window.getHeight())));
		add(new GaussianBlurLayer((int) Window.getWidth(), (int) Window.getHeight()));
		add(new PostProcessLayer(new TonemappingShader((int) Window.getWidth(), (int) Window.getHeight())));
		add(new PostProcessLayer(new WhiteBalanceShader((int) Window.getWidth(), (int) Window.getHeight())));
		add(new PostProcessLayer(new VignetteShader((int) Window.getWidth(), (int) Window.getHeight())));
		add(new PostProcessLayer(new BrightnessContrastSaturationShader((int) Window.getWidth(), (int) Window.getHeight())));
		add(new BloomLayer((int) Window.getWidth(), (int) Window.getHeight()));
	}};

	public static boolean usePostProcessing;

	private static PostProcessLayer startPostProcess;
	private static List<PostProcessLayer> postProcessLayers = new ArrayList<>();

	public static void init(){
		quad = Loader.get().loadToVAO(POSITIONS, 2);
		startPostProcess = new PostProcessLayer(new StartPostProcessShader((int) Window.getWidth(), (int) Window.getHeight()));
		startPostProcess.loadVariables();
	}
	
	public static void doPostProcessing(int colourTexture) {
		start();

		startPostProcess.render(colourTexture);
		int lastFrameImage = startPostProcess.getOutputTexture();

		if (!usePostProcessing || postProcessLayers.size() == 0) {
			end();
			return;
		}

		for (PostProcessLayer layer : postProcessLayers) {
			if (!layer.isActive())
				continue;

			layer.loadVariables();
			layer.render(lastFrameImage);
			lastFrameImage = layer.getOutputTexture();
		}
		end();
	}

	public static int getFinalImage() {
		if (postProcessLayers.size() == 1 && !postProcessLayers.get(0).isActive())
			return startPostProcess.getOutputTexture();

		if (usePostProcessing && postProcessLayers.size() != 0 && isAnyLayerActive()) {
			int outImage = postProcessLayers.get(0).getOutputTexture();

			for (PostProcessLayer layer : postProcessLayers) {
				if (layer.isActive())
					outImage = layer.getOutputTexture();
			}
			return outImage;
		}

		return startPostProcess.getOutputTexture();
	}

	public static void cleanUp() {
		startPostProcess.cleanUp();
		if (usePostProcessing && postProcessLayers.size() != 0)
			for (PostProcessLayer layer : postProcessLayers)
				layer.cleanUp();
	}
	
	private static void start(){
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glDisable(GL_DEPTH_TEST);
	}
	
	private static void end(){
		glEnable(GL_DEPTH_TEST);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}

	public static List<PostProcessLayer> getLayers() { return postProcessLayers; }

	public static void addLayer(PostProcessLayer layer) { postProcessLayers.add(layer); }

	public static void removeLayer(PostProcessLayer layer) { postProcessLayers.remove(layer); }

	public static List<PostProcessLayer> getAllPostProcessLayers() { return allPostProcessLayers; }

	public static boolean isAnyLayerActive() {
		for (PostProcessLayer layer : postProcessLayers)
			if (layer.isActive())
				return true;

		return false;
	}

	public static void swapTwoLayers(int firstIndex, int secondIndex) { Collections.swap(postProcessLayers, firstIndex, secondIndex); }

	public static Color getAmbientLightColor() { return ambientLightColor; }

	public static void setAmbientLightColor(Color color) { ambientLightColor = color; }

	public static float getAmbientLightIntensity() { return ambientLightIntensity; }

	public static void setAmbientLightIntensity(float intensity) { ambientLightIntensity = intensity; }

	public static boolean isUseFog() { return useFog; }

	public static void setUseFog(boolean use) { useFog = use; }

	public static float getFogDensity() { return fogDensity; }

	public static void setFogDensity(float density) { fogDensity = density; }

	public static Color getFogColor() { return PostProcessing.fogColor; }

	public static void setFogColor(Color color) { fogColor = color; }

	public static float getFogSmoothness() { return fogSmoothness; }

	public static void setFogSmoothness(float smoothness) { fogSmoothness = smoothness; }
}
