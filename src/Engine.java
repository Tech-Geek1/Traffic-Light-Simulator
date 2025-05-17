import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Runs the main simulation
 */
public class Engine extends JFrame
{

    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String PINK = "\033[38;5;201m";
    public static final String RED = "\u001B[41m";

    public static Scene scene;
    public static String path;

    public static boolean APIStarted = false;
    public static int targetCars = 3;

    public static JPanel canvas;
    public static BufferedImage bufferedImage;
    public static Graphics2D g2d;

    // public static int width = 1080;
    // public static int height = 1080;

    public static int id = 0;
    public static int iterations = 0;

    public static boolean paused = false;

    public static int maxWait = 0;
    public static int minWait = 1000;

    public static int aveWait = 0;

    public static Grapher grapher;
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static int width = (int) screenSize.getWidth();
    public static int height = (int) screenSize.getHeight();
    public static int dashedVerticalX = 0;
    public static int dashedRightVerticalX = 0;
    public static int dashedHorizontalY = 0;
    public static int dashedBottomHorizontalY = 0;
    public static int verticalRoadX = 0;
    public static int newVerticalRoadWidth = 0;
    public static int horizontalRoadY = 0;
    public static int newHorizontalRoadHeight = 0;
    public static int horizontalLineHeight = 0;
    public static int horizontalLineY = 0;

    public static int verticalLineWidth = 0;
    public static int verticalLineX = 0;
    public static TrafficLightController northSouthController;
    public static TrafficLightController eastWestController;
    public static TrafficLightController southNorthController;
    public static TrafficLightController westEastController;
    public static int horizontalLightWidth = 90;
    public static int horizontalLightHeight = 30;
    public static int verticalLightWidth = 30;
    public static int verticalLightHeight = 90;

    public static int northboundX, northboundY;
    public static int eastboundX, eastboundY;
    public static int southboundX, southboundY;
    public static int westboundX, westboundY;

    public static int[][] heatmap = new int[50][50];
    public static int inc = 0;

    public static BufferedImage hmap;
    public static Graphics2D hmapg;

    public static double AVE = 0;
    public static double vals = 0;


    public static boolean heatmapon = true;
    public static int maxCars;
    public static int carSpawnRate;
    public static int trafficDensity;
    public static String roadType;
    public static String trafficPattern;
    public static UserInput userInput;


    public static void main(String[] args)
    {
        // int DisplayWidth = (int) screenSize.getWidth();
        // int DisplayHeight = (int) screenSize.getHeight();
        SwingUtilities.invokeLater(() -> {
            userInput = new UserInput();
            userInput.setVisible(true);
        });

        path = "4way.txt";
        try
        {
            path = args[0];
        } catch (Exception e)
        {
            log("[Exception] No path supplied, defaulting to: " + path, 1);
        }
        startSimulation(maxCars, carSpawnRate, trafficDensity, roadType, trafficPattern);
        
        // init();
        // new Engine();
        // Timer timer = new Timer((40*5)/12, e -> step()); // (40*5)/12 60fps
        // timer.start();
    }
    public static void startSimulation(int maxCars, int carSpawnRate, int trafficDensity, String selectedRoadType, String trafficPattern) {
        // Engine.maxCars = maxCars;
        // Engine.carSpawnRate = carSpawnRate;
        // Engine.trafficDensity = trafficDensity;
        // Engine.roadType = selectedRoadType;
        // Engine.trafficPattern = trafficPattern;

        init();
        new Engine();
        Timer timer = new Timer((40 * 5) / 12, e -> step()); // (40*5)/12 60fps
        timer.start();
        try (Scanner systemin = new Scanner(System.in))
        {
            while (true)
            {
                String command = systemin.nextLine();
                if (command.equals("exit"))
                {   
                    log("[Console] Exiting...", 0);
                    System.exit(0);
                }
                if (command.contains(".txt"))
                {
                    path = command;

                    load(scene);

                    //Utils.PrintSceneAscii(scene);
                    
                    // Needs better way of checking front end is running
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 


                    // API.UpdateBoard(scene);
                }
                if (command.contains("car"))
                {
                    // scene.cars.add(new Car(width, height/2-20 + width/16, 3, getID()));
                }
                if (command.contains("pause"))
                {
                    paused = true;
                }
                if (command.contains("resume"))
                {
                    paused = false;
                }
                if (command.contains("chart"))
                {
                    if (grapher.running == true)
                    {
                        grapher.running = false;
                        grapher.stop();
                    } else {
                        grapher.running = true;
                        grapher.go();
                    }
                    
                }
            }
        }
    }

