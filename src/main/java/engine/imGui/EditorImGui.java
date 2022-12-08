package engine.imGui;

import engine.TestFieldsWindow;
import engine.renderEngine.Loader;
import engine.renderEngine.postProcessing.PostProcessLayer;
import engine.renderEngine.postProcessing.PostProcessing;
import engine.toolbox.KeyListener;
import engine.toolbox.Maths;
import engine.toolbox.MouseListener;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.internal.ImGuiWindow;
import imgui.internal.ImRect;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class EditorImGui {

    private static float leftPadding = 150.0f;
    private static float textVerticalOffset = 4.0f;

    public static void helpMarker(String description) { helpMarker("(?)", description); }

    public static void helpMarker(String customMarker, String description) {
        ImGui.textDisabled(customMarker);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(description);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    public static void multilineTextCentered(String text) {
        float win_width = ImGui.getWindowSize().x;
        ImVec2 tmp = new ImVec2();
        ImGui.calcTextSize(tmp, text);
        float text_width = tmp.x;

        // calculate the indentation that centers the text on one line, relative
        // to window left, regardless of the `ImGuiStyleVar_WindowPadding` value
        float text_indentation = (win_width - text_width) * 0.5f;

        // if text is too long to be drawn on one line, `text_indentation` can
        // become too small or even negative, so we check a minimum indentation
        float min_indentation = 20.0f;
        if (text_indentation <= min_indentation)
            text_indentation = min_indentation;

        ImGui.sameLine(text_indentation);
        ImGui.pushTextWrapPos(win_width - text_indentation);
        ImGui.textWrapped(text);
        ImGui.popTextWrapPos();
    }

    public static void horizontalCenteredText(String text) {
        ImVec2 tmp = new ImVec2();
        ImGui.calcTextSize(tmp, text);
        ImGui.setCursorPosX((ImGui.getWindowWidth() - tmp.x) / 2.0f);
        ImGui.text(text);
    }

    public static boolean horizontalCenterButton(String buttonText, float horizontalMargin) {
        return horizontalCenterButton(buttonText, horizontalMargin, false);
    }

    public static boolean horizontalCenterButton(String buttonText, boolean stretchHorizontal) {
        return horizontalCenterButton(buttonText, 0.0f, stretchHorizontal);
    }

    public static boolean horizontalCenterButton(String buttonText, float horizontalMargin, boolean stretchHorizontal) {
        if (!stretchHorizontal) {
            float width = 0.0f;

            ImVec2 tmp = new ImVec2(width, 0);
            ImGui.calcTextSize(tmp, buttonText);
            float horP = (ImGui.getContentRegionAvailX() / 2.0f) - horizontalMargin - tmp.x;
            width += tmp.x;
            width += (horP > 6.0f ? horP * 2.0f : 0.0f);

            float avail = ImGui.getContentRegionAvailX();
            float off = (avail - width) * 0.5f;
            if (off > 0.0f)
                ImGui.setCursorPos(ImGui.getCursorPosX() + off, ImGui.getCursorPosY());

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding,
                    (Math.max(horP, 6.0f)),
                    ImGui.getStyle().getFramePaddingY());
        } else {
            ImVec2 tmp = new ImVec2();
            ImGui.calcTextSize(tmp, buttonText);

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding,
                    (ImGui.getContentRegionAvailX() / 2.0f) - (tmp.x / 2.0f),
                    ImGui.getStyle().getFramePaddingY());
        }
        boolean button = ImGui.button(buttonText);
        ImGui.popStyleVar(1);
        return button;
    }

    public static void header(String header) {
        ImGui.pushID(header);
        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, 1.5f);
        ImGui.nextColumn();

        ImGui.text(header);

        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawVec2Control(String label, Vector2f values) { drawVec2Control(label, values, 0.0f); }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f - (ImGui.getStyle().getItemSpacingX() * 1.5f);

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 204, 36, 29, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 224, 50, 43, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 204, 36, 29, 255);
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
            values.x = resetValue;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 2f) - (buttonSize.x / 2) - ImGui.getStyle().getItemSpacingX());
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 152, 151, 26, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 172, 165, 40, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 152, 151, 26, 255);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValue;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawVec3Control(String label, Vector3f values) { drawVec3Control(label, values, 0.0f); }

    public static void drawVec3Control(String label, Vector3f values, float resetValue) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f) / 3.0f - (ImGui.getStyle().getItemSpacingX() * 1.5f);

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 204, 36, 29, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 224, 50, 43, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 204, 36, 29, 255);
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
            values.x = resetValue;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 3f) - (buttonSize.x - (buttonSize.x / 2) + (ImGui.getStyle().getItemSpacingX() * 2.5f)));
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 152, 151, 26, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 172, 165, 40, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 152, 151, 26, 255);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValue;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 2f) - ((buttonSize.x / 2f) + ImGui.getStyle().getItemSpacingX()));
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 69, 133, 136, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 89, 147, 150, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 69, 133, 136, 255);
        if (ImGui.button("Z", buttonSize.x, buttonSize.y))
            values.z = resetValue;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesZ = {values.z};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat("##z", vecValuesZ, 0.1f);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];
        values.z = vecValuesZ[0];

        ImGui.columns(1);
        ImGui.popID();
    }

    public static float dragFloat(String label, float value) { return dragFloat(label, value, 0.1f, -999_999_999, 999_999_999); }

    public static float dragFloat(String label, float value, float speed) { return dragFloat(label, value, speed, -999_999_999, 999_999_999); }

    public static float dragFloat(String label, float value, float speed, float min) { return dragFloat(label, value, speed, min, 999_999_999); }

    public static float dragFloat(String label, float value, float speed, float min, float max) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat("##dragFloat" + label, valArr, speed, min, max);

        ImGui.columns(1);
        ImGui.popID();

        return Maths.clamp(valArr[0], min, max);
    }

    public static float sliderFloat(String label, float value, float speed, float min, float max) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        float sliderHeight = 3.5f;
        float sliderHandleSize = 11.4f;

        float dragFloatWidth = 50.0f;

        float startX = ImGui.getCursorPosX();
        float startY = ImGui.getCursorPosY();
        float endX = ImGui.getWindowContentRegionMaxX() - sliderHandleSize - 8.0f - dragFloatWidth;

        float pos = endX - startX - (Maths.normalize(1 - value, min, max) * (endX - startX)) + (sliderHandleSize / 2.0f) - 2.5f;

        //<editor-fold desc="Colors">
        ImVec4 tmpColor = ImGui.getStyle().getColor(ImGuiCol.Button);
        Color buttonColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);
        tmpColor = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);
        Color buttonHoverColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);
        tmpColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
        Color buttonActiveColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);

        tmpColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgActive);
        Color fillColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);

        tmpColor = ImGui.getStyle().getColor(ImGuiCol.TitleBg);
        Color backgroundColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w);

        tmpColor = ImGui.getStyle().getColor(ImGuiCol.Border);
        Color borderColor = new Color(tmpColor.x, tmpColor.y, tmpColor.z, tmpColor.w - 0.02f);
        //</editor-fold>

        ImVec2 rightFieldPos = new ImVec2(startX + ImGui.getContentRegionAvailX() - dragFloatWidth, startY);

        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 2.0f, startY + 14.0f - (sliderHeight / 2.0f) - 2.0f), new Vector2f(ImGui.getContentRegionAvailX() + 4.0f - dragFloatWidth - 8.0f, sliderHeight + 4.0f), borderColor);
        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 5.0f, startY + 14.0f - (sliderHeight / 2.0f)), new Vector2f(ImGui.getContentRegionAvailX() - dragFloatWidth - 9.0f, sliderHeight), backgroundColor);
        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 4.0f, startY + 14.0f - (sliderHeight / 2.0f)), new Vector2f(pos - 2.0f, sliderHeight), fillColor);

        float[] valArr = { value };

        //<editor-fold desc="Handle">
        ImGui.setCursorPos(startX + pos - 0.7f, startY + 5.0f);
        ImGui.setNextItemWidth(sliderHandleSize + 6.1f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0.0f, -0.4f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
        ImGui.pushStyleColor(ImGuiCol.Text, 0.0f, 0.0f, 0.0f, 0.0f);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, buttonColor.r, buttonColor.g, buttonColor.b, buttonColor.a);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, buttonActiveColor.r, buttonActiveColor.g, buttonActiveColor.b, buttonActiveColor.a);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, buttonHoverColor.r, buttonHoverColor.g, buttonHoverColor.b, buttonHoverColor.a);

        ImGui.dragFloat("##sliderHandle" + label, valArr, 0.293f * (speed / 1.5f), min, max);

        ImGui.popStyleColor(4);
        ImGui.popStyleVar(2);
        //</editor-fold>

        ImGui.setCursorPos(rightFieldPos.x, rightFieldPos.y);
        ImGui.setNextItemWidth(dragFloatWidth);
        ImGui.dragFloat("##sliderDragFloat" + label, valArr, speed, min, max);

        ImGui.columns(1);
        ImGui.popID();

        return Maths.clamp(valArr[0], min, max);
    }

    public static int dragInt(String label, int value) { return dragInt(label, value, 1); }

    public static int dragInt(String label, int value, int speed) { return dragInt(label, value, speed, -999_999_999, 999_999_999); }

    public static int dragInt(String label, int value, int speed, int min) { return dragInt(label, value, speed, min, 999_999_999); }

    public static int dragInt(String label, int value, int speed, int min, int max) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        int[] valArr = {value};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragInt("##dragFloat" + label, valArr, speed, min, max);

        ImGui.columns(1);
        ImGui.popID();

        return Maths.clamp(valArr[0], min, max);
    }

    public static int inputInt(String label, int value) { return inputInt(label, value, 1, -999_999_999, 999_999_999); }

    public static int inputInt(String label, int value, int step) { return inputInt(label, value, step, -999_999_999, 999_999_999); }

    public static int inputInt(String label, int value, int step, int min) { return inputInt(label, value, step, min, 999_999_999); }

    public static int inputInt(String label, int value, int step, int min, int max) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImInt val = new ImInt(value);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.inputInt("##inputInt" + label, val, step);

        ImGui.columns(1);
        ImGui.popID();

        return Maths.clamp(val.get(), min, max);
    }

    public static boolean colorPicker3(String label, Color color) {
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        float[] imColor = { color.r / 255, color.g / 255, color.b / 255};
        if (ImGui.colorEdit3("##colorPicker" + label, imColor)) {
            color.set(imColor[0] * 255, imColor[1] * 255, imColor[2] * 255);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    public static boolean colorPicker4(String label, Color color) {
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        float[] imColor = { color.r / 255, color.g / 255, color.b / 255, color.a / 255 };
        if (ImGui.colorEdit4("##colorPicker" + label, imColor)) {
            color.set(imColor[0] * 255, imColor[1] * 255, imColor[2] * 255, imColor[3] * 255);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    public static String inputText(String label, String text, String placeHolder) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        float start = ImGui.getCursorPosX();
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImString outString = new ImString(text, 256);

        if (ImGui.inputText("##" + label, outString)) {
            ImGui.columns(1);
            ImGui.popStyleVar();
            ImGui.popID();
            return outString.get();
        }
        if (text.equals("")) {
            ImGui.sameLine();
            ImGui.setCursorPosX(start + 10.0f);
            ImGui.textDisabled(placeHolder);
        }

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();

        return text;
    }

    public static String inputTextNoLabel(String variableName, String text, String placeHolder) {
        ImGui.pushID(variableName);

        ImGui.setCursorPosX(ImGui.getCursorPosX() - 6f);
        float start = ImGui.getCursorPosX();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
        ImString outString = new ImString(text, 256);

        if (ImGui.inputText("##" + variableName, outString)) {
            ImGui.popStyleVar();
            ImGui.popID();
            return outString.get();
        }

        if (text.equals("")) {
            ImGui.sameLine();
            ImGui.setCursorPosX(start + 10.0f);
            ImGui.textDisabled(placeHolder);
        }

        ImGui.popStyleVar();
        ImGui.popID();

        return text;
    }

    public static boolean checkbox(String label, boolean value) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
//        ImGui.setColumnWidth(0, ImGui.getWindowWidth() - (ImGui.getStyle().getFramePaddingX() * 2.0f) - 50.0f - ImGui.getStyle().getWindowPaddingX()); // checkbox on right side
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        boolean endValue = ImGui.checkbox("##dragCheckbox" + label, value);

        ImGui.columns(1);
        ImGui.popID();

        return endValue;
    }

    public static boolean enumCombo(String label, ImInt currentItem, String[] items, int popupMaxHeightInItems) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.pushStyleColor(ImGuiCol.FrameBg,
                ImGui.getStyle().getColor(ImGuiCol.Button).x,
                ImGui.getStyle().getColor(ImGuiCol.Button).y,
                ImGui.getStyle().getColor(ImGuiCol.Button).z,
                ImGui.getStyle().getColor(ImGuiCol.Button).w);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered,
                ImGui.getStyle().getColor(ImGuiCol.ButtonHovered).x,
                ImGui.getStyle().getColor(ImGuiCol.ButtonHovered).y,
                ImGui.getStyle().getColor(ImGuiCol.ButtonHovered).z,
                ImGui.getStyle().getColor(ImGuiCol.ButtonHovered).w);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive,
                ImGui.getStyle().getColor(ImGuiCol.ButtonActive).x,
                ImGui.getStyle().getColor(ImGuiCol.ButtonActive).y,
                ImGui.getStyle().getColor(ImGuiCol.ButtonActive).z,
                ImGui.getStyle().getColor(ImGuiCol.ButtonActive).w);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX(), 10.0f);
        boolean a = (ImGui.combo("##" + label, currentItem, items, popupMaxHeightInItems));
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);

        ImGui.columns(1);
        ImGui.popID();

        return a;
    }

    public static boolean toggledButton(String text, boolean value) {
        if (value) {
            ImGui.pushStyleColor(ImGuiCol.Button,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBg).x,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBg).y,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBg).z,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBg).w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered).x,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered).y,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered).z,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered).w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgActive).x,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgActive).y,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgActive).z,
                    ImGui.getStyle().getColor(ImGuiCol.FrameBgActive).w);

            if (ImGui.button(text))
                value = false;

            ImGui.popStyleColor(3);
        } else {
            if (ImGui.button(text))
                value = true;
        }

        return value;
    }

    public static void drawRectangle(Vector2f position, Vector2f scale, Color color) {
        ImGui.setCursorPos(position.x, position.y);
        ImGui.image(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png"), scale.x, scale.y, 0, 0, 0, 1, color.r, color.g, color.b, color.a);
    }
}
