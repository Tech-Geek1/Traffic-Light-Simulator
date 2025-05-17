import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public class Utils {

    public static ArrayList<int[]> generateSpawns(int[][] board, int width, int height, int DisplayWidth, int DisplayHeight)
    {
        ArrayList<int[]> coordinateList = new ArrayList<int[]>();
        System.out.println("w: " + width + " h: " + height);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (board[x][y] != 0)
                {
                    int exits = 0;
                    // Check dir
                    exits = checkdir(board, exits, x, y, width, height);

                    if (exits == 1) {
                        int scaledx = (int)((DisplayWidth*(x + 0.5))/width);
                        int scaledy = (int)((DisplayHeight*(y + 0.5))/height);
                        int[] arr = {scaledx, scaledy};
                        coordinateList.add(arr);
                        System.out.println("x: " + scaledx + " y: " + scaledy);
                    }
                }
            }  
        }
        return coordinateList;
    }

    public static int checkdir(int[][] board, int exits, int x, int y, int width, int height)
    {
        // Check north
        if (y-1 >= 0 && y-1 < height)
        {
            if (board[x][y-1] != 0)
            {
                exits++;
            }
        }

        if (y+1 >= 0 && y+1 < height)
        {
            if (board[x][y+1] != 0)
            {
                exits++;
            }
        }

        // Check east
        if (x+1 >= 0 && x+1 < width)
        {
            if (board[x+1][y] != 0)
            {
                exits++;
            }
        }


        // Check west
        if (x-1 >= 0 && x-1 < width)
        {
            if (board[x-1][y] != 0)
            {
                exits++;
            }
        }

        return exits;

    }

    public static ArrayList<Integer> checkdirections(int[][] board, int x, int y, int width, int height)
    {
        ArrayList<Integer> direction = new ArrayList<Integer>();
        // Check north
        if (y-1 >= 0 && y-1 < height)
        {
            if (board[x][y-1] != 0)
            {
                direction.add(0);
            }
        }

        // Check east
        if (x+1 >= 0 && x+1 < width)
        {
            if (board[x+1][y] != 0)
            {
                direction.add(1);
            }
        }

        if (y+1 >= 0 && y+1 < height)
        {
            if (board[x][y+1] != 0)
            {
                direction.add(2);
            }
        }


        // Check west
        if (x-1 >= 0 && x-1 < width)
        {
            if (board[x-1][y] != 0)
            {
                direction.add(3);
            }
        }

        return direction;

    }
    
    public static void drawMap(Graphics2D g2d, int[][] board)
    {
        for (int j = 0; j < board[0].length; j++) {
            for (int i = 0; i < board.length; i++) {
                if (board[i][j] > 1) 
                {
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval((int)((Engine.WIDTH*(i+0.5))/board.length) - 45, (int)((Engine.HEIGHT*(j+0.5))/board[0].length) - 45, 90, 90);  
                } else if (board[i][j] == 1)
                {
                    g2d.setColor(Color.PINK);
                    g2d.fillOval((int)((Engine.WIDTH*(i+0.5))/board.length) - 45, (int)((Engine.HEIGHT*(j+0.5))/board[0].length) - 45, 90, 90);
                }
            }
        }   
    }
    public static void drawGrid(Graphics2D g2d, int[][] board)
    {
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < board.length; i++) {
            g2d.drawLine((int)((Engine.WIDTH*(i))/board.length), 0, (int)((Engine.WIDTH*(i))/board.length), Engine.HEIGHT);
        }
        for (int i = 0; i < board[0].length; i++) {
            g2d.drawLine(0, (int)((Engine.HEIGHT*(i))/board[0].length), Engine.WIDTH, (int)((Engine.HEIGHT*(i))/board[0].length));
        }
    }

    public static void drawSpawns(Graphics2D g2d, int[][] board, ArrayList<int[]> spawnList)
    {
        g2d.setColor(Color.GREEN);
        for (int[] spawn : spawnList) {
            g2d.fillOval(spawn[0]-30/2, spawn[1]-30/2, 30, 30);
        }
    }

    public static void drawMapGraphics(Graphics2D g2d, int[][] board)
    {
        int height = board[0].length;
        int width = board.length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String path = "";
                if (board[x][y] == 0)
                {
                    path += "0000";
                } else {
                    // Check north
                    if (y-1 >= 0 && y-1 < height)
                    {
                        if (board[x][y-1] != 0)
                        {
                            path += 1;
                        } else {
                            path += 0;
                        }
                    } else {
                        path += 0;
                    }

                    // Check east
                    if (x+1 >= 0 && x+1 < width)
                    {
                        if (board[x+1][y] != 0)
                        {
                            path += 1;
                        } else {
                            path += 0;
                        }
                    } else {
                        path += 0;
                    }

                    if (y+1 >= 0 && y+1 < height)
                    {
                        if (board[x][y+1] != 0)
                        {
                            path += 1;
                        } else {
                            path += 0;
                        }
                    } else {
                        path += 0;
                    }

                    // Check west
                    if (x-1 >= 0 && x-1 < width)
                    {
                        if (board[x-1][y] != 0)
                        {
                            path += 1;
                        } else {
                            path += 0;
                        }
                    } else {
                        path += 0;
                    }
                }
                try {
                    BufferedImage img = ImageIO.read(new File("resources/"+path+".png"));
                    g2d.drawImage(img, (x*Engine.WIDTH/width), (y*Engine.HEIGHT/height), Engine.WIDTH/width, Engine.HEIGHT/height, null);
                } catch (Exception e)
                {

                }
                
            }
        }
        
    }

    public static void renderCars(Graphics2D g2d, ArrayList<Car> cars)
    {

        // for (Car car : cars) {
        //     car.render(g2d);
        // }

    }

    public static int[] GtW(int[] coordinates, int[][] board)
    {
        int x = coordinates[0];
        int y = coordinates[1];

        int height = board[0].length;
        int width = board.length;

        int[] result = {(x*Engine.WIDTH)/width, (y*Engine.HEIGHT)/height};
        return result;
    }

    public static void drawMapImage(Graphics2D g2d, BufferedImage mapImage) {
        g2d.drawImage(mapImage, 0, 0, null);
    }

    /**
     * Determines where traffic lights are needed, what kind are needed and where they are drawn on the display
     * @param board integer array of the board data
     * @param width width of the board
     * @param height height of the board
     * @param DisplayWidth width of the display
     * @param DisplayHeight height of the display
     * @return Returns an array list of needed traffic lights based on the board
     */
