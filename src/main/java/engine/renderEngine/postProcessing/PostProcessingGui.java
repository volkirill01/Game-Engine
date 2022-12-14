package engine.renderEngine.postProcessing;

import engine.TestFieldsWindow;
import engine.components.Transform;
import engine.imGui.EditorImGui;
import engine.imGui.EditorImGuiWindow;
import engine.renderEngine.Loader;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;

import java.util.List;

public class PostProcessingGui extends EditorImGuiWindow {

    public void imgui() {
        ImGui.begin(" Post Processing ");

        EditorImGui.header("Ambient");
        EditorImGui.field_Color_WithAlpha("Ambient Light Color", PostProcessing.getAmbientLightColor());
        PostProcessing.setAmbientLightIntensity(EditorImGui.field_Float("Ambient Light Intensity", PostProcessing.getAmbientLightIntensity(), 0.02f, 0));

        ImGui.separator();
        EditorImGui.header("Fog");
        EditorImGui.filed_Color("Fog Color", PostProcessing.getFogColor());
        PostProcessing.setFogDensity(EditorImGui.field_Float("Fog Density", PostProcessing.getFogDensity() * 10, 0.02f, 0, 1) / 10);
        PostProcessing.setFogSmoothness(EditorImGui.field_Float("Fog Smoothness", PostProcessing.getFogSmoothness() / 10, 0.02f, 0.001f) * 10);

        ImGui.separator();
        EditorImGui.header("Post Processing Layers");
        PostProcessing.usePostProcessing = EditorImGui.field_Boolean("Use PostProcessing", PostProcessing.usePostProcessing);
        ImGui.separator();

        drawLayers();

        if (PostProcessing.getLayers().size() > 0)
            ImGui.separator();

        float centerOfWindow = ImGui.getWindowContentRegionMaxX() / 2.0f;
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 6.0f);
        boolean isOpen = EditorImGui.horizontalCenterButton("Add Post Effect", 60.0f);
        float popupPosY = ImGui.getCursorPosY();
        ImVec2 popupPosition = new ImVec2(centerOfWindow - 105.0f, popupPosY);

        if (EditorImGui.BeginPopup("PostEffectAdder", popupPosition, isOpen)) {
            for (PostProcessLayer layer : PostProcessing.getAllPostProcessLayers()) {
                if (!layer.getPostEffectName().startsWith("##"))
                    if (ImGui.menuItem(layer.getPostEffectName()))
                        PostProcessing.addLayer(layer.copy());
            }
            EditorImGui.EndPopup();
        }

