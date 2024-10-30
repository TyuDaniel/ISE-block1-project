package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class CpuClockGraph {
    private XYChart.Series<Number, Number> clockSeries;  // Holds the CPU clock speed data
    private int timeInMilliseconds = 0;  // Keeps track of elapsed time
    private static final int MAX_TIME_RANGE = 10000;  // Display last 10 seconds
    private static final int UPDATE_INTERVAL = 100;   // Update every 100 milliseconds
    private static final double TIME_SCALE = 0.7;     // Makes time run a bit faster

    private LineChart<Number, Number> clockChart;  // The chart for displaying the data
    private CentralProcessor processor;  // Interface to get CPU info
    private long[] previousTicks;  // Previous CPU ticks for load calculations

    public CpuClockGraph() {
        // Get system info and the CentralProcessor instance
        SystemInfo systemInfo = new SystemInfo();
        processor = systemInfo.getHardware().getProcessor();
        previousTicks = processor.getSystemCpuLoadTicks();  // Initial ticks

        // Set up the X and Y axes for the chart
        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1);  // X-axis for time
        NumberAxis yAxis = new NumberAxis(0, 100, 10);  // Y-axis for CPU load percentage

        // Create the line chart and set its title
        clockChart = new LineChart<>(xAxis, yAxis);
        clockChart.setTitle("CPU Clock Speed");

        // Clean up the chart appearance
        clockChart.setHorizontalGridLinesVisible(false);
        clockChart.setVerticalGridLinesVisible(false);
        clockChart.setLegendVisible(false);

        // Prepare the series for the clock speed data
        clockSeries = new XYChart.Series<>();
        clockSeries.setName("CPU Clock Speed");
        clockChart.getData().add(clockSeries);
        clockChart.setCreateSymbols(false);  // Just show the line, no symbols

        // Start a timeline to update the clock speed every 0.1 seconds
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateCpuClockSpeed())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();  // Kick off the timeline
    }

    private void updateCpuClockSpeed() {
        long[] currentTicks = processor.getSystemCpuLoadTicks();  // Get current CPU ticks
        double clockSpeed = processor.getSystemCpuLoadBetweenTicks(previousTicks) * 100;  // Calculate CPU load percentage

        previousTicks = currentTicks;  // Update previous ticks for next round

        // Add current clock speed to the series
        clockSeries.getData().add(new XYChart.Data<>((timeInMilliseconds * TIME_SCALE / 1000.0), clockSpeed));

        timeInMilliseconds += UPDATE_INTERVAL;  // Update the time

        // Remove old data points to keep the graph fresh
        if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
            clockSeries.getData().remove(0);  // Drop the oldest data point
        }

        // Adjust the X-axis to show the last 10 seconds
        ((NumberAxis) clockSeries.getChart().getXAxis()).setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        ((NumberAxis) clockSeries.getChart().getXAxis()).setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
    }

    // Get the LineChart for embedding in the UI
    public LineChart<Number, Number> getClockChart() {
        return clockChart;
    }
}
