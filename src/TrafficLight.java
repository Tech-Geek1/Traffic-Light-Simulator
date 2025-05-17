import java.awt.Color;

public class TrafficLight {
    private String color;  // Red, Yellow, Green (the base color)
    private boolean isOn;  // Indicates if the light is on (bright) or off (dim)
    private int x, y;      // Position of the traffic light
    private int direction; // Direction of the traffic light (0: north-south, 1: east-west)

    // Constructor with default dimmed light
    public TrafficLight(int x, int y, String color, int direction) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = direction;
        this.isOn = false;  // Start with the light off (dim)
    }

    public int getDirection() {
        return direction;
    }
    // Getter method for the color
    public String getColor() {
        return color;
    }

    // Toggle the light to on (bright) or off (dim)
    public void setOn(boolean on) {
        this.isOn = on;
    }

    // Get the current color (returns a bright or dim version of the color)
    public Color getDisplayColor() {
        switch (color) {
            case "red":
                return isOn ? Color.RED : new Color(139, 0, 0);  // Bright red or dim red
            case "yellow":
                return isOn ? Color.YELLOW : new Color(139, 139, 0);  // Bright yellow or dim yellow
            case "green":
                return isOn ? Color.GREEN : new Color(0, 100, 0);  // Bright green or dim green
            default:
                return Color.GRAY;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