// public static ArrayList<TrafficLight> generateTrafficLights(int[][] board, int width, int height, int displayWidth, int displayHeight) {
//     ArrayList<TrafficLight> trafficLights = new ArrayList<>();

//     for (int y = 0; y < height; y++) {
//         for (int x = 0; x < width; x++) {
//             if (board[x][y] != 0) {
//                 // Determine connections
//                 String path = "";
//                 path += (y > 0 && board[x][y - 1] != 0) ? "1" : "0"; // North
//                 path += (x < width - 1 && board[x + 1][y] != 0) ? "1" : "0"; // East
//                 path += (y < height - 1 && board[x][y + 1] != 0) ? "1" : "0"; // South
//                 path += (x > 0 && board[x - 1][y] != 0) ? "1" : "0"; // West


//                 // Identify intersection type
//                 if (isIntersection(path)) {
//                     // Calculate tile size
//                     int tileWidth = displayWidth / width;
//                     int tileHeight = displayHeight / height;

//                     // Tile's top-left corner on the display
//                     int tileDisplayX = x * tileWidth;
//                     int tileDisplayY = y * tileHeight;

//                     // double inc = (IntersectionSize(path) == 3) ? 0.5 : 1;
//                     double inc = ((IntersectionSize(path)/2)*Math.random())+1;


