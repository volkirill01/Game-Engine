package engine.renderEngine.postProcessing;

import static org.lwjgl.opengl.GL30.*;

public class ImageRenderer {

	private Fbo fbo;

	public ImageRenderer(int width, int height) { this.fbo = new Fbo(width, height, Fbo.NONE); }

	public void renderQuad() {
		if (fbo != null)
			fbo.bindFrameBuffer();

		glClear(GL_COLOR_BUFFER_BIT);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

		if (fbo != null)
			fbo.unbindFrameBuffer();
	}

	public int getOutputTexture() {
		if (fbo != null)
			return fbo.getColourTexture();
		return 0;
	}

	public void cleanUp() { if (fbo != null) fbo.cleanUp(); }
}