    public static void step()
    {
        try {
            targetCars = Integer.parseInt(UserInput.maxCarsField.getText());
        } catch (Exception e) {
        }

        //draws the green background
        g2d.setColor(new Color(80, 200, 120));
        g2d.fillRect(0, 0, width, height);

        // Draws the road
        g2d.setColor(Color.black);

        // Vertical road (centered, independent of its width)
        newVerticalRoadWidth = width / 8;  // Set the width of the vertical road
        verticalRoadX = (width / 2) - (newVerticalRoadWidth / 2);  // Calculate the X position to keep it centered
        g2d.fillRect(verticalRoadX, 0, newVerticalRoadWidth, height);  // Draw the vertical road centered

        // Horizontal road (centered, independent of its height)
        newHorizontalRoadHeight = height / 6;  // Set the height of the horizontal road
        horizontalRoadY = (height / 2) - (newHorizontalRoadHeight / 2);  // Calculate the Y position to keep it centered
        g2d.fillRect(0, horizontalRoadY, width, newHorizontalRoadHeight);  // Draw horizontal road centered

        // Set the color to white for the lines
        g2d.setColor(Color.white);

        // Draw the solid white line across the center of the horizontal road with a gap at the intersection
        horizontalLineHeight = newHorizontalRoadHeight / 10;  // The height of the white line (a fraction of the horizontal road height)
        horizontalLineY = (height / 2) - (horizontalLineHeight / 2);  // Y position to center the white line on the horizontal road

        // Draw the first part of the white line (left side to intersection)
        g2d.fillRect(0, horizontalLineY, (width / 2) - (newVerticalRoadWidth / 2), horizontalLineHeight);  // Left part of the white line

        // Draw the second part of the white line (right side of the intersection)
        g2d.fillRect((width / 2) + (newVerticalRoadWidth / 2), horizontalLineY, (width / 2) - (newVerticalRoadWidth / 2), horizontalLineHeight);  // Right part of the white line

        // Now for the vertical white line, broken around the horizontal road

        verticalLineWidth = newVerticalRoadWidth / 14;  // The width of the white line (a fraction of the vertical road width)
        verticalLineX = (width / 2) - (verticalLineWidth / 2);  // X position to center the white line on the vertical road

        // Draw the first part of the vertical white line (top part, above the intersection)
        g2d.fillRect(verticalLineX, 0, verticalLineWidth, (height / 2) - (newHorizontalRoadHeight / 2));  // Top part of the white line

        // Draw the second part of the vertical white line (bottom part, below the intersection)
        g2d.fillRect(verticalLineX, (height / 2) + (newHorizontalRoadHeight / 2), verticalLineWidth, (height / 2) - (newHorizontalRoadHeight / 2));  // Bottom part of the white line

        // Set dashed line stroke
    float[] dashPattern = { 10, 10 }; // Length of dashes and spaces
    BasicStroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0);
    g2d.setStroke(dashed);
    g2d.setColor(Color.white);

    // === Dashed lines for the left vertical road ===
    dashedVerticalX = verticalRoadX + newVerticalRoadWidth / 4;  // Left vertical road
    g2d.drawLine(dashedVerticalX, 0, dashedVerticalX, (height / 2) - (newHorizontalRoadHeight / 2)); // Above the intersection
    g2d.drawLine(dashedVerticalX, (height / 2) + (newHorizontalRoadHeight / 2), dashedVerticalX, height); // Below the intersection

    // === Dashed lines for the right vertical road ===
    dashedRightVerticalX = verticalRoadX + (3 * newVerticalRoadWidth / 4);  // Right vertical road
    g2d.drawLine(dashedRightVerticalX, 0, dashedRightVerticalX, (height / 2) - (newHorizontalRoadHeight / 2)); // Above the intersection
    g2d.drawLine(dashedRightVerticalX, (height / 2) + (newHorizontalRoadHeight / 2), dashedRightVerticalX, height); // Below the intersection

