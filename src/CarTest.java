import static org.junit.Assert.*;
import org.junit.Test;

public class CarTest {

    @Test
    public void testCarInitialization() {
        int x = 100;
        int y = 200;
        int direction = 0;
        int lane = 1;
        int id = 1;

        Car car = new Car(x, y, direction, lane, id);

        assertEquals(x, car.x);
        assertEquals(y, car.y);
        assertEquals(direction, car.direction);
        assertEquals(lane, car.lane);
        assertEquals(id, car.id);
        assertFalse(car.isTurningLeft);
        assertFalse(car.hasTurned);
    }
}
