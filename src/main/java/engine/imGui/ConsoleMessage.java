package engine.imGui;

import imgui.ImVec4;

public class ConsoleMessage {
    public enum MessageType {
        Error,
        Warning,
        Info,
        Simple,
        Custom
    }

    public MessageType messageType;
    public Object message;
    public ImVec4 messageColor;

    public ConsoleMessage(Object message, MessageType messageType) {
        this.messageType = messageType;
        this.message = message;

        switch (messageType) {
            case Error -> this.messageColor = new ImVec4(236, 101, 63, 255);
            case Warning -> this.messageColor = new ImVec4(247, 166, 42, 255);
            case Info -> this.messageColor = new ImVec4(75, 161, 234, 255);
            case Simple -> this.messageColor = new ImVec4(255, 255, 255, 255);
            case Custom -> this.messageColor = new ImVec4();
        }
    }

    public ConsoleMessage(Object message, MessageType messageType, ImVec4 color) {
        this.messageType = messageType;
        this.message = message;

        if (messageType == MessageType.Custom)
            this.messageColor = color;
    }

    public static String getMessageIcon(MessageType messageType) {
        switch (messageType) {
            case Error -> { return "\uEF19"; }
            case Warning -> { return "\uEF1B"; }
            case Info -> { return "\uEF4E"; }
            case Simple -> { return "\uEF04"; }
            case Custom -> { return "\uEEF3"; }
            default -> { return "\uEF16"; }
        }
    }

    public static ImVec4 getMessageColor(MessageType messageType) {
        switch (messageType) {
            case Error -> { return new ImVec4(236, 101, 63, 255); }
            case Warning -> { return new ImVec4(247, 166, 42, 255); }
            case Info -> { return new ImVec4(75, 161, 234, 255); }
            case Simple -> { return new ImVec4(255, 255, 255, 255); }
            case Custom -> { return new ImVec4(247, 60, 125, 255); }
            default -> { return new ImVec4(); }
        }
    }
}
