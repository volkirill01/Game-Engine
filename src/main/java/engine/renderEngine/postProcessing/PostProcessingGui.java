package engine.renderEngine.postProcessing;

import engine.imGui.EditorImGui;
import engine.imGui.EditorImGuiWindow;
import engine.renderEngine.Loader;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;

import java.util.Arrays;
import java.util.List;

public class PostProcessingGui extends EditorImGuiWindow {

    public void imgui() {
        ImGui.begin(" Post Processing ");

        EditorImGui.header("Ambient");
        EditorImGui.colorPicker4("Ambient Light Color", PostProcessing.getAmbientLightColor());
        PostProcessing.setAmbientLightIntensity(EditorImGui.dragFloat("Ambient Light Intensity", PostProcessing.getAmbientLightIntensity(), 0.02f, 0));

        ImGui.separator();
        EditorImGui.header("Fog");
        EditorImGui.colorPicker3("Fog Color", PostProcessing.getFogColor());
        PostProcessing.setFogDensity(EditorImGui.dragFloat("Fog Density", PostProcessing.getFogDensity() * 10, 0.02f, 0, 1) / 10);
        PostProcessing.setFogSmoothness(EditorImGui.dragFloat("Fog Smoothness", PostProcessing.getFogSmoothness() / 10, 0.02f, 0.001f) * 10);

        ImGui.separator();
        EditorImGui.header("Post Processing Layers");
        if (EditorImGui.checkbox("Use PostProcessing", PostProcessing.usePostProcessing))
            PostProcessing.usePostProcessing = !PostProcessing.usePostProcessing;
        ImGui.separator();

        drawLayers();

        if (PostProcessing.getLayers().size() > 0)
            ImGui.separator();

        if (EditorImGui.horizontalCenterButton("Add Post Effect", 60.0f))
            ImGui.openPopup("##postEffectAdder");

        if (ImGui.beginPopup("##postEffectAdder")) {
            for (PostProcessLayer layer : PostProcessing.getAllPostProcessLayers()) {
                if (!layer.getPostEffectName().startsWith("##"))
                    if (ImGui.menuItem(layer.getPostEffectName()))
                        PostProcessing.addLayer(layer.copy());
            }
            ImGui.endPopup();
        }

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

                String current_item = "";
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
                ImGui.setCursorPos(ImGui.getWindowWidth() - (16.0f * 2.0f) + 3.0f - arrowsSize, ImGui.getCursorPosY() + (16.0f / 2.0f));
                ImVec2 dropDownPos = ImGui.getCursorPos();
                ImGui.image(Loader.get().loadTexture("engineFiles/images/utils/icon=ellipsis-solid(32x32).png"), 16, 16);

                ImGui.setCursorPos(dropDownPos.x - 8.0f, dropDownPos.y - 8.0f);
                ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
                ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX() * 2, ImGui.getStyle().getItemSpacingY() * 1.4f);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() * 2, ImGui.getStyle().getFramePaddingY() * 2);
                if (ImGui.beginCombo("##postProcessLayerCombo", current_item, ImGuiComboFlags.NoArrowButton)) {
                    if (ImGui.menuItem("Remove effect")) {
                        PostProcessing.removeLayer(layer);
                    }
                    EditorImGui.helpMarker("Help (?)", "Tooltip");
                    ImGui.endCombo();
                }
                ImGui.popStyleColor(4);
                ImGui.popStyleVar(3);
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
