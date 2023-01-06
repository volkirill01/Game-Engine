package engine.toolbox;

import engine.entities.EditorCamera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Maths {

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, float rotation, Vector2f scale) {
        Matrix4f transformationMatrix = new Matrix4f().identity(); // TODO ADD ROTATION TO GUI

        transformationMatrix.translate(translation.x, translation.y, 0);
        transformationMatrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1));
        transformationMatrix.scale(scale.x, scale.y, 1.0f);
        return transformationMatrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f transformationMatrix = new Matrix4f().identity();

        transformationMatrix.translate(translation.x, translation.y, translation.z);
        transformationMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        transformationMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        transformationMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        transformationMatrix.scale(scale.x, scale.y, scale.z);

        return transformationMatrix;
    }

    public static Matrix4f createViewMatrix(EditorCamera editorCamera) {
        Matrix4f viewMatrix = new Matrix4f().identity();

        viewMatrix.rotate((float)Math.toRadians(editorCamera.getPitch()), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float)Math.toRadians(editorCamera.getYaw()), new Vector3f(0, 1, 0));
        Vector3f cameraPosition = editorCamera.getPosition();
        Vector3f negativeCameraPosition = new Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        viewMatrix.translate(negativeCameraPosition.x, negativeCameraPosition.y, negativeCameraPosition.z);

        return viewMatrix;
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    public static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }

    public static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }

    public static float normalize(float value, float min, float max) { return ((value - min) / (max - min)); }
}
