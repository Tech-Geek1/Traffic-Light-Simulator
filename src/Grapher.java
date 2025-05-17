import javax.swing.Timer;
import java.util.LinkedList;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class Grapher {
    SwingWrapper<XYChart> sw;
    XYChart chart;
    long startTime;

    LinkedList<Double> yValues = new LinkedList<>();
    LinkedList<Double> xValues = new LinkedList<>();

    Timer timer;
    boolean running = false;

    int count = 0;

    public void go() {
        chart = QuickChart.getChart("Average Wait time (s/60)", "Time (s)", "Average Wait Time", "aveWait", new double[] { 0 }, new double[] { 0 });
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisTicksVisible(true);

        sw = new SwingWrapper<>(chart);
        sw.displayChart();

        startTime = System.currentTimeMillis();
        
        timer = new Timer(40, e -> updateChart());
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
    
    public double getY() {
        return Engine.AVE;
    }
    
    public void updateChart() {
        double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
    
        yValues.add(getY());
        xValues.add(elapsedTime);
    
        if (yValues.size() > 300) {
            yValues.removeFirst();
            xValues.removeFirst();
        }
    
        double[] xArray = new double[xValues.size()];
        double[] yArray = new double[yValues.size()];
        for (int i = 0; i < xValues.size(); i++) {
            xArray[i] = xValues.get(i);
            yArray[i] = yValues.get(i);
        }
    
        chart.updateXYSeries("aveWait", xArray, yArray, null);
        if (count % 3 == 0)
        {
            sw.repaintChart();
            count = 0;
        }
        count++;
    }
}
