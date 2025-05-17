import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Used for communication between the front and back end
 */
public class API
{
    public static Socket socket;
    public static PrintWriter writer;
    public static OutputStream outputStream;
    public static InputStream inputStream;

    public static String serverAddress = "localhost";
    public static int port = 12345;

    /**
     * Begins the api and initialises a connection to it
     */
    public static void init()
    {
        Thread api = new Thread() {
            public void run()
            {
                try {
                    String FrontEnd = "frontend/FrontEnd.py";
                    ProcessBuilder processBuilder = new ProcessBuilder("python3", FrontEnd);
                    
                    Process process = processBuilder.start();
                    
                    // Read the output from the FrontEnd
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Started"))
                        {
                            Engine.setState(true);
                        }
                        System.out.println(line);
                    }

                    int exitCode = process.waitFor();
                    System.out.println("Front end exited with code: " + exitCode);
                    System.exit(exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        api.setDaemon(true);
        api.start();

        try {
            socket = new Socket(serverAddress, port);
            outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream, true);
            inputStream = socket.getInputStream();
            StringBuilder response = new StringBuilder();
            for (int ch; (ch = inputStream.read()) != -1; ) {
                response.append((char) ch);
            }
            switch (response.toString()) {
                case "Success":
                    Engine.setState(true);
                    break;
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            Engine.log("[Critical] API failed to start", 2);
            // System.exit(0);
        }
        
    }

    /**
     * Sends the current board to the front end
     * @param scene Scene holding board state
     */
    public static void UpdateBoard(Scene scene)
    {
        String board = "";
        for (int y = 0; y < scene.height; y++)
        {
        for (int x = 0; x < scene.width; x++)
            {
                board+=scene.board[x][y];
            }
            board+="\\n";
        }
        String packet = "{\"command\": \"updateBoard\",\"data\": \"" + board + "\"}";
        writer.println(packet);
    }

    /**
     * Informs front end of the creation of a Car
     * @param car Car being added to the front end
     */
    public static void CreateCar(Car car)
    {
        int carID = 0;//car.id
        String packet = "{\"command\": \"createCar\",\"data\": [{\"ID\":" + carID + ",\"x\":" + car.x + ",\"y\":" + car.y + "}]}";
        writer.println(packet);
    }

    /**
     * Updates the position of a car on the front end
     * @param car Car to be updated
     */
    public static void UpdateCar(Car car)
    {
        int carID = 0;//car.id
        String packet = "{\"command\": \"updateCar\",\"data\": [{\"ID\":" + carID + ",\"x\":" + car.x + ",\"y\":" + car.y + "}]}";
        writer.println(packet);
    }

    /**
     * Creates a JSON of the lights states and sends it to the front end.
     * @param lights ArrayList of traffic light objects.
     */
    public static void UpdateLights(ArrayList<String> lights)
    {
        String packet = "{\"command\": \"updateLights\",\"data\": [";
        for (int i = 0; i < lights.size(); i++)
        {
            packet+="{\"ID\":\"" + lights.get(i) + "\",\"colour\":\"" + lights.get(i) + "\"}";
            if (i+1 != lights.size())
            {
                packet+=",";
            }
        }
        packet+="]}";
        writer.println(packet);
    }
}
