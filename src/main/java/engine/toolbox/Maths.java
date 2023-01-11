package engine.toolbox;

import engine.entities.EditorCamera;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

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

    public static void decomposeTransformationMatrix(float[] transformationMatrixArray, Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.set(transformationMatrixArray);
        decomposeTransformationMatrix(matrix, translation, rotation, scale);
    }

    public static boolean decomposeTransformationMatrix(Matrix4f transformationMatrix, Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f localMatrix = new Matrix4f(transformationMatrix);

        // Normalize the matrix
        if (localMatrix.get(3, 3) == Math.ulp(0))
            return false;

        // First, isolate perspective.  This is the messiest.
        if (localMatrix.get(0, 3) != Math.ulp(0) || localMatrix.get(1, 3) != Math.ulp(0) || localMatrix.get(2, 3) != Math.ulp(0)) {
            // Clear the perspective partition
            localMatrix.set(0, 3, 0);
            localMatrix.set(1, 3, 0);
            localMatrix.set(2, 3, 0);
            localMatrix.set(3, 3, 1);
        }

        // Next take care of translation (easy).
        translation.set(localMatrix.get(3, 0), localMatrix.get(3, 1), localMatrix.get(3, 2));
        localMatrix.set(3, 0, 0);
        localMatrix.set(3, 1, 0);
        localMatrix.set(3, 2, 0);

        Vector3f[] row = new Vector3f[3];
        Vector3f pdum3;

        for (int i = 0; i < 3; i++)
            row[i] = new Vector3f(0.0f);

        // Now get scale and shear.
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0: row[i].x = localMatrix.get(i, j);
                    case 1: row[i].y = localMatrix.get(i, j);
                    case 2: row[i].z = localMatrix.get(i, j);
                }
            }

        // Compute X scale factor and normalize first row.
        scale.x = row[0].length();
        row[0] = row[0].normalize();
        scale.y = row[1].length();
        row[1] = row[1].normalize();
        scale.z = row[2].length();
        row[2] = row[2].normalize();

        // At this point, the matrix (in rows[]) is orthonormal.
        // Check for a coordinate system is flip. If the determinate.
        // Is -1, then negate the matrix and the scaling factors.

        pdum3 = row[1].cross(row[2]);
        if (row[0].dot(pdum3) < 0) {
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0: scale.x *= 1;
                    case 1: scale.y *= 1;
                    case 2: scale.z *= 1;
                }
                row[i] = row[i].mul(-1);
            }
        }

//        rotation.y = (float) Math.asin(-row[0].z); // TODO FIX ROTATION CALCULATION
//        if (Math.cos(rotation.y) != 0) {
//            rotation.x = (float) Math.atan2(row[1].z, row[2].z);
//            rotation.z = (float) Math.atan2(row[0].y, row[0].x);
//        } else {
//            rotation.x = (float) Math.atan2(-row[2].x, row[1].y);
//            rotation.z = 0.0f;
//        }

        return true;
    }

//    public static void decomposeTransformationMatrix(Matrix4f transformationMatrix, Vector3f translation, Vector3f rotation, Vector3f scale) {
//        Matrix4f matrix = new Matrix4f(transformationMatrix);
//        Vector3f tmpScale = new Vector3f(0.0f);
//
//        translation.x = matrix.get(3, 0);
//        translation.y = matrix.get(3, 1);
//        translation.z = matrix.get(3, 2);
//
//        tmpScale.set(matrix.get(0, 0), matrix.get(0, 1), matrix.get(0, 2));
//        scale.x = tmpScale.length();
//        tmpScale.set(matrix.get(1, 0), matrix.get(1, 1), matrix.get(1, 2));
//        scale.y = tmpScale.length();
//        tmpScale.set(matrix.get(2, 0), matrix.get(2, 1), matrix.get(2, 2));
//        scale.z = tmpScale.length();
//
////        matrix.set(0, 0, matrix.get(0, 0) / scale.x);
////        matrix.set(0, 1, matrix.get(0, 1) / scale.x);
////        matrix.set(0, 2, matrix.get(0, 2) / scale.x);
////        matrix.set(1, 0, matrix.get(1, 0) / scale.y);
////        matrix.set(1, 1, matrix.get(1, 1) / scale.y);
////        matrix.set(1, 2, matrix.get(1, 2) / scale.y);
////        matrix.set(2, 0, matrix.get(2, 0) / scale.z);
////        matrix.set(2, 1, matrix.get(2, 1) / scale.z);
////        matrix.set(2, 2, matrix.get(2, 2) / scale.z);
//
//        //, matrix.get(0, 1) / scale.x, matrix.get(0, 2) / scale.x)
////        rotation.x = tmpScale.length();
////        tmpScale.set(matrix.get(1, 0) / scale.y, matrix.get(1, 1) / scale.y, matrix.get(1, 2) / scale.y);
////        rotation.y = tmpScale.length();
////        tmpScale.set(matrix.get(2, 0) / scale.z, matrix.get(2, 1) / scale.z, matrix.get(2, 2) / scale.z);
////        rotation.z = tmpScale.length();
//
//        rotation.y = (float) Math.asin(-matrix.get(0, 2) * scale.y);
//        if (Math.cos(rotation.y * scale.y) != 0) {
//            rotation.x = (float) Math.atan2(matrix.get(1, 2) * scale.x, matrix.get(2, 2) * scale.x);
//            rotation.z = (float) Math.atan2(matrix.get(0, 1) * scale.z, matrix.get(0, 0) * scale.z);
//        } else {
//            rotation.x = (float) Math.atan2(-matrix.get(2, 0) * scale.x, matrix.get(1, 1) * scale.x);
//            rotation.z = 0.0f;
//        }
//
//        System.out.println(translation.x + " " + translation.y + " " + translation.z);
//        System.out.println(rotation.x + " " + rotation.y + " " + rotation.z);
//        System.out.println(scale.x + " " + scale.y + " " + scale.z);
//    }

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
