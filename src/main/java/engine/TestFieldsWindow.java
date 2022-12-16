package engine;

import engine.imGui.EditorImGui;
import engine.renderEngine.textures.Texture;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestFieldsWindow {

    public static float[] testFloats = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
    public static int[] testInts = { 0, 0, 0, 0 };
    public static String[] testStrings = { "", "", "" };
    public static boolean[] testBooleans = { false, false, false, false, false, false };
    public static Vector2f[] testVectors2f = { new Vector2f(0.0f), new Vector2f(0.0f) };
    public static Vector3f[] testVectors3f = { new Vector3f(0.0f), new Vector3f(0.0f) };
    public static Color[] testColors = { Color.White, Color.White };

    static Texture testTexture;
    static Texture testTexture2;
    static Texture testTexture3;

    public static void imgui() {
        ImGui.begin(" Test Fields ");

        if (ImGui.collapsingHeader("Boolean Types")) {
            testBooleans[0] = EditorImGui.field_Boolean("True boolean(Switch)", testBooleans[0], EditorImGui.BooleanType.Switch);
            testBooleans[1] = !EditorImGui.field_Boolean("False boolean(Switch)", !testBooleans[1], EditorImGui.BooleanType.Switch);
            testBooleans[2] = EditorImGui.field_Boolean("True boolean(Checkbox)", testBooleans[2], EditorImGui.BooleanType.Checkbox);
            testBooleans[3] = !EditorImGui.field_Boolean("False boolean(Checkbox)", !testBooleans[3], EditorImGui.BooleanType.Checkbox);
            testBooleans[4] = EditorImGui.field_Boolean("True boolean(Bullet)", testBooleans[4], EditorImGui.BooleanType.Bullet);
            testBooleans[5] = !EditorImGui.field_Boolean("False boolean(Bullet)", !testBooleans[5], EditorImGui.BooleanType.Bullet);
            ImGui.separator();
        }

        if (ImGui.collapsingHeader("Texture Fields")) {
            EditorImGui.header("Texture Used Intensity, range(-1.0, 1.0)");
            testTexture = (Texture) EditorImGui.field_Texture("Texture1", testTexture, new Vector2f(1.0f), new Vector2f(0.0f), true, 1, -1.0f, 1.0f).get(0);
            EditorImGui.header("Texture Used Intensity, range(0.0, 2.0)");
            testTexture2 = (Texture) EditorImGui.field_Texture("Texture2", testTexture2, new Vector2f(2.0f), new Vector2f(1.0f), true, 0.5f, 0.0f, 2.0f).get(0);
            EditorImGui.header("Texture Not Use Intensity");
            testTexture3 = EditorImGui.field_Texture("Texture3", testTexture3, new Vector2f(1.0f), new Vector2f(0.0f));
            ImGui.separator();
        }

        for (int i = 0; i < testFloats.length; i++)
            testFloats[i] = EditorImGui.field_Float("Test Float (" + i + ")", testFloats[i]);

        for (int i = 0; i < testInts.length; i++)
            testInts[i] = EditorImGui.field_Int_WithButtons("Test Int (" + i + ")", testInts[i]);
//                        EditorImGui.field_Int("Tets int", 0);

        for (int i = 0; i < testStrings.length; i++)
            testStrings[i] = EditorImGui.field_Text("Test String (" + i + ")", testStrings[i], "Test");

        for (int i = 0; i < testBooleans.length; i++)
            testBooleans[i] = EditorImGui.field_Boolean("Test Boolean (" + i + ")", testBooleans[i]);

        for (int i = 0; i < testVectors2f.length; i++)
            testVectors2f[i] = EditorImGui.field_Vector2f("Test Vector2 (" + i + ")", testVectors2f[i]);

        for (int i = 0; i < testVectors3f.length; i++)
            testVectors3f[i] = EditorImGui.field_Vector3f("Test Vector3 (" + i + ")", testVectors3f[i]);

        for (int i = 0; i < testColors.length; i++)
            EditorImGui.field_Color_WithAlpha("Test Color (" + i + ")", testColors[i]);

        ImGui.end();
    }
}
