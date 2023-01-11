package engine.imGui;

import engine.entities.GameObject;
import engine.eventSystem.EventSystem;
import engine.eventSystem.Events.Event;
import engine.eventSystem.Events.EventType;
import engine.eventSystem.Observer;
import engine.renderEngine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiWindow;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.*;

public class Console extends EditorImGuiWindow implements Observer{

    private String filterText = "";

    public Console() {
        addToEventSystem();
        this.filterOptions[0] = "All";
        for (int i = 0; i < ConsoleMessage.MessageType.values().length; i++)
            this.filterOptions[i + 1] = ConsoleMessage.MessageType.values()[i].name();
    }

    private ImInt targetFilterOption = new ImInt(0);
    private String[] filterOptions = new String[ConsoleMessage.MessageType.values().length + 1];

    private boolean clearOnPlay = true;
    private boolean pauseOnError = true;

    private List<ConsoleMessage> messages = new ArrayList<>();

    @Override
    public void imgui() {
        ImGui.begin(" \uEDE6 Console ", ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() + 2.0f, ImGui.getStyle().getFramePaddingY() - 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 2.0f, 2.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);
        ImGui.setCursorPosX(ImGui.getCursorPosX() - 10f);

        if (ImGui.button("\uEE09 Clear"))
            messages = new ArrayList<>();

        if (ImGui.button("Send test messages")) {
            log("Test error", ConsoleMessage.MessageType.Error);
            log("Test error", ConsoleMessage.MessageType.Error);
            log("Test warning", ConsoleMessage.MessageType.Warning);
            log("Test warning", ConsoleMessage.MessageType.Warning);
            log("Test info", ConsoleMessage.MessageType.Info);
            log("Test info", ConsoleMessage.MessageType.Info);
            log("Test simple", ConsoleMessage.MessageType.Simple);
            log("Test simple", ConsoleMessage.MessageType.Simple);
            log("Test custom", ConsoleMessage.MessageType.Custom, new ImVec4(255, 0, 255, 255));
            log("Test custom", ConsoleMessage.MessageType.Custom, new ImVec4(255, 0, 255, 255));
        }
        clearOnPlay = EditorImGui.toggledButton("\uEE09 Clear On Play", clearOnPlay);
        pauseOnError = EditorImGui.toggledButton("\uEC72 Pause On Error", pauseOnError);
        drawFilterOptions();
        filterText = drawSearchInput(filterText);

        ImGui.popStyleVar(3);
        ImGui.endMenuBar();

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() + ImGui.getStyle().getWindowPaddingX());
        for (int i = 0; i < messages.size(); i++) {
            if (!filterText.equals("") && !messages.get(i).message.toString().toLowerCase().contains(filterText.toLowerCase()))
                continue;

            boolean isEven = i % 2 == 0;

            switch (filterOptions[targetFilterOption.get()]) {
                case "All":
                    drawMessage(messages.get(i), isEven);
                    break;
                case "Error":
                    if (messages.get(i).messageType == ConsoleMessage.MessageType.Error)
                        drawMessage(messages.get(i), isEven);
                    break;
                case "Warning":
                    if (messages.get(i).messageType == ConsoleMessage.MessageType.Warning)
                        drawMessage(messages.get(i), isEven);
                    break;
                case "Info":
                    if (messages.get(i).messageType == ConsoleMessage.MessageType.Info)
                        drawMessage(messages.get(i), isEven);
                    break;
                case "Simple":
                    if (messages.get(i).messageType == ConsoleMessage.MessageType.Simple)
                        drawMessage(messages.get(i), isEven);
                    break;
                case "Custom":
                    if (messages.get(i).messageType == ConsoleMessage.MessageType.Custom)
                        drawMessage(messages.get(i), isEven);
                    break;
            }
        }
        super.imgui();
        ImGui.end();
    }

    private void drawMessage(ConsoleMessage consoleMessage, boolean isEven) {
        ImGui.beginGroup();

        float startX = ImGui.getCursorPosX();

        if (isEven) {
            ImGui.pushStyleColor(ImGuiCol.Header, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 15 / 255.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 20 / 255.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 15 / 255.0f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Header, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 28 / 255.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 32 / 255.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 28 / 255.0f);
        }
        float selectableHeight = consoleMessage.message.toString().split("\n").length * 19.0f;
        ImGui.selectable("##" + consoleMessage.hashCode(), true, 0, ImGui.getContentRegionAvailX() - ImGui.getScrollX(), selectableHeight);
        ImGui.popStyleColor(3);
        ImGui.setItemAllowOverlap();
        ImGui.sameLine();
        ImGui.setCursorPosX(startX);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 50.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 120 / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 160 / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, 140 / 255.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
        ImGui.setCursorPos(ImGui.getCursorPosX() + 1.5f, ImGui.getCursorPosY() + 1.5f);
        if (ImGui.button("##deleteMessage" + consoleMessage.hashCode(), 16.0f, 16.0f))
            messages.remove(consoleMessage);
        ImGui.popStyleColor(4);
        ImGui.popStyleVar();

        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() - 18.1f, ImGui.getCursorPosY() - 5.6f);
        ImGui.text("\uEEE4");

        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + 2.0f, ImGui.getCursorPosY() - 0.5f);

        ImGui.textColored(consoleMessage.messageColor.x / 255.0f, consoleMessage.messageColor.y / 255.0f, consoleMessage.messageColor.z / 255.0f, consoleMessage.messageColor.w / 255.0f, "[" + consoleMessage.messageType + "] " + consoleMessage.message.toString());

        ImGui.endGroup();
    }

    private boolean drawFilterOptions() {
        ImGui.pushID("consoleFilter");

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

        ImGui.setNextItemWidth(90f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX(), 10.0f);
        boolean a = (ImGui.combo("##consoleFilter", targetFilterOption, filterOptions, filterOptions.length));
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);

        ImGui.popID();
        return a;
    }

    private String drawSearchInput(String text) {
        ImGui.pushID("search");

        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        float start = ImGui.getCursorPosX();
        float scrollbarSize = ImGui.getStyle().getScrollbarSize(); // TODO добавить проверку если есть скроллбар то это значение иначе 0

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() + (ImGui.getStyle().getWindowPaddingX() - 3) + scrollbarSize);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 23.0f, ImGui.getStyle().getFramePaddingY() - 1.0f);
        ImString outString = new ImString(text, 256);
        ImVec2 startCursorPos = new ImVec2(ImGui.getCursorPosX() + 4.0f, ImGui.getCursorPosY() - 1.5f);
        if (ImGui.inputText("##search", outString)) {

            ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
            ImGui.text("\uEC82 ");

            ImGui.popStyleVar();
            ImGui.popID();
            return outString.get();
        }
        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
        ImGui.text("\uEC82 ");

        if (text.equals("")) {
            ImGui.setCursorPos(start + 23.0f, ImGui.getCursorPosY() - 1.0f);
            ImGui.textDisabled("Search");
        }

        ImGui.popStyleVar();
        ImGui.popID();

        return text;
    }

    private void send(Object message, ConsoleMessage.MessageType type, ImVec4 color) {
        if (type != ConsoleMessage.MessageType.Custom)
            messages.add(new ConsoleMessage(message, type));
        else
            messages.add(new ConsoleMessage(message, type, color));

        if (Window.get().getImGuiLayer().getGameViewWindow().isPlaying() && type == ConsoleMessage.MessageType.Error && pauseOnError) {
            Window.get().getImGuiLayer().showModalPopup("Pause On Error", ConsoleMessage.MessageType.Error);
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
    }

    public static Map<ConsoleMessage.MessageType, Integer> getMessagesCount() {
        Map<ConsoleMessage.MessageType, Integer> messagesCountMap = new HashMap<>();

        for (ConsoleMessage.MessageType type : ConsoleMessage.MessageType.values()) {
            int messagesCount = 0;
            for (ConsoleMessage consoleMessage : Window.get().getImGuiLayer().getConsole().messages)
                if (type == consoleMessage.messageType)
                    messagesCount++;

            messagesCountMap.put(type, messagesCount);
        }

        return messagesCountMap;
    }

    public static ConsoleMessage getLastMessage() {
        if (Window.get().getImGuiLayer().getConsole().messages.size() > 0)
            return Window.get().getImGuiLayer().getConsole().messages.get(Window.get().getImGuiLayer().getConsole().messages.size() - 1);
        return new ConsoleMessage("", ConsoleMessage.MessageType.Simple);
    }

    public static void log(Object message) { Window.get().getImGuiLayer().getConsole().send(message, ConsoleMessage.MessageType.Info, new ImVec4()); }

    public static void log(Object message, ConsoleMessage.MessageType type) { Window.get().getImGuiLayer().getConsole().send(message, type, new ImVec4()); }

    public static void log(Object message, ConsoleMessage.MessageType type, ImVec4 color) { Window.get().getImGuiLayer().getConsole().send(message, type, color); }

    @Override
    public void addToEventSystem() { EventSystem.addObserver(this); }

    @Override
    public void onNotify(GameObject object, Event event) {
        if (event.type == EventType.GameEngineStartPlay)
            if (clearOnPlay)
                messages = new ArrayList<>();
    }
}
