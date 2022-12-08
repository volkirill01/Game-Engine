package engine.renderEngine.font.fontRendering;

import engine.renderEngine.font.fontMeshCreator.FontType;
import engine.renderEngine.font.fontMeshCreator.GUIText;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() { shader = new FontShader(); }

	public void render(Map<FontType, List<GUIText>> texts) {
		prepare();
		for (FontType font : texts.keySet()) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
			for (GUIText text : texts.get(font)) {
				renderText(text);
			}
		}
		endRendering();
	}

	public void cleanUp() { shader.cleanUp(); }
	
	private void prepare() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text) {
		glBindVertexArray(text.getMesh());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		shader.loadUniformVector2("translation", text.getPosition());

		shader.loadUniformColorWithAlpha("color", text.getFontColor());
		shader.loadUniformFloat("width", text.getFontWidth());
		shader.loadUniformFloat("edge", text.getFontSoftness());

		shader.loadUniformColorWithAlpha("borderColor", text.getBorderColor());
		shader.loadUniformFloat("borderWidth", text.getBorderWidth());
		shader.loadUniformFloat("borderEdge", text.getBorderSoftness());

		shader.loadUniformColorWithAlpha("dropShadowColor", text.getDropShadowColor());
		shader.loadUniformFloat("dropShadowWidth", text.getDropShadowWidth());
		shader.loadUniformFloat("dropShadowEdge", text.getDropShadowSoftness());
		shader.loadUniformVector2("dropShadowOffset", text.getDropShadowOffset());

		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}
	
	private void endRendering() {
		shader.stop();
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	}

}