    // === Dashed lines for the top horizontal road ===
    dashedHorizontalY = horizontalRoadY + newHorizontalRoadHeight / 4;  // Top horizontal road
    g2d.drawLine(0, dashedHorizontalY, (width / 2) - (newVerticalRoadWidth / 2), dashedHorizontalY); // Left of the intersection
    g2d.drawLine((width / 2) + (newVerticalRoadWidth / 2), dashedHorizontalY, width, dashedHorizontalY); // Right of the intersection

    // === Dashed lines for the bottom horizontal road ===
    dashedBottomHorizontalY = horizontalRoadY + (3 * newHorizontalRoadHeight / 4);  // Bottom horizontal road
    g2d.drawLine(0, dashedBottomHorizontalY, (width / 2) - (newVerticalRoadWidth / 2), dashedBottomHorizontalY); // Left of the intersection
    g2d.drawLine((width / 2) + (newVerticalRoadWidth / 2), dashedBottomHorizontalY, width, dashedBottomHorizontalY); // Right of the intersection



    // Reset stroke back to solid after drawing dashed lines
    g2d.setStroke(new BasicStroke());

    //draws the grey traffic lights
    // Define dimensions for horizontal and vertical lights
    g2d.setColor(Color.gray);
    g2d.fillRect(northboundX, northboundY, verticalLightWidth, verticalLightHeight);
    g2d.fillRect(eastboundX, eastboundY, horizontalLightWidth, horizontalLightHeight);
    g2d.fillRect(southboundX, southboundY, verticalLightWidth, verticalLightHeight);
    g2d.fillRect(westboundX, westboundY, horizontalLightWidth, horizontalLightHeight);

    northSouthController.tick();
    eastWestController.tick();
    // southNorthController.tick();
    // westEastController.tick();
    if (heatmapon) {
        if (inc%6 == 0) {
            int max = 0;
            int max_x = 0;
            int max_y = 0;
            for (int j = 0; j < heatmap.length; j++) {
                for (int i = 0; i < heatmap.length; i++) {
                    try {
                        double light = heatmap[i][j];
                        if (light > max) {
                            max = (int) light;
                            max_x = i;
                            max_y = j;
                        }
                        Color c = new Color((int) (255*(light/max)), 50, 100);
                        hmapg.setColor(c);
                        // hmapg.fillRect((int)((((double) i*width))/heatmap.length), (int)((((double) j*height))/heatmap.length), (int)((((double) width))/heatmap.length), (int)((((double) height))/heatmap.length));
                        hmapg.fillRect((int)(((double)(i*width))/heatmap.length), (int)(((double)(j*height))/heatmap.length), (int)(((double)(width))/heatmap.length), (int)(((double)(height))/heatmap.length));
                    } catch (Exception e) {

                    }
                    heatmap[i][j] = (int)(((double) heatmap[i][j])/1.05);
                }
            }
            // System.out.println(max_x + " " + max_y);
            if (inc %60 == 0) {
                if (Math.abs(25-max_x) > Math.abs(25-max_y)) {

                    // northSouthController.redDuration -= 0.1;
                    // northSouthController.greenDuration += 0.1;

                    // eastWestController.greenDuration -= 0.1;
                    // eastWestController.redDuration += 0.1;
                    
                    // eastWestController.changeColor("green");
                    // northSouthController.changeColor("red");

                    if (eastWestController.currentColor.equals("red")) {
                        eastWestController.changeColor("green");
                    }

                    if (northSouthController.currentColor.equals("green")) {
                        northSouthController.changeColor("yellow");
                    }

                    

                } else {
                    // northSouthController.redDuration += 0.1;
                    // northSouthController.greenDuration -= 0.1;

                    // eastWestController.greenDuration += 0.1;
                    // eastWestController.redDuration -= 0.1;

                    
                    if (northSouthController.currentColor.equals("red")) {
                        northSouthController.changeColor("green");
                    }

                    if (eastWestController.currentColor.equals("green")) {
                        eastWestController.changeColor("yellow");
                    }

                }
            }
        }
        inc++;
        g2d.drawImage(hmap, 0, 0, null);
        
    }
    
                    
                    
