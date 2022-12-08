package engine.eventSystem;


import engine.entities.GameObject;
import engine.eventSystem.Events.Event;

public interface Observer {

    /** Fill this method with:
     * {
     *      EventSystem.addObserver(this)
     * }
     * and add this method to constructor */
    void addToEventSystem();

    void onNotify(GameObject object, Event event);
}