//                     // For each connected direction, create a traffic light
//                     if (path.charAt(0) == '1') { // North
//                         TrafficLight tl = createTrafficLight(
//                             tileDisplayX, tileDisplayY, tileWidth, tileHeight,
//                             "NORTH", /* rotation */ 180, inc
//                         );
//                         trafficLights.add(tl);
//                     }
//                     if (path.charAt(1) == '1') { // East
//                         TrafficLight tl = createTrafficLight(
//                             tileDisplayX, tileDisplayY, tileWidth, tileHeight,
//                             "WEST", /* rotation */ 90, inc
//                         );
//                         trafficLights.add(tl);
//                     }
//                     if (path.charAt(2) == '1') { // South
//                         TrafficLight tl = createTrafficLight(
//                             tileDisplayX, tileDisplayY, tileWidth, tileHeight,
//                             "SOUTH", /* rotation */ 0, inc
//                         );
//                         trafficLights.add(tl);
//                     }
//                     if (path.charAt(3) == '1') { // West
//                         TrafficLight tl = createTrafficLight(
//                             tileDisplayX, tileDisplayY, tileWidth, tileHeight,
//                             "EAST", /* rotation */ -90, inc
//                         );
//                         trafficLights.add(tl);
//                     }
//                 }
//             }
//         }
//     }

//     return trafficLights;
// }