        int mouse_x=MouseInfo.getPointerInfo().getLocation().x-canvas.getLocationOnScreen().x;
        int mouse_y=MouseInfo.getPointerInfo().getLocation().y-canvas.getLocationOnScreen().y;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 20));
        String info = "";
                    
        if (!paused)
        {
            if (UserInput.trafficOptiSelector.getSelectedItem().equals("True")) {
                heatmapon = true;
            } else {
                heatmapon = false;
            }
            tick();
            if (heatmapon) {
                heat();
            }
        }

        renderTrafficLights(g2d);
        renderObjects(g2d);
        aveWait = 0;
        for (Car car : scene.cars) {
            if (Math.abs(car.x-mouse_x)<30 && Math.abs(car.y-mouse_y)<30)
            {
                info = "ID: " + car.id + " Dir: " + car.direction + " Speed: " + car.speed;
            }
            aveWait+=car.waited;
            AVE = (AVE*vals + car.waited)/(vals+1);
            vals++;
            }
            for (TrafficLight trafficLight : Scene.trafficLights)
            {
                // if (Math.abs(trafficLight.getX()-mouse_x)<30 && Math.abs(trafficLight.getY()-mouse_y)<30)
                //         {
                //             if (trafficLight.getDirection() == 0) {  // North-south lights
                //                 info = "Color: " + trafficLight.getColor() + " Time: " + northSouthController.getWaitTime();
                //             } else if (trafficLight.getDirection() == 1) {  // East-west lights
                //                 info = "Color: " + trafficLight.getColor() + " Time: " + eastWestController.getWaitTime();
                //             } else if(trafficLight.getDirection() == 2) { //south north lights
                //                 info = "Color: " + trafficLight.getColor() + " Time: " + southNorthController.getWaitTime();

                //             } else if(trafficLight.getDirection()==3) {
                //                 info = "Color: " + trafficLight.getColor() + " Time: " + westEastController.getWaitTime();

                //             }
                //         }
                if (Math.abs(trafficLight.getX() - mouse_x) < 30 && Math.abs(trafficLight.getY() - mouse_y) < 30) {
                    String currentColor = "";
                    int waitTime = 0;

                    // Determine the current color and wait time based on the traffic light's direction
                    switch (trafficLight.getDirection()) {
                        case 0:  // North-south lights
                            currentColor = northSouthController.getCurrentColor();
                            waitTime = northSouthController.getWaitTime();
                            break;
                        case 1:  // East-west lights
                            currentColor = eastWestController.getCurrentColor();
                            waitTime = eastWestController.getWaitTime();
                            break;
                        case 2:  // South-north lights
                            currentColor = southNorthController.getCurrentColor();
                            waitTime = southNorthController.getWaitTime();
                            break;
                        case 3:  // West-east lights
                            currentColor = westEastController.getCurrentColor();
                            waitTime = westEastController.getWaitTime();
                            break;
                    }

                    info = "Color: " + currentColor + " Time: " + waitTime;
}

                    }
                    
                    g2d.drawString(info, mouse_x-120, mouse_y-10);
                    aveWait = aveWait/scene.cars.size();
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Target N of Cars: " + (targetCars+1), 10, 20);
                    g2d.drawString("Number of Cars: " + scene.cars.size(), 10, 40);
                    g2d.drawString("Current ID: " + id, 10, 60);
                    g2d.drawString("Ave Wait time: " + aveWait, 10, 80);
                    if (maxWait < aveWait)
                    {
                        maxWait = aveWait;
                    }
                    if (minWait > aveWait)
                    {
                        minWait = aveWait;
                    }
                    g2d.drawString("Max Wait time: " + maxWait, 10, 100);
                    g2d.drawString("Min Wait time: " + minWait, 10, 120);
                    canvas.repaint();
        
                }

    public Engine() {
        setTitle("Traffic Simulator");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = bufferedImage.createGraphics();

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bufferedImage, 0, 0, null);
            }
        };

        add(canvas);

        setVisible(true);
    }

    public static void renderObjects(Graphics2D g2d)
    {
        // if (scene.cars.size() == 0)
        // {
        //     scene.cars.add(new Car(0, height/2-20 - width/16, 1, getID()));
        // }
        ArrayList<Car> remove = new ArrayList<Car>();
        for (Car car : scene.cars) {
            if (car.x > width || car.x < 0 - 80 || car.y > height || car.y < 0 - 80)
            {
                remove.add(car);
            } else {
                g2d.setColor(Color.white);
                switch (car.direction) {
                    case 0:
                        // System.out.println("0");
                        g2d.drawRect(car.x, car.y, 15, 40);
                        break;
                    case 1:
                        // System.out.println("1");
                        g2d.drawRect(car.x, car.y, 40, 15);
                        break;
                    case 2:
                        // System.out.println("0");
                        g2d.drawRect(car.x, car.y, 15, 40);
                        break;
                    case 3:
                        // System.out.println("3");
                        g2d.drawRect(car.x, car.y, 40, 15);
                        break;
                }
            }
        }
        for (Car car : remove) {
            scene.cars.remove(car);
        }
    }

    public static void renderTrafficLights(Graphics2D g2d) {
    for (TrafficLight trafficLight : Scene.trafficLights) {
        g2d.setColor(trafficLight.getDisplayColor());  // Use the display color (dim/bright)
        g2d.fillOval(trafficLight.getX(), trafficLight.getY(), 20, 20);
    }
}



    public static void tick()
    {
        // for (TrafficLight trafficLight : scene.trafficLights) {
        //     trafficLight.tick();
        // }
        while (scene.cars.size() <= targetCars)
        {
            int direction = (int) (Math.random()*4);
            int lane = (int) (Math.random() * 2);
            int x = 0;
            int y = 0;

            if (!UserInput.trafficPatternSelector.getSelectedItem().equals("Balanced")) {
                if (direction%2 == 0) {
                    direction = (int) (Math.random()*4);
                }
            }



    switch (direction) {
        case 0: // Southbound (car moves down the screen)
            // Adjust x position based on lane
        if (lane == 0) {
            x = dashedRightVerticalX - (width / 32);  // Left lane of the dashed line (closer to the dashed line)
            x += 20;
        } else if (lane == 1) {
            x = dashedRightVerticalX + (width / 32);  // Shift the right lane a bit to the left by subtracting 5 pixels
            x -= 25;
        }
        
        y = 0 - 80;  // Start off-screen above and move downward
        break;

        case 1: // Eastbound (car moves right across the screen)
            // Adjust y position based on lane
        if (lane == 0) {
            y = dashedHorizontalY - (height / 32);  // Lane 0 is above the dashed line
        } else if (lane == 1) {
            y = dashedHorizontalY + (height / 32);
            y -= 20;  // Lane 1 is below the dashed line but shifted a bit up by 5 pixels
        }
        
        x = 0 - 80;  // Start off-screen to the left and move rightward
        break;
        case 2: // Northbound (car moves up the screen)
            // Adjust x position based on lane
            if (lane == 0) {
                x = dashedVerticalX - (width / 32);  // Left lane of the dashed line (closer to the dashed line)
                x += 10;
            } else if (lane == 1) {
                x = dashedVerticalX + (width / 40);  // Right lane of the dashed line (closer to the dashed line)
                x -= 20;
            }
            
            y = height; 
                    break;

        case 3: // Westbound (car moves left across the screen)
        // Adjust y position based on lane
            if (lane == 0) {
                y = dashedBottomHorizontalY - (height / 32);  // Lane 0 is above the dashed line
                y += 5;
            } else if (lane == 1) {
                y = dashedBottomHorizontalY + (height / 32);  // Lane 1 is below the dashed line but shifted a bit up by 5 pixels
                y -= 20;
            }
            
            x = width;  // Start off-screen to the right and move leftward
            break;
    }
        Car newCar = new Car(x,y,direction,lane,getID());

        if (lane == 0) {  // Only cars in the left lane can turn left
                newCar.isTurningLeft = Math.random() < 0.2;  // 20% chance to turn left
            } else {
                newCar.isTurningLeft = false;  // Cars in the right lane do not turn left
            }

    // Add the new car to the scene
    scene.cars.add(newCar);

        // scene.cars.add(new Car(x, y, direction, lane, getID()));


    // Add the new car with the calculated x, y, direction, and lane
    // if (direction == 2) {  // Only adding for northbound for testing
    //     scene.cars.add(new Car(x, y, direction, lane, getID()));
    // }
    // if (direction == 0) {  // Only adding for southbound for testing
    //     scene.cars.add(new Car(x, y, direction, lane, getID()));
    // }
    // if (direction == 1) {  // Only adding for eastbound for testing
    //     scene.cars.add(new Car(x, y, direction, lane, getID()));
    // }
    // if (direction == 3) {  // Only adding for westbound for testing
    //     scene.cars.add(new Car(x, y, direction, lane, getID()));
    // }
        }
        for (Car car : scene.cars) {
            if (true)
            {
                if (canDrive(car))
                {
                    if (!collisionDetection(car)) {
                        switch (car.direction) {
                            case 0:
                                car.y+= car.speed;
                                break;
                            case 1:
                                car.x+= car.speed;
                                break;
                            case 2:
                                car.y-= car.speed;
                                break;
                            case 3:
                                car.x-= car.speed;
                                break;
                        }
                    }
                } else {
                    car.waited++;
                }
            }
        }
        if (iterations > (int) (Math.random()*320))
        {
            iterations = 0;
            targetCars = (int) (Math.random()*20);
        }
        iterations++;
    }