        super.imgui();
        ImGui.end();
    }

    private void drawLayers() {
        float arrowsSize = 22.0f;
        float checkboxSize = 37.0f;

        List<PostProcessLayer> layers = PostProcessing.getLayers();
        int layersCount = layers.size();

        for (int i = 0; i < layersCount; i++) {
            try {
                PostProcessLayer layer = layers.get(i);

                ImGui.pushID("##postProcessLayer" + i);

                ImGui.columns(3, "", false);

                //<editor-fold desc="Is Active checkbox">
                ImGui.setColumnWidth(0, checkboxSize);
                if (ImGui.checkbox("##isActive", layer.isActive()))
                    layer.setActive(!layer.isActive());
                ImGui.nextColumn();
                //</editor-fold>

                //<editor-fold desc="Header">
                if (!layer.isActive()) {  // ------------------------------------------
                    ImVec4 buttonDisabledColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
                    ImVec4 textDisabledColor = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);

                    ImGui.pushStyleColor(ImGuiCol.Text, textDisabledColor.x, textDisabledColor.y, textDisabledColor.z, textDisabledColor.w);
                    ImGui.pushStyleColor(ImGuiCol.CheckMark, textDisabledColor.x, textDisabledColor.y, textDisabledColor.z, textDisabledColor.w);

                    ImGui.pushStyleColor(ImGuiCol.Button, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);

                    ImGui.pushStyleColor(ImGuiCol.Header, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
                    ImGui.pushStyleColor(ImGuiCol.HeaderHovered, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
                    ImGui.pushStyleColor(ImGuiCol.HeaderActive, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
                }

                ImGui.setColumnWidth(1, ImGui.getWindowWidth() - ImGui.getStyle().getWindowPaddingX() - checkboxSize - arrowsSize);
                boolean collapsingHeader = ImGui.collapsingHeader(layer.getPostEffectName());
                ImVec2 headerPos = ImGui.getCursorPos();
                ImGui.setItemAllowOverlap();
                //</editor-fold>

                //<editor-fold desc="Dropdown menu">
                ImGui.sameLine();
                ImGui.setCursorPos(ImGui.getWindowWidth() - (16.0f * 2.0f) + 3.0f - arrowsSize - 7.0f, ImGui.getCursorPosY() + (16.0f / 2.0f) - 7.0f);
                if (EditorImGui.BeginButtonDropDownImage(
                        Loader.get().loadTexture("engineFiles/images/utils/icon=ellipsis-solid(32x32).png").getTextureID(),
                        "PostProcessLayerMenu", new ImVec2(18, 18), true)) {

                    if (ImGui.menuItem("Reset"))
                        layer.reset();

                    ImGui.separator();
                    if (ImGui.menuItem("Remove Layer"))
                        PostProcessing.removeLayer(layer);

                    if (i > 0) {
                        if (ImGui.menuItem("Move Up"))
                            PostProcessing.swapTwoLayers(i, i - 1);
                    } else
                        ImGui.textDisabled("Move Up"); // TODO IF MOVE UP OR DOWN DON'T WORK, REPLACE IF STATEMENTS

                    if (i < layersCount - 1) {
                        if (ImGui.menuItem("Move Down"))
                            PostProcessing.swapTwoLayers(i, i + 1);
                    } else
                        ImGui.textDisabled("Move Down");

                    EditorImGui.EndButtonDropDown();
                }
                //</editor-fold>

                //<editor-fold desc="Arrows">
                ImGui.nextColumn();

                if (layersCount == 1) {
                    ImVec4 disabledButtonColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgActive);
                    ImGui.pushStyleColor(ImGuiCol.Button, disabledButtonColor.x, disabledButtonColor.y, disabledButtonColor.z, disabledButtonColor.w);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, disabledButtonColor.x, disabledButtonColor.y, disabledButtonColor.z, disabledButtonColor.w);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, disabledButtonColor.x, disabledButtonColor.y, disabledButtonColor.z, disabledButtonColor.w);
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.0f, ImGui.getStyle().getFramePaddingY());
                    ImGui.button("##arrowsBackground");
                    ImGui.popStyleVar();
                    ImGui.popStyleColor(3);
                } else if (i != 0 && i < layersCount - 1) {
                    //<editor-fold desc="Arrows background">
                    ImVec2 buttonPos = ImGui.getCursorPos();
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.0f, ImGui.getStyle().getFramePaddingY());
                    ImGui.button("##arrowsBackground");
                    ImGui.popStyleVar();
                    ImGui.setItemAllowOverlap();

                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.0f, -3.0f);
                    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, -2f);
                    ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);
                    ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
                    ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

                    float arrowsOffset = 4.0f;
                    ImGui.setCursorPos(buttonPos.x, buttonPos.y + 0.5f);
                    buttonPos = ImGui.getCursorPos();
                    //</editor-fold>

                    //<editor-fold desc="Up button">
                    if (ImGui.button("##\uEA6A"))
                        PostProcessing.swapTwoLayers(i, i - 1);

                    ImGui.setCursorPos(buttonPos.x + 2.0f, buttonPos.y - 3.8f);
                    ImGui.textDisabled("_");
                    ImGui.setCursorPos(buttonPos.x + 6.8f, buttonPos.y - 3.8f);
                    ImGui.textDisabled("_");

                    ImGui.setCursorPos(buttonPos.x, buttonPos.y - arrowsOffset);
                    ImGui.text("\uEA6A");
                    buttonPos = ImGui.getCursorPos();
                    //</editor-fold>

                    //<editor-fold desc="Down button">
                    if (ImGui.button("##\uEA67"))
                        PostProcessing.swapTwoLayers(i, i + 1);

                    ImGui.setCursorPos(buttonPos.x, buttonPos.y - arrowsOffset);
                    ImGui.text("\uEA67");
                    //</editor-fold>

                    ImGui.popStyleColor(4);
                    ImGui.popStyleVar(3);
                } else if (i == 0) {
                    //<editor-fold desc="Down button">
                    ImVec2 buttonPos = ImGui.getCursorPos();
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.0f, ImGui.getStyle().getFramePaddingY());
                    if (ImGui.button("##\uEA67"))
                        PostProcessing.swapTwoLayers(i, i + 1);
                    ImGui.popStyleVar();

                    float arrowsOffset = 4.0f;
                    ImGui.setCursorPos(buttonPos.x, buttonPos.y + arrowsOffset);
                    ImGui.text("\uEA67");
                    //</editor-fold>
                } else if (i == layersCount - 1) {
                    //<editor-fold desc="Up button">
                    ImVec2 buttonPos = ImGui.getCursorPos();
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.0f, ImGui.getStyle().getFramePaddingY());
                    if (ImGui.button("##\uEA6A"))
                        PostProcessing.swapTwoLayers(i, i - 1);
                    ImGui.popStyleVar();

                    float arrowsOffset = 4.0f;
                    ImGui.setCursorPos(buttonPos.x, buttonPos.y + arrowsOffset);
                    ImGui.text("\uEA6A");
                    //</editor-fold>
                }
                //</editor-fold>

                ImGui.columns(1);
                ImGui.setCursorPos(headerPos.x, headerPos.y);

                if (collapsingHeader) {
                    layer.imgui(layer.isActive(), "");
                    if (i < layersCount - 1)
                        ImGui.separator();
                }

                if (!layer.isActive())
                    ImGui.popStyleColor(8); // ------------------------------------------

                ImGui.popID();
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        }
    }
}
