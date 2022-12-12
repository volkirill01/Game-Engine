package engine;

import engine.imGui.EditorImGui;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestFieldsWindow {

    public static float[] testFloats = { 0.0f, 0.0f, 0.0f, 0.0f };
    public static int[] testInts = { 0, 0, 0, 0 };
    public static String[] testStrings = { "", "", "" };
    public static Vector2f[] testVectors2f = { new Vector2f(0.0f), new Vector2f(0.0f) };
    public static Vector3f[] testVectors3f = { new Vector3f(0.0f), new Vector3f(0.0f) };
    public static Color[] testColors = { Color.White, Color.White };

    public static void imgui() {
        ImGui.begin(" Test Fields ");

        for (int i = 0; i < testFloats.length; i++)
            testFloats[i] = EditorImGui.field_Float("Test Float (" + i + ")", testFloats[i]);

        for (int i = 0; i < testInts.length; i++)
            testInts[i] = EditorImGui.field_Int_WithButtons("Test Int (" + i + ")", testInts[i]);
        EditorImGui.field_Int("Tets int", 0);

        for (int i = 0; i < testStrings.length; i++)
            testStrings[i] = EditorImGui.field_Text("Test String (" + i + ")", testStrings[i], "Test");

        for (int i = 0; i < testVectors2f.length; i++)
            EditorImGui.field_Vector2f("Test Vector2 (" + i + ")", testVectors2f[i]);

        for (int i = 0; i < testVectors3f.length; i++)
            testVectors3f[i] = EditorImGui.field_Vector3f("Test Vector3 (" + i + ")", testVectors3f[i]);

        for (int i = 0; i < testColors.length; i++)
            EditorImGui.field_Color_WithAlpha("Test Color (" + i + ")", testColors[i]);

        ImGui.end();
    }
}