public static boolean collisionDetection(Car car) {
    boolean carAhead = false;
    int stoppingDistance = 50;  // Adjust as needed
    int minDistanceAhead = Integer.MAX_VALUE;

    for (Car otherCar : scene.cars) {
        if (car == otherCar) continue;  // Skip self
        if (car.direction != otherCar.direction || car.lane != otherCar.lane) continue;  // Only check cars in same lane and direction

        int distance = 0;
        switch (car.direction) {
            case 0:  // Southbound
                if (otherCar.y > car.y) {
                    distance = otherCar.y - car.y;
                    if (distance < stoppingDistance && distance < minDistanceAhead) {
                        minDistanceAhead = distance;
                        carAhead = true;
                    }
                }
                break;
            case 2:  // Northbound
                if (otherCar.y < car.y) {
                    distance = car.y - otherCar.y;
                    if (distance < stoppingDistance && distance < minDistanceAhead) {
                        minDistanceAhead = distance;
                        carAhead = true;
                    }
                }
                break;
            case 1:  // Eastbound
                if (otherCar.x > car.x) {
                    distance = otherCar.x - car.x;
                    if (distance < stoppingDistance && distance < minDistanceAhead) {
                        minDistanceAhead = distance;
                        carAhead = true;
                    }
                }
                break;
            case 3:  // Westbound
                if (otherCar.x < car.x) {
                    distance = car.x - otherCar.x;
                    if (distance < stoppingDistance && distance < minDistanceAhead) {
                        minDistanceAhead = distance;
                        carAhead = true;
                    }
                }
                break;
        }
    }
    return carAhead;
}

