package engine.toolbox;

public class Shortcut {
    public String shortcutName = "Key";
    public String shortcutDisplayKeys = "firstKey + secondKey";
    public int firstKeyCode = 0;
    public int secondKeyCode = 0;

    public Shortcut(String shortcutName, int firstKeyCode, String shortcutDisplayKeys) {
        init(shortcutName, firstKeyCode, firstKeyCode, shortcutDisplayKeys);
    }

    public Shortcut(String shortcutName, int firstKeyCode, int secondKeyCode, String shortcutDisplayKeys) {
        init(shortcutName, firstKeyCode, secondKeyCode, shortcutDisplayKeys);
    }

    private void init(String shortcutName, int firstKeyCode, int secondKeyCode, String shortcutDisplayKeys) {
        this.shortcutName = shortcutName;
        this.firstKeyCode = firstKeyCode;
        this.secondKeyCode = secondKeyCode;
        this.shortcutDisplayKeys = shortcutDisplayKeys;
    }
}
