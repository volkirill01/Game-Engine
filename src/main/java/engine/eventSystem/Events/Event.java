package engine.eventSystem.Events;

public class Event {
    public EventType type;

    public Event() { this.type = EventType.UserEvent; }

    public Event(EventType type) { this.type = type; }
}
