// UserInput.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInput extends JFrame {
    public static JTextField maxCarsField;
    private JTextField carSpawnRateField;
    private JTextField trafficDensityField;
    private JComboBox<String> roadTypeSelector;
    public static JComboBox<String> trafficPatternSelector;
    public static JComboBox<String> trafficOptiSelector;

    public UserInput() {
        setTitle("Traffic Simulation Settings");
        setSize(600, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Max Cars:"));
        maxCarsField = new JTextField("100");
        add(maxCarsField);

        add(new JLabel("Traffic Pattern:"));
        String[] patterns = {"Balanced", "Rush Hour"};
        trafficPatternSelector = new JComboBox<>(patterns);
        add(trafficPatternSelector);

        add(new JLabel("Traffic Optimised:"));
        String[] options = {"False", "True"};
        trafficOptiSelector = new JComboBox<>(options);
        add(trafficOptiSelector);


        setLocationRelativeTo(null);
    }

    private void startSimulation() {
        int maxCars = Integer.parseInt(maxCarsField.getText());
        int carSpawnRate = Integer.parseInt(carSpawnRateField.getText());
        int trafficDensity = Integer.parseInt(trafficDensityField.getText());
        String selectedRoadType = (String) roadTypeSelector.getSelectedItem();
        String trafficPattern = (String) trafficPatternSelector.getSelectedItem();

        dispose();
        Engine.startSimulation(maxCars, carSpawnRate, trafficDensity, selectedRoadType, trafficPattern);
    }
}