public static boolean canDrive(Car car) {
    int carLength = 40;  // Length of the car
    int stopBuffer = 20;  // Distance before the intersection where cars should stop

    // Intersection boundaries 
    int intersectionLeft = verticalRoadX;
    int intersectionRight = verticalRoadX + newVerticalRoadWidth;
    int intersectionTop = horizontalRoadY;
    int intersectionBottom = horizontalRoadY + newHorizontalRoadHeight;

    // Get the current color for the car's direction from the TrafficLightController
    String lightColor = "";
    switch (car.direction) {
        case 0:  // Southbound
        case 2:  // Northbound
            lightColor = northSouthController.getCurrentColor();
            break;
        case 1:  // Eastbound
        case 3:  // Westbound
            lightColor = eastWestController.getCurrentColor();
            break;
    }

    // Determine if the car is approaching the intersection and needs to stop
    switch (car.direction) {
        case 0:  // Southbound (moving down the screen)
            int stopLineY_South = intersectionTop - stopBuffer;
            int carFrontY_South = car.y + carLength;
            if (lightColor.equals("green") && car.getLane() == 1 && carFrontY_South- 70 > stopLineY_South){
                car.setDirection(1);
                car.setLane(1);
            }

            if (carFrontY_South < stopLineY_South) {
                // Before stop line
                return true;
            } else if (carFrontY_South >= stopLineY_South && carFrontY_South <= intersectionTop) {
                // At or near stop line
                return lightColor.equals("green");
            } else {
                // Passed intersection
                return true;
            }

        case 2:  // Northbound (moving up the screen)
            int stopLineY_North = intersectionBottom + stopBuffer;
            int carFrontY_North = car.y;
            if (lightColor.equals("green") && car.getLane() == 0 && carFrontY_North+ 50 < stopLineY_North){
                car.setDirection(3);
                car.setLane(1);
            }

            if (carFrontY_North > stopLineY_North) {
                // Before stop line
                return true;
            } else if (carFrontY_North <= stopLineY_North && carFrontY_North >= intersectionBottom) {
                // At or near stop line
                return lightColor.equals("green");
            } else {
                // Passed intersection
                return true;
            }

        case 1:  // Eastbound (moving right)
            int stopLineX_East = intersectionLeft - stopBuffer;
            int carFrontX_East = car.x + carLength;
            if (lightColor.equals("green") && car.getLane() == 0 && carFrontX_East- 70 > stopLineX_East){
                car.setDirection(2);
                car.setLane(1);
            }

            if (carFrontX_East < stopLineX_East) {
                // Before stop line
                return true;
            } else if (carFrontX_East >= stopLineX_East && carFrontX_East <= intersectionLeft) {
                // At or near stop line
                return lightColor.equals("green");
            } else {
                // Passed intersection
                return true;
            }

        case 3:  // Westbound (moving left)
            int stopLineX_West = intersectionRight + stopBuffer;
            int carFrontX_West = car.x;

            if (carFrontX_West > stopLineX_West) {
                // Before stop line
                return true;
            } else if (carFrontX_West <= stopLineX_West && carFrontX_West >= intersectionRight) {
                // At or near stop line
                return lightColor.equals("green");
            } else {
                // Passed intersection
                return true;
            }
    }
    return true;
}


    /**
     * Used to initialise all features before simulation
     */
    public static void init()
    {
        log("[Console] Starting initialisation...", 0);
        // start python front end with api
        // API.init();
        scene = new Scene();
        for (int j = 0; j < heatmap.length; j++) {
            for (int i = 0; i < heatmap.length; i++) {
                heatmap[i][j] = 0;
            }  
        }

        hmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        hmapg = hmap.createGraphics();

        load(scene);

        initializeTrafficLights();

        initializeTrafficLightControllers();

        //Utils.PrintSceneAscii(scene);
        
        // Needs better way of checking front end is running
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        

        grapher = new Grapher();
        // API.UpdateBoard(scene);

    }

    private static void initializeTrafficLights() {

        // Draw the top-left rectangle
        // Northbound road (placed on the left side of the south road, at the bottom)
        //adjust distance between lights by adding to northboundY
        //shift all the lights up or down by changing each color by a constant number
        northboundX = (width / 2) - width / 14 - verticalLightWidth / 2;
        northboundY = (height / 2) - height / 4 - verticalLightHeight / 2;
        northboundX += 2;
        northboundY += 410;

        Scene.trafficLights.add(new TrafficLight(northboundX + verticalLightWidth / 4, northboundY + 10, "red", 0));  // Green light
        Scene.trafficLights.add(new TrafficLight(northboundX + verticalLightWidth / 4, northboundY + 40, "yellow", 0)); // Yellow light
        Scene.trafficLights.add(new TrafficLight(northboundX + verticalLightWidth / 4, northboundY + 70, "green", 0));    // Red light
         

        // Eastbound road (left side of the west road)
        //adjust the distance between lights by adding to eastboundX
        //shift all the lights up or down by changing each color by a constant number
        eastboundX = (width / 2) - width / 12 - horizontalLightWidth / 2;
        eastboundY = (height / 2) - height / 10 - horizontalLightHeight / 2;
        eastboundX -= 10;
        Scene.trafficLights.add(new TrafficLight(eastboundX +5, eastboundY + horizontalLightHeight / 4, "green", 1)); // Green light
        Scene.trafficLights.add(new TrafficLight(eastboundX + 35, eastboundY + horizontalLightHeight / 4, "yellow", 1));// Yellow light
        Scene.trafficLights.add(new TrafficLight(eastboundX + 65, eastboundY + horizontalLightHeight / 4, "red", 1));   // Red light



        // // Southbound road (left side of the north road)
        //adjust the distance between lights by adding to southboundY
        //shift all the lights up or down by changing each color by a constant number
        southboundX = (width / 2) - width / 16 - verticalLightWidth / 2;
        southboundY = (height / 2) + height / 4 - verticalLightHeight / 2;
        southboundY -= 415;
        southboundX += 255;
        Scene.trafficLights.add(new TrafficLight(southboundX + verticalLightWidth / 4, southboundY +5, "green", 0));  // Green light
        Scene.trafficLights.add(new TrafficLight(southboundX + verticalLightWidth / 4, southboundY + 35, "yellow", 0)); // Yellow light
        Scene.trafficLights.add(new TrafficLight(southboundX + verticalLightWidth / 4, southboundY + 65, "red", 0));    // Red light


        // // Westbound road (left side of the east road)
        //adjust the distance between lights by adding to westboundX
        //shift all the lights up or down by changing each color by a constant number
        westboundX = (width / 2) + width / 12 - horizontalLightWidth / 2;
        westboundY = (height / 2) - height / 16 - horizontalLightHeight / 2;
        westboundX += 10;
        westboundY = westboundY + 175;
        Scene.trafficLights.add(new TrafficLight(westboundX + 10, westboundY + horizontalLightHeight / 4, "red", 1)); // Green light
        Scene.trafficLights.add(new TrafficLight(westboundX + 40, westboundY + horizontalLightHeight / 4, "yellow", 1));// Yellow light
        Scene.trafficLights.add(new TrafficLight(westboundX + 70, westboundY + horizontalLightHeight / 4, "green", 1));   // Red light


    }

    private static void initializeTrafficLightControllers() {
        List<TrafficLight> northSouthLights = new ArrayList<>();
        List<TrafficLight> eastWestLights = new ArrayList<>();
        // List<TrafficLight> southNorthLights = new ArrayList<>();
        // List<TrafficLight> westEastLights = new ArrayList<>();
        int redDuration = 60*5;
        int greenDuration = 40*5;
        int yellowDuration = 10*5;

        // Add lights to the appropriate group based on direction (assuming 0 is north-south and 1 is east-west)
        for (TrafficLight trafficLight : Scene.trafficLights) {
            if (trafficLight.getDirection() == 0) {
                northSouthLights.add(trafficLight);
            } else if (trafficLight.getDirection() == 1) {
                eastWestLights.add(trafficLight);
            }         }

                // Initialize the controllers
        northSouthController = new TrafficLightController(0,northSouthLights,redDuration,greenDuration,yellowDuration);
        northSouthController.currentColor = "green";
        northSouthController.waitTime = 0;
        eastWestController = new TrafficLightController(1,eastWestLights,redDuration,greenDuration,yellowDuration);
        eastWestController.waitTime = 0;
        // southNorthController = new TrafficLightController(2,southNorthLights);
        // westEastController = new TrafficLightController(3,westEastLights);

    }
    
    /**
     * loads all media, road constructs etc.
     * @param scene scene to load to
     */
    public static void load(Scene scene)
    {
        // Read map
        Scanner sc;
        try {
            sc = new Scanner(new File(path));
            // Directions for road 1011 Indicated N-S-W -> -|
            String directions = sc.nextLine();
            System.out.println(directions);
            int y = 0;
            while (sc.hasNextLine())
            {
                String[] line = sc.nextLine().split("");
                for (int x = 0; x < line.length; x++)
                {
                    scene.board[x][y] = Integer.parseInt(line[x]);
                }
                y++;
                
            }
            sc.close();
        } catch (FileNotFoundException e) {
            log("[Exception] " + path + " file not found in <load(Scene scene)>", 1);
        }
        log("[Console] Successfully loaded resource: " + path, 0);
        
    }

    public static synchronized Boolean getstate()
    {
        return APIStarted;
    }

    public static synchronized void setState(Boolean state)
    {
        APIStarted = state;
    }

    public static int getID()
    {
        return id++;
    }

    public static void heat() {
    
        for (Car car : scene.cars) {
            double carx = car.x;
            double cary = car.y;
            int scaledX = (int) ((carx/width)*heatmap.length);
            int scaledY = (int) ((cary/height)*heatmap.length);
            // System.out.println(scaledX + " " + scaledY);
            if (scaledX >= 0 && scaledX < heatmap.length && scaledY >= 0 && scaledY < heatmap.length) {
                heatmap[scaledX][scaledY] += 10;
                try {
                    heatmap[scaledX+1][scaledY] += 5;
                } catch (Exception e) {
    
                }
                try {
                    heatmap[scaledX-1][scaledY] += 5;
                } catch (Exception e) {
    
                }
                try {
                    heatmap[scaledX][scaledY+1] += 5;
                } catch (Exception e) {
    
                }
                try {
                    heatmap[scaledX][scaledY-1] += 5;
                } catch (Exception e) {
    
                }
    
    
            }
        }
    }

    /**
     * Used to log messages to console
     * @param message
     * @param code
     */
    public static void log(String message, int code)
    {
        switch (code)
        {
            case 0:
                System.out.println(BLUE + message + RESET);
                break;
            case 1:
                System.out.println(YELLOW + message + RESET);
                break;
            case 2:
                System.out.println(RED + message + RESET);
                break;
            case 3:
                System.out.println(PINK + message + RESET);
            default:
                break;
        }

    }
}
