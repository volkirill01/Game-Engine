package engine.renderEngine.font.fontMeshCreator;

import engine.renderEngine.font.fontRendering.TextMaster;
import engine.toolbox.customVariables.Color;
import org.joml.Vector2f;

public class GUIText {

	private String textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;

	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;

	private Color fontColor = Color.Black;
	private float fontWidth = 0.5f;
	private float fontSoftness = 0.1f;

	private Color borderColor = Color.Black;
	private float borderWidth = 0.0f;
	private float borderSoftness = 0.1f;

	private Color dropShadowColor = Color.Black;
	private float dropShadowWidth = 0.0f;
	private float dropShadowSoftness = 0.2f;
	private Vector2f dropShadowOffset = new Vector2f(-0.001f, -0.001f);

	private boolean centerText;

	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
		this.textString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		TextMaster.loadText(this);
	}

	public void remove() { TextMaster.removeText(this); }

	public FontType getFont() { return font; }

	public Color getFontColor() { return this.fontColor; }

	public int getNumberOfLines() { return numberOfLines; }

	public void setPosition(Vector2f position) { this.position = position; }

	public Vector2f getPosition() { return position; }

	public int getMesh() { return textMeshVao; }

	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	public int getVertexCount() { return this.vertexCount; }

	protected float getFontSize() { return fontSize; }

	protected void setNumberOfLines(int number) { this.numberOfLines = number; }

	protected boolean isCentered() { return centerText; }

	protected float getMaxLineSize() { return lineMaxSize; }

	public String getTextString() { return textString; }

	public float getFontWidth() { return this.fontWidth; }

	public float getFontSoftness() { return this.fontSoftness; }

	public Color getBorderColor() { return this.borderColor; }

	public float getBorderWidth() { return this.borderWidth; }

	public float getBorderSoftness() { return this.borderSoftness; }

	public Color getDropShadowColor() { return this.dropShadowColor; }

	public float getDropShadowWidth() { return this.dropShadowWidth; }

	public float getDropShadowSoftness() { return this.dropShadowSoftness; }

	public Vector2f getDropShadowOffset() { return this.dropShadowOffset; }

	public void setFontParams(Color fontColor, float fontSize, float fontWidth, float fontSoftness) {
		this.fontColor = fontColor;
		this.fontSize = fontSize;
		this.fontWidth = fontWidth;
		this.fontSoftness = fontSoftness;
	}

	public void setBorder(Color borderColor, float borderWidth, float borderSoftness) {
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		this.borderSoftness = borderSoftness;
	}

	public void setDropShadow(Color dropShadowColor, Vector2f dropShadowOffset, float dropShadowWidth, float dropShadowSoftness) {
		this.dropShadowColor = dropShadowColor;
		this.dropShadowOffset = dropShadowOffset;
		this.dropShadowWidth = dropShadowWidth;
		this.dropShadowSoftness = dropShadowSoftness;
	}
}
