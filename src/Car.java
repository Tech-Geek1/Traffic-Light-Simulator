import java.util.Queue;

public class Car {
    public int x;
    public int y;
    public int direction;

    public int id;

    public int waited = 0;
    public Queue<Integer> actions;

    public int speed = 15;
    public int maxSpeed;
    public int lane;
    public boolean isTurningLeft = false;
    public boolean hasTurned = false;

    public Car(int x, int y, int direction, int lane, int id) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.lane = lane;
        this.id = id;
        speed += (int) (Math.random() * 5); // Randomizing speed
        maxSpeed = speed;
    }

    public int getXCoordinate() {
        return x;
    }

    public void setXCoordinate(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getDirection() {
        return direction;
    }
    public int getLane() {
        return lane;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public void setLane(int lane) {
        this.lane = lane;
    }

    // Helper method to get the position based on the direction
    public int getPosition() {
        // If the car moves vertically (North/South), use 'y' as the position
        // If the car moves horizontally (East/West), use 'x' as the position
        return (direction == 0 || direction == 2) ? y : x;
    }
    
    // Determine if another car is in the same lane
    public boolean isInSameLane(Car otherCar) {
        // If the cars are moving north or south, they share the same lane if 'x' coordinates match
        // If the cars are moving east or west, they share the same lane if 'y' coordinates match
        return this.direction == otherCar.direction &&
               ((this.direction == 0 || this.direction == 2) ? this.x == otherCar.x : this.y == otherCar.y);
    }
}
