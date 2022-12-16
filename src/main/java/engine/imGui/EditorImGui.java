package engine.imGui;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.Texture;
import engine.toolbox.Maths;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EditorImGui {

    private static float leftPadding = 150.0f;
    private static float textVerticalOffset = 4.0f;

    public enum BooleanType {
        Checkbox,
        Switch,
        Bullet
    }

    // Single line height
    //      (ImGui.getTextLineHeight() + ImGui.getStyle().getFramePaddingY())
    // Single line height with spacing
    //      (ImGui.getTextLineHeight() + (ImGui.getStyle().getFramePaddingY() * 2.0f) + ImGui.getStyle().getItemSpacingY())

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
        ImGui.pushID("Header-" + header);
        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, -1.5f);
        ImGui.nextColumn();

        ImGui.text(header);

        ImGui.columns(1);
        ImGui.popID();
    }

    public static Vector2f field_Vector2f(String label, Vector2f values) { return field_Vector2f(label, values, new Vector2f(0.0f)); }

    public static Vector2f field_Vector2f(String label, Vector2f values, Vector2f resetValues) {
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
            values.x = resetValues.x;
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
            values.y = resetValues.y;
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

        return values;
    }

    public static Vector3f field_Vector3f(String label, Vector3f values) { return field_Vector3f(label, values, new Vector3f(0.0f)); }

    public static Vector3f field_Vector3f(String label, Vector3f values, Vector3f resetValues) {
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
            values.x = resetValues.x;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = { values.x };
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 3f) - (buttonSize.x - (buttonSize.x / 2) + (ImGui.getStyle().getItemSpacingX() * 2.5f)));
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 152, 151, 26, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 172, 165, 40, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 152, 151, 26, 255);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValues.y;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = { values.y };
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 2f) - ((buttonSize.x / 2f) + ImGui.getStyle().getItemSpacingX()));
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 69, 133, 136, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 89, 147, 150, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 69, 133, 136, 255);
        if (ImGui.button("Z", buttonSize.x, buttonSize.y))
            values.z = resetValues.z;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesZ = { values.z };
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.dragFloat("##z", vecValuesZ, 0.1f);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];
        values.z = vecValuesZ[0];

        ImGui.columns(1);
        ImGui.popID();

        return values;
    }

    public static float field_Float(String label, float value) { return field_Float(label, value, 0.1f, -999_999_999, 999_999_999); }

    public static float field_Float(String label, float value, float speed) { return field_Float(label, value, speed, -999_999_999, 999_999_999); }

    public static float field_Float(String label, float value, float speed, float min) { return field_Float(label, value, speed, min, 999_999_999); }

    public static float field_Float(String label, float value, float speed, float min, float max) {
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

    public static float slider_Float(String label, float value) { return slider_Float(label, value, 0.05f, 0, 1); }

    public static float slider_Float(String label, float value, float speed) { return slider_Float(label, value, speed, 0, 1); }

    public static float slider_Float(String label, float value, float speed, float min) { return slider_Float(label, value, speed, min, 1); }

    public static float slider_Float(String label, float value, float speed, float min, float max) {
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

        float lineHeight = ImGui.getTextLineHeight() + ImGui.getStyle().getFramePaddingY();
        float finalYPos = startY + (lineHeight / 4.0f) + (sliderHeight / 2.0f);

        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 2.0f, finalYPos - 2.0f), new Vector2f(ImGui.getContentRegionAvailX() + 4.0f - dragFloatWidth - 8.0f, sliderHeight + 4.0f), borderColor);
        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 5.0f, finalYPos), new Vector2f(ImGui.getContentRegionAvailX() - dragFloatWidth - 9.0f, sliderHeight), backgroundColor);
        drawRectangle(new Vector2f(ImGui.getCursorPosX() + 4.0f, finalYPos), new Vector2f(pos - 2.0f, sliderHeight), fillColor);

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

    public static int field_Int(String label, int value) { return field_Int(label, value, 1); }

    public static int field_Int(String label, int value, int speed) { return field_Int(label, value, speed, -999_999_999, 999_999_999); }

    public static int field_Int(String label, int value, int speed, int min) { return field_Int(label, value, speed, min, 999_999_999); }

    public static int field_Int(String label, int value, int speed, int min, int max) {
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

    public static int field_Int_WithButtons(String label, int value) { return field_Int_WithButtons(label, value, 1, -999_999_999, 999_999_999); }

    public static int field_Int_WithButtons(String label, int value, int step) { return field_Int_WithButtons(label, value, step, -999_999_999, 999_999_999); }

    public static int field_Int_WithButtons(String label, int value, int step, int min) { return field_Int_WithButtons(label, value, step, min, 999_999_999); }

    public static int field_Int_WithButtons(String label, int value, int step, int min, int max) {
        ImGui.pushID("Int_WithButtons-" + label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImInt val = new ImInt(value);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 2.0f, ImGui.getStyle().getFramePaddingY());
        ImGui.inputInt("##inputInt" + label, val, step);
        ImGui.popStyleVar();

        ImGui.columns(1);
        ImGui.popID();

        return Maths.clamp(val.get(), min, max);
    }

    public static boolean filed_Color(String label, Color color) {
        boolean isColorChange = false;
        ImGui.pushID("ColorPicker3-" + label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        float[] imColor = { color.r / 255, color.g / 255, color.b / 255};
        if (ImGui.colorEdit3("##colorPicker" + label, imColor)) {
            color.set(imColor[0] * 255, imColor[1] * 255, imColor[2] * 255);
            isColorChange = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return isColorChange;
    }

    public static boolean field_Color_WithAlpha(String label, Color color) {
        boolean ifColorChange = false;
        ImGui.pushID("ColorPicker4-" + label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        float[] imColor = { color.r / 255, color.g / 255, color.b / 255, color.a / 255 };
        if (ImGui.colorEdit4("##colorPicker" + label, imColor)) {
            color.set(imColor[0] * 255, imColor[1] * 255, imColor[2] * 255, imColor[3] * 255);
            ifColorChange = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return ifColorChange;
    }

    public static String field_Text(String label, String text) { return field_Text(label, text, ""); }

    public static String field_Text(String label, String text, String placeHolder) {
        ImGui.pushID("TextInput-" + label);

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

    public static String field_Text_NoLabel(String label, String text) { return field_Text_NoLabel(label, text, ""); }

    public static String field_Text_NoLabel(String label, String text, String placeHolder) {
        ImGui.pushID("TextInput_NoLabel-" + label);

        ImGui.setCursorPosX(ImGui.getCursorPosX() - 6f);
        float start = ImGui.getCursorPosX();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
        ImString outString = new ImString(text, 256);

        if (ImGui.inputText("##" + label, outString)) {
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

    public static boolean field_Boolean(String label, boolean value) { return field_Boolean(label, value, BooleanType.Checkbox, false); }

    public static boolean field_Boolean(String label, boolean value, BooleanType type) { return field_Boolean(label, value, type, false); }

    public static boolean field_Boolean(String label, boolean value, BooleanType type, boolean onRight) {
        ImGui.pushID("Boolean(" + type.name() + ")-" + label);

        ImGui.columns(2, "", false);
        if (onRight)
            ImGui.setColumnWidth(0, ImGui.getWindowWidth() - (ImGui.getStyle().getFramePaddingX() * 2.0f) - 50.0f - ImGui.getStyle().getWindowPaddingX()); // checkbox on right side
        else
            ImGui.setColumnWidth(0, leftPadding);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        if (type == BooleanType.Checkbox) {
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            if (ImGui.checkbox("##booleanField" + label, value))
                value = !value;
        } else if (type == BooleanType.Switch) {
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingY() * 2.0f + 9.5f, ImGui.getStyle().getFramePaddingY() - 5.0f);
            ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + 5.0f);
            ImVec2 startPos = ImGui.getCursorPos();

            ImVec4 backgroundColor = !value ? ImGui.getStyle().getColor(ImGuiCol.FrameBg) : ImGui.getStyle().getColor(ImGuiCol.Button);
            ImVec4 backgroundHoverColor = !value ? ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered) : ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);

            ImGui.pushStyleColor(ImGuiCol.Button, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, backgroundHoverColor.x, backgroundHoverColor.y, backgroundHoverColor.z, backgroundHoverColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, backgroundHoverColor.x, backgroundHoverColor.y, backgroundHoverColor.z, backgroundHoverColor.w);
            boolean background = ImGui.button("##background");
            boolean isHover = ImGui.isItemHovered();
            ImGui.popStyleColor(3);

            ImGui.setItemAllowOverlap();
            ImGui.popStyleVar();

            float isOn = value ? (ImGui.getStyle().getFramePaddingY() * 2.0f + 7.0f) : 0.0f;
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingY() + 6.0f, ImGui.getStyle().getFramePaddingY() - 3.0f);
            ImGui.setCursorPos(startPos.x + isOn, startPos.y - 2.0f);

            ImVec4 buttonColor = !isHover ? ImGui.getStyle().getColor(ImGuiCol.Button) : ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);
            ImVec4 buttonHoverColor = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);

            buttonColor = new ImVec4(buttonColor.x + (value ? 0.06f : 0.0f), buttonColor.y + (value ? 0.06f : 0.0f), buttonColor.z + (value ? 0.06f : 0.0f), 1.0f);
            buttonHoverColor = new ImVec4(buttonHoverColor.x + (value ? 0.06f : 0.0f), buttonHoverColor.y + (value ? 0.06f : 0.0f), buttonHoverColor.z + (value ? 0.06f : 0.0f), 1.0f);

            ImGui.pushStyleColor(ImGuiCol.Button, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHoverColor.x, buttonHoverColor.y, buttonHoverColor.z, buttonHoverColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonHoverColor.x, buttonHoverColor.y, buttonHoverColor.z, buttonHoverColor.w);
            boolean button = ImGui.button("##button");
            ImGui.popStyleColor(3);
            ImGui.popStyleVar(2);

            if (background || button)
                value = !value;
        } else if (type == BooleanType.Bullet) {
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingY() + 6.0f, ImGui.getStyle().getFramePaddingY() - 3.0f);
            ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
            ImVec2 startPos = ImGui.getCursorPos();

            ImVec4 backgroundColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
            ImVec4 backgroundHoverColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);

            ImGui.pushStyleColor(ImGuiCol.Button, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, backgroundHoverColor.x, backgroundHoverColor.y, backgroundHoverColor.z, backgroundHoverColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, backgroundHoverColor.x, backgroundHoverColor.y, backgroundHoverColor.z, backgroundHoverColor.w);
            boolean background = ImGui.button("##background");
            boolean isHover = ImGui.isItemHovered();
            ImGui.popStyleColor(3);

            ImGui.setItemAllowOverlap();
            ImGui.popStyleVar();

            boolean button = false;
            if (value) {
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingY() + 0.5f, ImGui.getStyle().getFramePaddingY() - 8.5f);
                ImGui.setCursorPos(startPos.x + 5.5f, startPos.y + 5.5f);

                ImVec4 buttonColor = !isHover ? ImGui.getStyle().getColor(ImGuiCol.Button) : ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);
                ImVec4 buttonHoverColor = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);

                ImGui.pushStyleColor(ImGuiCol.Button, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHoverColor.x, buttonHoverColor.y, buttonHoverColor.z, buttonHoverColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonHoverColor.x, buttonHoverColor.y, buttonHoverColor.z, buttonHoverColor.w);
                button = ImGui.button("##button");
                ImGui.popStyleColor(3);
                ImGui.popStyleVar();
            }
            ImGui.popStyleVar();

            if (background || button)
                value = !value;
        }

        ImGui.columns(1);
        ImGui.popID();

        return value;
    }

    public static boolean checkbox(String label, boolean value) { return checkbox(label, value, false); }

    public static boolean checkbox(String label, boolean value, boolean onRight) {
        ImGui.pushID("Checkbox-" + label);

        ImGui.columns(2, "", false);
        if (onRight)
            ImGui.setColumnWidth(0, ImGui.getWindowWidth() - (ImGui.getStyle().getFramePaddingX() * 2.0f) - 50.0f - ImGui.getStyle().getWindowPaddingX()); // checkbox on right side
        else
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

    public static boolean collapsingHeader(String label) {
        ImGui.pushID("CollapsingHeader-" + label);

        ImVec4 idleColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
        ImVec4 hoveredColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);
        ImVec4 activeColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgActive);

        ImGui.pushStyleColor(ImGuiCol.Header, idleColor.x, idleColor.y, idleColor.z, idleColor.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, hoveredColor.x, hoveredColor.y, hoveredColor.z, hoveredColor.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, activeColor.x, activeColor.y, activeColor.z, activeColor.w);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

        boolean result = ImGui.collapsingHeader(label);
        ImGui.popStyleColor(4);

        ImGui.popID();

        return result;
    }

    public static Enum field_Enum(String label, Enum enumValue) {
        String[] enumValues = getEnumValues(enumValue.getClass());
        String enumType = enumValue.name();
        ImInt index = new ImInt(indexOf(enumType, enumValues));

        if (EditorImGui.enumCombo(label, index, enumValues, enumValues.length))
            return enumValue.getClass().getEnumConstants()[index.get()];

        return enumValue;
    }

    private static int indexOf(String str, String[] arr) {
        for (int i = 0; i < arr.length; i++)
            if (str.equals(arr[i]))
                return i;

        return -1;
    }

    private static <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValues : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValues.name();
            i++;
        }
        return enumValues;
    }

    public static boolean enumCombo(String label, ImInt currentItem, String[] items, int popupMaxHeightInItems) {
        ImGui.pushID("Enum-" + label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImVec4 buttonHoveredColor = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);

        ImVec2 startCursorPos = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonHoveredColor.x, buttonHoveredColor.y, buttonHoveredColor.z, buttonHoveredColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 2.0f, ImGui.getStyle().getFramePaddingY());
        ImGui.button("##Enum-Background");
        ImGui.setItemAllowOverlap();
        ImGui.popStyleVar();
        ImGui.popStyleColor();
        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing,
                ImGui.getStyle().getFramePaddingX() * 6.0f,
                ImGui.getStyle().getFramePaddingX());
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, (ImGui.getStyle().getFramePaddingX() * 2.0f), ImGui.getStyle().getFramePaddingY());
        boolean a = (ImGui.combo("##" + label, currentItem, items, popupMaxHeightInItems));
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(6);

        ImGui.columns(1);
        ImGui.popID();

        ImGui.setCursorPos(ImGui.getCursorPosX(), startCursorPos.y + ImGui.getTextLineHeight() + (ImGui.getStyle().getFramePaddingY() * 2.0f) + ImGui.getStyle().getItemSpacingY());

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

    public static void drawRectangle(ImVec2 position, ImVec2 scale, Color color) {
        ImGui.setCursorPos(position.x, position.y);
        ImGui.image(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png").getTextureID(), scale.x, scale.y, 0, 0, 0, 1, color.r, color.g, color.b, color.a);
    }

    public static void drawRectangle(Vector2f position, Vector2f scale, Color color) {
        ImGui.setCursorPos(position.x, position.y);
        ImGui.image(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png").getTextureID(), scale.x, scale.y, 0, 0, 0, 1, color.r, color.g, color.b, color.a);
    }

    public static boolean BeginButtonDropDownImage(int textureID, String popupLabel, ImVec2 buttonSize, boolean noBackground) {
        ImVec2 pos = ImGui.getCursorPos();

        ImVec2 size = new ImVec2(buttonSize.x, buttonSize.y);

        if (noBackground) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
        }

        boolean pressed = ImGui.imageButton(textureID, size.x, size.y, 0, 1, 1, 0);
        if (noBackground)
            ImGui.popStyleColor(4);

        return BeginPopup(popupLabel, buttonSize, pos, pressed);
    }

    public static boolean BeginButtonDropDown(String label, ImVec2 buttonSize) {
        ImVec2 pos = ImGui.getCursorPos();

        ImVec2 size = new ImVec2(buttonSize.x, buttonSize.y);
        boolean pressed = ImGui.button("##button", size.x, size.y);

        return BeginPopup(label, buttonSize, pos, pressed);
    }

    public static boolean BeginPopup(String label, ImVec2 position, boolean isOpen) { return BeginPopup(label, new ImVec2(0.0f, 0.0f), position, isOpen); }

    private static boolean BeginPopup(String label, ImVec2 buttonSize, ImVec2 position, boolean pressed) {
        // Popup
        ImVec2 popupPos = new ImVec2(ImGui.getWindowPosX() + position.x - buttonSize.x, ImGui.getWindowPosY() + position.y + buttonSize.y);

        ImGui.setNextWindowPos(popupPos.x, popupPos.y);

        if (pressed)
            ImGui.openPopup(label);

        if (ImGui.beginPopup(label)) {
            ImVec4 buttonColor = ImGui.getStyle().getColor(ImGuiCol.Button);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
            ImGui.pushStyleColor(ImGuiCol.ChildBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing,
                    ImGui.getStyle().getFramePaddingX() * 6.0f,
                    ImGui.getStyle().getFramePaddingX());
            return true;
        }

        return false;
    }

    public static void EndButtonDropDown() { EndPopup(); }

    public static void EndPopup() {
        ImGui.popStyleColor(3);
        ImGui.popStyleVar();
        ImGui.endPopup();
    }

    public static Object field_Asset(String label, Object field, Asset.AssetType assetType) {
        ImGui.pushID("AssetField-" + label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.nextColumn();

        ImVec4 backgroundColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);

        ImVec2 startCursorPos = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 2.0f, ImGui.getStyle().getFramePaddingY());
        ImGui.button("##AssetField-Background");
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        ImGui.setCursorPos(startCursorPos.x + (ImGui.getStyle().getFramePaddingX() * 2.0f), startCursorPos.y + ImGui.getStyle().getFramePaddingY());

        if (ImGui.beginDragDropTarget() && ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
            String[] payload = ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD");

            if (payload[0].equals(assetType.name()) && ImGui.acceptDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
                switch (assetType) {
                    case Model -> {
                        TexturedModel oldModel = (TexturedModel) field;
                        RawModel model = OBJLoader.loadOBJ(payload[1]);

                        Material material;
                        if (oldModel != null)
                            material = oldModel.getMaterial();
                        else
                            material = new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png"));

                        field = new TexturedModel(model, material);
                    }
                    case Image -> {
                        field = Loader.get().loadTexture(payload[1]);
                    }
                }
                System.out.println("Asset Field-" + payload[1]);
            }
            ImGui.endDragDropTarget();
        }

        String assetName = "null (" + assetType.name() + ")";
        int assetIcon = 0;

        if (field != null) {
            switch (assetType) {
                case Model -> {
                    TexturedModel model = (TexturedModel) field;
                    assetName = model.getRawModel().getFilepath();
                    assetIcon = Loader.get().loadTexture("engineFiles/images/icons/icon=cube-solid-(32x32).png").getTextureID();
                }
                case Image -> {
                    Texture texture = (Texture) field;
                    assetName = texture.getFilepath();
                    assetIcon = Loader.get().loadTexture("engineFiles/images/icons/icon=image-solid(32x32).png").getTextureID();
                }
            }
        }
        assetName = assetName.replace("\\", "/").split("/")[assetName.replace("\\", "/").split("/").length - 1];

        if (field != null) {
            ImGui.image(assetIcon, 16, 16, 0, 1, 1, 0);
            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getCursorPosX() + 1.5f, ImGui.getCursorPosY() - 1.0f);
        }
        ImGui.text(assetName.split("\\.")[0]);

        ImGui.columns(1);
        ImGui.popID();

        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + ImGui.getStyle().getFramePaddingY());

        return field;
    }


    public static Texture field_Texture(String label, Texture field, Vector2f tiling, Vector2f offset) { return (Texture) field_Texture(label, field, tiling, offset, false, 0.0f, 0.0f, 0.0f).get(0); }

    /**
     * returns List of two objects (Texture, intensity)
     */
    public static List<Object> field_Texture(String label, Texture field, Vector2f tiling, Vector2f offset, float intensity) { return field_Texture(label, field, tiling, offset, true, intensity, 0.0f, 1.0f); }

    /**
     * returns List of two objects (Texture, intensity)
     */
    public static List<Object> field_Texture(String label, Texture field, Vector2f tiling, Vector2f offset, boolean useIntensity, float intensity, float intensityMin, float intensityMax) {
        ImGui.pushID("TextureField-" + label);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t" + label);
        ImGui.setCursorPosX(ImGui.getCursorPosX() + leftPadding);

        ImVec4 backgroundColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
        ImVec4 backgroundHoveredColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);
        ImVec4 backgroundActiveColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgActive);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + 20.0f);
        ImVec2 startCursorPos = ImGui.getCursorPos();

        if (useIntensity) {
            float[] tmpIntensity = { intensity };
            ImGui.columns(2, "", false);
            ImGui.setColumnWidth(0, leftPadding);
            ImGui.setCursorPos(ImGui.getCursorPosX() - 2.0f, ImGui.getCursorPosY() - 17.0f);
            ImGui.text("\t\tIntensity");
            ImGui.nextColumn();
            ImGui.setCursorPos(ImGui.getCursorPosX() - 2.0f, ImGui.getCursorPosY() - 20.0f);
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 124.0f);
            ImGui.sliderFloat("##Intensity", tmpIntensity, intensityMin, intensityMax);
            intensity = tmpIntensity[0];
            ImGui.columns(1);
        }

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y + 19.0f);
        tiling = EditorImGui.vector2("Tiling", tiling, new Vector2f(1.0f), 62.0f);
        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y + 49.0f);
        offset = EditorImGui.vector2("Offset", offset, new Vector2f(0.0f), 62.0f);

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y - 44.0f);
        ImGui.setCursorPosX(ImGui.getContentRegionAvailX() + ImGui.getStyle().getWindowPaddingX() + 30.0f);
        ImVec2 backgroundPosition = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, backgroundHoveredColor.x, backgroundHoveredColor.y, backgroundHoveredColor.z, backgroundHoveredColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, backgroundActiveColor.x, backgroundActiveColor.y, backgroundActiveColor.z, backgroundActiveColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 60.0f, 51.5f);
        ImGui.button("##background");
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);

        ImVec2 finalPosition = ImGui.getCursorPos();

        if (ImGui.beginDragDropTarget() && ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
            String[] payload = ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD");

            if (payload[0].equals(Asset.AssetType.Image.name()) && ImGui.acceptDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
                field = Loader.get().loadTexture(payload[1]);
                System.out.println("Texture Field-" + payload[1]);
            }
            ImGui.endDragDropTarget();
        }

        if (field == null) {
            ImGui.setCursorPos(backgroundPosition.x + 28.0f, backgroundPosition.y + 50.0f);
            ImGui.text("null (Image)");
        } else {
            ImGui.setCursorPos(backgroundPosition.x + 6.0f, backgroundPosition.y + 6.0f);
            ImGui.image(field.getTextureID(), 109.0f, 109.0f, 0, 1, 1, 0);
        }

        ImGui.popID();

        List<Object> result = new ArrayList<>();
        result.add(field);
        result.add(intensity);

        ImGui.setCursorPos(finalPosition.x, finalPosition.y);

        return result;
    }

    private static Vector2f vector2(String label, Vector2f values, Vector2f resetValues, float size) {
        ImGui.pushID(label);

        ImGui.columns(2, "", false);
        ImGui.setColumnWidth(0, leftPadding);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + textVerticalOffset);
        ImGui.text("\t\t" + label);
        ImGui.nextColumn();

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f - (ImGui.getStyle().getItemSpacingX() * 1.5f);

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 204, 36, 29, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 224, 50, 43, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 204, 36, 29, 255);
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
            values.x = resetValues.x;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.setNextItemWidth((ImGui.getContentRegionAvailX() / 2f) - (buttonSize.x / 2) - ImGui.getStyle().getItemSpacingX() - size);
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 152, 151, 26, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 172, 165, 40, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 152, 151, 26, 255);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValues.y;
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - (size * 2.0f));
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popID();

        return values;
    }
}
