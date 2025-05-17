import static org.junit.Assert.*;
import org.junit.Test;

public class TrafficLightTest {

    @Test
    public void testTrafficLightInitialization() {
        int x = 50;
        int y = 75;
        String color = "red";
        int direction = 0;

        TrafficLight light = new TrafficLight(x, y, color, direction);

        assertEquals(x, light.getX());
        assertEquals(y, light.getY());
        assertEquals(color, light.getColor());
        assertEquals(direction, light.getDirection());
    }
}
