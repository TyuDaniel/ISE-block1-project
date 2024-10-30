package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class CpuFrequencyChart {

    private XYChart.Series<Number, Number> frequencySeries;  // Holds the data points for CPU frequency
    private int timeInMilliseconds = 0;  // Keeps track of the elapsed time
    private static final int MAX_TIME_RANGE = 10000;  // Display the last 10 seconds on the graph
    private static final int UPDATE_INTERVAL = 100;  // Update the graph every 100 milliseconds
    private static final double TIME_SCALE = 0.7;  // Scale to speed up the time representation
    private CentralProcessor processor;  // Represents the CPU
    private LineChart<Number, Number> frequencyChart;  // Chart to visualize CPU frequency

    public CpuFrequencyChart() {
        SystemInfo systemInfo = new SystemInfo();  // Create an instance to fetch system info
        HardwareAbstractionLayer hal = systemInfo.getHardware();  // Get the hardware layer
        processor = hal.getProcessor();  // Access the CPU information

        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1);  // X-axis for time in seconds
        NumberAxis yAxis = new NumberAxis(0, 5, 0.5);  // Y-axis for frequency (0 to 5 GHz)

        frequencyChart = new LineChart<>(xAxis, yAxis);  // Initialize the line chart
        frequencyChart.setTitle("CPU Max Frequency over time (GHz)");  // Set chart title

        frequencyChart.setHorizontalGridLinesVisible(false);  // Clean look without grid lines
        frequencyChart.setVerticalGridLinesVisible(false);
        frequencyChart.setLegendVisible(false);  // Hide the legend

        frequencySeries = new XYChart.Series<>();  // Create a series for data points
        frequencySeries.setName("CPU Max Frequency (GHz)");  // Name of the series

        frequencyChart.getData().add(frequencySeries);  // Add the series to the chart
        frequencyChart.setCreateSymbols(false);  // Only show the line, no symbols
        frequencyChart.setId("cpuClockChart");  // Set an ID for the chart

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateCpuFrequency())  // Define update action
        );
        timeline.setCycleCount(Timeline.INDEFINITE);  // Run the timeline indefinitely
        timeline.play();  // Start updating the chart
    }

    private void updateCpuFrequency() {
        double maxFrequency = processor.getMaxFreq() / 1_000_000_000.0;  // Get max frequency in GHz

        frequencySeries.getData().add(new XYChart.Data<>((timeInMilliseconds * TIME_SCALE / 1000.0), maxFrequency));  // Add data point

        timeInMilliseconds += UPDATE_INTERVAL;  // Increment time tracker

        // Remove old data to keep the chart focused on the last 10 seconds
        if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
            frequencySeries.getData().remove(0);  // Remove the oldest point
        }

        // Update the X-axis bounds to reflect the time range
        ((NumberAxis) frequencySeries.getChart().getXAxis()).setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        ((NumberAxis) frequencySeries.getChart().getXAxis()).setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
    }

    public LineChart<Number, Number> getFrequencyChart() {
        return frequencyChart;  // Return the chart for GUI embedding
    }
}