public static void spawnCar(int[][] board, int x, int y, int width, int height, ArrayList<Car> cars, int id)
    {
        // int b_x = (x*width)/Engine.WIDTH;
        // int b_y = (y*height)/Engine.HEIGHT;
        // // Check posible directions 
        // ArrayList<Integer> dirs = checkdirections(board, b_x, b_y, width, height);        
        // if (dirs.size() > 0)
        // {
        //     int dir = dirs.get((int) (Math.random()*dirs.size()));
        //     switch (dir) {
        //         case 0:
        //             cars.add(new Car(x, y, dir, id, 40, 40));
        //             break;
        //         case 1:
        //             cars.add(new Car(x, y, dir, id, 40, 40));
        //             break;
        //         case 2:
        //             cars.add(new Car(x, y, dir, id, 40, 40));
        //             break;
        //         case 3:
        //             cars.add(new Car(x, y, dir, id, 40, 40)); 
        //             break;
        //     }
        // }

    }

    public static boolean isParallel(int dir1, int dir2)
    {
        if (dir1%2 == dir2%2) 
        {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isIntersection(String path) {
        int connections = 0;
        for (char c : path.toCharArray()) {
            if (c == '1') connections++;
        }
        return connections >= 3; 
    }

    private static int IntersectionSize(String path) {
        int connections = 0;
        for (char c : path.toCharArray()) {
            if (c == '1') connections++;
        }
        return connections; 
    }





    // public static void renderTrafficLights(Graphics2D g2d, ArrayList<TrafficLight> trafficLights) {
    //     for (TrafficLight tl : trafficLights) {
        
    //         AffineTransform oldTransform = g2d.getTransform();

    //         // Apply rotation
    //         g2d.rotate(Math.toRadians(tl.getRotation()), tl.getX() + tl.getTWidth() / 2.0, tl.getY() + tl.getTHeight() / 2.0);

    //         // Draw the traffic light
    //         g2d.setColor(Color.DARK_GRAY);
    //         g2d.fillRect(tl.getX(), tl.getY(), tl.getTWidth(), tl.getTHeight());

    //         // Draw the lights inside
    //         int circleDiameter = tl.getTWidth() - 4;
    //         int spacing = 4;
    //         int startY = tl.getY() + spacing;

    //         String[] lights = {"R", "Y", "G"};
    //         for (int i = 0; i < 3; i++) {
    //             g2d.setColor(tl.getColor().equals(lights[i]) ? getTheColor(lights[i]) : getTheColor(lights[i]).darker().darker());
    //             g2d.fillOval(tl.getX() + 2, startY + i * (circleDiameter + spacing), circleDiameter, circleDiameter);
    //         }

    //         // Restore the original transform
    //         g2d.setTransform(oldTransform);

    //     }

    // }

    private static Color getTheColor(String light) {
            switch (light) {
                case "R": return Color.RED;
                case "Y": return Color.YELLOW;
                case "G": return Color.GREEN;
                default: return Color.BLACK;
            }
        }

    // public static boolean canDrive(Car car, ArrayList<Car> cars) {
    //     // Check grid in front for other cars

    //     // Check current grid for traffic lights
        
    //     int width = Engine.board.length;
    //     int height = Engine.board[0].length;

    //     int gridX = (car.x*width)/Engine.WIDTH;
    //     int gridY = (car.y*height)/Engine.HEIGHT;

    //     for (Car car2 : cars) {
    //         switch (car.direction) {
    //             case 0:
    //                 if (car2.y >= car.y) continue;
    //                 break;
    //             case 1:
    //                 if (car2.x <= car.x) continue;
    //                 break;
    //             case 2:
    //                 if (car2.y <= car.y) continue;
    //                 break;
    //             case 3:
    //                 if (car2.x >= car.x) continue;
    //                 break;
    //         }
    //         if (car.direction == car2.direction && euclidD(car.x, car.y, car2.x, car2.y) < 50) {
    //             return false;
    //         }
            
    //     }



    //     ArrayList<TrafficLight> trafficlights = Engine.trafficlights;

    //     for (TrafficLight tl : trafficlights) {
    //         int normal = (tl.getDirection()%2 == 0) ? ((tl.getDirection() == 0) ? 2 : 0) : ((tl.getDirection() == 1) ? 3 : 1);
    //         if (car.direction != normal) continue;

    //         int gridtlX = (tl.getX()*width)/Engine.WIDTH;
    //         int gridtlY = (tl.getY()*height)/Engine.HEIGHT;

    //         double dist = euclidD((car.x*width)/Engine.WIDTH, (car.y*height)/Engine.HEIGHT, (tl.getX()*width)/Engine.WIDTH, (tl.getY()*height)/Engine.HEIGHT); // Distance into the intersection
    //         if (gridX == gridtlX && gridY == gridtlY)
    //         {
    //             switch (tl.getColor())
    //             {
    //                 case "G":
    //                     return true;

    //                 case "Y":
    //                     return true;

    //                 case "Yt":
    //                     return true;

    //                 case "R":
    //                 if (car.isTurning) return true;
    //                     if (dist > 0.2)
    //                     {
    //                         return true;
    //                     } else {
                            
    //                         return false;
    //                     }
    //                 // return (dist > 0.5) ? false : true;
    //             }
    //         }
    //     }
    //     // System.out.println("TEST");
    //     return true;
    // }

    public static double euclidD(double x1, double y1, double x2, double y2)
    {
        return Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
    }

    // public static boolean inTrafficLights(Car car) {
    //     int width = Engine.board.length;
    //     int height = Engine.board[0].length;

    //     int gridX = (car.x*width)/Engine.WIDTH;
    //     int gridY = (car.y*height)/Engine.HEIGHT;

    //     ArrayList<TrafficLight> trafficlights = Engine.trafficlights;

    //     for (TrafficLight tl : trafficlights) {
    //         int normal = (tl.getDirection()%2 == 0) ? ((tl.getDirection() == 0) ? 2 : 0) : ((tl.getDirection() == 1) ? 3 : 1);
    //         if (car.direction != normal) continue;

    //         int gridtlX = (tl.getX()*width)/Engine.WIDTH;
    //         int gridtlY = (tl.getY()*height)/Engine.HEIGHT;

    //         double dist = euclidD((car.x*width)/Engine.WIDTH, (car.y*height)/Engine.HEIGHT, gridtlX + 0.5, gridtlY + 0.5); // Distance into the intersection
    //         if (gridX == gridtlX && gridY == gridtlY)
    //         {
    //             if (dist < 0.5)
    //             {
    //                 return true;
    //             }
    //         }
    //     }
    //     return false;
    // }

    // public static double getAverageWait() {
    //     ArrayList<Car> cars = Engine.cars;
    //     double sum = 0;
    //     for (Car car : cars) {
    //         sum += car.average;
    //     }
    //     return (sum/cars.size());
    // }
    
}

