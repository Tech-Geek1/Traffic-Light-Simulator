import java.util.List;

public class TrafficLightController {
    private List<TrafficLight> lights;
    private int direction;  // 0 for north-south, 1 for east-west
    public int waitTime;   // Time to wait before switching lights
    public String currentColor;  // The current active color for this group
    public int redDuration;
    public int greenDuration;
    public int yellowDuration;


    public TrafficLightController(int direction, List<TrafficLight> lights,int redDuration, int greenDuration, int yellowDuration) {
        this.direction = direction;
        this.lights = lights;
        this.currentColor = "red";  // Start with red
        this.waitTime = 0;
        this.redDuration = redDuration;
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration*2;

        // only the red light is on initially
        for (TrafficLight light : lights) {
            if (light.getColor().equals("red")) {
                light.setOn(true);  // Red light is on
            } else {
                light.setOn(false); // Others are off
            }
        }
    }

    // public void tick() {
    //     waitTime++;
    //     if (currentColor.equals("red") && waitTime > (60 * 5)) {  // Red light for 5 seconds
    //         changeColor("green");
    //         waitTime = 0;
    //     } else if (currentColor.equals("green") && waitTime > (60 * 4)) {  // Green light for 4 seconds
    //         changeColor("yellow");
    //         waitTime = 0;
    //     } else if (currentColor.equals("yellow") && waitTime > (60 * 1)) {  // Yellow light for 1 second
    //         changeColor("red");
    //         waitTime = 0;
    //     }
    // }
    public void tick() {
    waitTime++;
    if (currentColor.equals("red") && waitTime > redDuration) {
        changeColor("green");
        waitTime = 0;
    } else if (currentColor.equals("green") && waitTime > greenDuration) {
        changeColor("yellow");
        waitTime = 0;
    } else if (currentColor.equals("yellow") && waitTime > yellowDuration) {
        changeColor("red");
        waitTime = 0;
    }
}


    // public void tick() {
    //     waitTime++;
    //     System.out.println("Tick for direction: " + direction + " currentColor: " + currentColor + " waitTime: " + waitTime);

    //     if (currentColor.equals("red") && waitTime > 20) {  // Lower the red light duration for testing
    //         changeColor("green");
    //         waitTime = 0;
    //     } else if (currentColor.equals("green") && waitTime > 10) {  // Green light for 10 ticks
    //         changeColor("yellow");
    //         waitTime = 0;
    //     } else if (currentColor.equals("yellow") && waitTime > 5) {  // Yellow light for 5 ticks
    //         changeColor("red");
    //         waitTime = 0;
    //     }
    // }

    // private void changeColor(String newColor) {
    //     currentColor = newColor;
    //     for (TrafficLight light : lights) {
    //         if (light.getColor().equals(newColor)) {
    //             light.setOn(true);  // Set the new color light to "on" (bright)
    //         } else {
    //             light.setOn(false);  // Set all other lights to "off" (dim)
    //         }
    //     }
    // }
    public void changeColor(String newColor) {
        System.out.println("Changing color for direction: " + direction + " to: " + newColor);
        currentColor = newColor;
        for (TrafficLight light : lights) {
            if (light.getColor().equals(newColor)) {
                light.setOn(true);  // Set the new color light to "on" (bright)
            } else {
                light.setOn(false);  // Set all other lights to "off" (dim)
            }
        }
    }
    public String getCurrentColor() {
    return currentColor;
}


    // Add getter to retrieve the current wait time for this group
    public int getWaitTime() {
        return waitTime;
    }
}
