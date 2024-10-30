package org.example.block1project;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;

public class CpuTemperatureGraph {

    private XYChart.Series<Number, Number> temperatureSeries;
    private int timeInMilliseconds = 0;
    private static final int MAX_TIME_RANGE = 10000; // 10 seconds
    private static final int UPDATE_INTERVAL = 100; // Update every 100ms
    private static final double TIME_SCALE = 0.7; 
    private static LineChart<Number, Number> lineChart;
    private Sensors sensors;

    public CpuTemperatureGraph() {
        // Initialize OSHI SystemInfo and Sensors
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        sensors = hardware.getSensors();

        // Create the X and Y axes for temperature
        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1);
        NumberAxis yAxis = new NumberAxis(20, 100, 10); 
        xAxis.setVisible(false);
        yAxis.setVisible(false);

        // Create a LineChart to display CPU temperature over time
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("CPU Temperature (°C)");
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setLegendVisible(false);

        // Create a Series to hold the data
        temperatureSeries = new XYChart.Series<>();
        temperatureSeries.setName("CPU Temperature");

        // Add the series to the LineChart
        lineChart.getData().add(temperatureSeries);
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("-fx-background-color: transparent;");
        lineChart.setId("cpuClockChart");

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateCpuTemperature())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Update the CPU temperature and add data to the graph
    private void updateCpuTemperature() {
        // Retrieve the current CPU temperature using OSHI
        double cpuTemperature = sensors.getCpuTemperature();
        cpuTemperature = Math.max(20, Math.min(100, cpuTemperature)); 
        temperatureSeries.getData().add(new XYChart.Data<>((timeInMilliseconds * TIME_SCALE / 1000.0), cpuTemperature));
        timeInMilliseconds += UPDATE_INTERVAL;

        if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
            temperatureSeries.getData().remove(0);
        }

        ((NumberAxis) temperatureSeries.getChart().getXAxis()).setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        ((NumberAxis) temperatureSeries.getChart().getXAxis()).setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
    }

    // Method to return the LineChart for embedding in other GUIs
    public static LineChart<Number, Number> getLineChart() {
        return lineChart;
    }
}
