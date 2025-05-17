import java.util.ArrayList;

public class Scene {
    public final int width = 10;
    public final int height = 10;
    public String path;
    public int[][] board = new int[width][height];
    public ArrayList<Car> cars = new ArrayList<Car>();

    public static ArrayList<TrafficLight> trafficLights = new ArrayList<TrafficLight>();
}
