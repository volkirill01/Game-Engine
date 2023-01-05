package engine.renderEngine.normalMappingRenderer;

import engine.components.Light;
import engine.renderEngine.shaders.ShaderProgram;
import org.joml.*;

import java.util.List;

public class NormalMappingShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "engineFiles/normalMapVShader.glsl";
	private static final String FRAGMENT_FILE = "engineFiles/normalMapFShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPositionEyeSpace[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_numberOfColumns;
	private int location_offset;
	private int location_plane;
	private int location_modelTexture;

	public NormalMappingShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	public void getAllUniforms() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_numberOfColumns = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_modelTexture = super.getUniformLocation("modelTexture");
		
		location_lightPositionEyeSpace = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0;i<MAX_LIGHTS;i++){
			location_lightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_modelTexture, 0);
	}
	
	protected void loadClipPlane(Vector4f plane){
		super.loadVector(location_plane, plane);
	}
	
	protected void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	protected void loadNumberOfColumns(int numberOfColumns){
		super.loadFloat(location_numberOfColumns, numberOfColumns);
	}
	
	protected void loadOffset(float x, float y){
		super.loadVector(location_offset, new Vector2f(x,y));
	}
	
	protected void loadSkyColour(float r, float g, float b){
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	protected void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	protected void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	protected void loadLights(List<Light> lights, Matrix4f viewMatrix){
		for(int i=0;i<MAX_LIGHTS;i++){
			try {
				if(i<lights.size()){
					super.loadVector(location_lightPositionEyeSpace[i], getEyeSpacePosition(lights.get(i), viewMatrix));
					super.loadColor(location_lightColour[i], lights.get(i).getColor());
				}else{
					super.loadVector(location_lightPositionEyeSpace[i], new Vector3f(0, 0, 0));
					super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				}
			} catch (NullPointerException e) {
//                throw new RuntimeException(e);
			}
		}
	}
	
	protected void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	protected void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix){
		Vector3f position = light.gameObject.transform.position;
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		eyeSpacePos = viewMatrix.transform(eyeSpacePos);
		return new Vector3f(eyeSpacePos.x, eyeSpacePos.y, eyeSpacePos.z);
	}
}
