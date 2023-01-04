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
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class PostProcessingGui extends EditorImGuiWindow {

    public void imgui() {
        ImGui.begin(" \uEC5F Post Processing ");

        EditorImGui.header("Ambient");
        EditorImGui.field_Color_WithAlpha("Ambient Light Color", PostProcessing.getAmbientLightColor());
        PostProcessing.setAmbientLightIntensity(EditorImGui.field_Float("Ambient Light Intensity", PostProcessing.getAmbientLightIntensity(), 0.02f, 0));

        ImGui.separator();
        EditorImGui.header("Fog");
        PostProcessing.setUseFog(EditorImGui.field_Boolean("Use Fog", PostProcessing.isUseFog()));

        if (!PostProcessing.isUseFog())
            EditorImGui.pushDisabled();

        EditorImGui.filed_Color("Fog Color", PostProcessing.getFogColor());
        PostProcessing.setFogDensity(EditorImGui.field_Float("Fog Density", PostProcessing.getFogDensity() * 10, 0.02f, 0, 1) / 10);
        PostProcessing.setFogSmoothness(EditorImGui.field_Float("Fog Smoothness", PostProcessing.getFogSmoothness() / 10, 0.02f, 0.001f) * 10);

        if (!PostProcessing.isUseFog())
            EditorImGui.popDisabled();

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
        List<PostProcessLayer> layers = PostProcessing.getLayers();
        int layersCount = layers.size();

        for (int i = 0; i < layersCount; i++) {
            try {
                PostProcessLayer layer = layers.get(i);

                ImGui.pushID("##postProcessLayer" + i);

                if (!layer.isActive())
                    EditorImGui.pushDisabled();

                ImVec2 startCursorPos = ImGui.getCursorPos();

                //<editor-fold desc="Header">
                ImVec4 headerColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
                ImVec4 headerHoveredColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);
                ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.Header, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleColor(ImGuiCol.Button, headerColor.x, headerColor.y, headerColor.z, headerColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, headerHoveredColor.x, headerHoveredColor.y, headerHoveredColor.z, headerHoveredColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, headerHoveredColor.x, headerHoveredColor.y, headerHoveredColor.z, headerHoveredColor.w);
                ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);

                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 2.0f, ImGui.getStyle().getFramePaddingY());
                ImGui.button("##componentBackground");
                ImGui.setItemAllowOverlap();
                ImGui.popStyleVar();

                ImGui.setCursorPos(startCursorPos.x + 35.0f, startCursorPos.y);
                ImVec4 separatorColor = ImGui.getStyle().getColor(ImGuiCol.Separator);
                separatorColor.w -= 0.2f;
                EditorImGui.drawRectangle(startCursorPos, new ImVec2(ImGui.getContentRegionAvailX() + 35.0f, 2.0f), separatorColor);
                ImGui.setItemAllowOverlap();

                ImGui.setCursorPos(startCursorPos.x + 28.0f, startCursorPos.y);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() - 3.0f, ImGui.getStyle().getFramePaddingY());
                boolean collapsingHeader = ImGui.collapsingHeader(layer.getPostEffectName());
                ImGui.setItemAllowOverlap();
                ImGui.popStyleVar();

                ImGui.popStyleVar();
                ImGui.popStyleColor(7);

                ImVec2 headerPos = ImGui.getCursorPos();
                if (!layer.isActive())
                    EditorImGui.popDisabled();
                //</editor-fold>

                //<editor-fold desc="Is Active checkbox">
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2.5f, 2.5f);
                ImGui.setCursorPos(startCursorPos.x + 3.0f, startCursorPos.y + 2.5f);
                if (ImGui.checkbox("##isActive", layer.isActive()))
                    layer.setActive(!layer.isActive());
                ImGui.popStyleVar();
                //</editor-fold>

                //<editor-fold desc="Dropdown menu">
                ImGui.sameLine();
                ImGui.setCursorPos(ImGui.getWindowWidth() - (16.0f * 2.0f) - ImGui.getStyle().getWindowPaddingX(), ImGui.getCursorPosY() + (16.0f / 2.0f) - 8.0f);

                if (EditorImGui.BeginButtonDropDownImage(
                        Loader.get().loadTexture("engineFiles/images/utils/icon=ellipsis-solid(32x32).png").getTextureID(),
                        "PostProcessLayerMenu", new ImVec2(18, 18), layer.isActive() ? ImGui.getStyle().getColor(ImGuiCol.Text) : ImGui.getStyle().getColor(ImGuiCol.TextDisabled), true)) {

                    if (ImGui.menuItem("Reset"))
                        layer.reset();

                    ImGui.separator();
                    if (ImGui.menuItem("Remove Layer"))
                        PostProcessing.removeLayer(layer);

                    if (i > 0) {
                        if (ImGui.menuItem("Move Up"))
                            PostProcessing.swapTwoLayers(i, i - 1);
                    } else
                        ImGui.textDisabled("Move Up");

                    if (i < layersCount - 1) {
                        if (ImGui.menuItem("Move Down"))
                            PostProcessing.swapTwoLayers(i, i + 1);
                    } else
                        ImGui.textDisabled("Move Down");

                    EditorImGui.EndButtonDropDown();
                }
                //</editor-fold>

                ImGui.setCursorPos(headerPos.x, headerPos.y);

                if (!layer.isActive())
                    EditorImGui.pushDisabled();

                if (collapsingHeader) {
                    layer.imgui(layer.isActive(), "");
                    if (i < layersCount - 1)
                        ImGui.separator();
                }

                if (!layer.isActive())
                    EditorImGui.popDisabled();

                ImGui.popID();
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        }
    }
}
