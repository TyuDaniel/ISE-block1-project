package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.HWDiskStore;

import java.util.List;

public class DiskReadWriteGraph {
    private final LineChart<Number, Number> readChart; 
    private final LineChart<Number, Number> writeChart; 
    private final XYChart.Series<Number, Number> readSeries; 
    private final XYChart.Series<Number, Number> writeSeries; 
    private final SystemInfo systemInfo; 
    private final HWDiskStore disk; 
    private int timeInMilliseconds = 0;

    private static final int MAX_TIME_RANGE = 10000; 
    private static final int UPDATE_INTERVAL = 100; 
    private static final double TIME_SCALE = 0.7; 

    private long previousBytesRead = 0; 
    private long previousBytesWritten = 0; 
    private long cumulativeReadSpeed = 0; 
    private long cumulativeWriteSpeed = 0; 
    private int sampleCount = 0; 

    public DiskReadWriteGraph() {
        systemInfo = new SystemInfo(); // Initialize SystemInfo for hardware data
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<HWDiskStore> diskStores = hardware.getDiskStores();
        disk = diskStores.isEmpty() ? null : diskStores.get(0); // Select first disk if available

        // Initialize read and write charts with labeled axes and tick marks
        readChart = createStyledChart("Disk Read Speed (KB/s)");
        writeChart = createStyledChart("Disk Write Speed (KB/s)");

        readSeries = new XYChart.Series<>(); 
        writeSeries = new XYChart.Series<>();

        readChart.getData().add(readSeries); // Add read data series to read chart
        writeChart.getData().add(writeSeries); // Add write data series to write chart

        startMonitoring(); // Start monitoring disk read/write speeds
    }

    private LineChart<Number, Number> createStyledChart(String title) {
        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1); 
        NumberAxis yAxis = new NumberAxis(); 

        xAxis.setLabel("Time (s)");
        yAxis.setLabel("Speed (KB/s)");

        xAxis.setTickUnit(1); 
        yAxis.setAutoRanging(true); // Automatically scale the y-axis of the graph

        // Set chart appearance and style
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chart.setStyle("-fx-background-color: transparent;");

        return chart;
    }

    private void startMonitoring() {
        if (disk == null) {
            System.err.println("No disk drive found.");
            return;
        }

        // Initialize previous read/write bytes to start monitoring
        disk.updateAttributes();
        previousBytesRead = disk.getReadBytes();
        previousBytesWritten = disk.getWriteBytes();

        // Timeline for periodic updates every UPDATE_INTERVAL ms
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateCharts()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play(); // Start the timeline animation for continuous monitoring
    }

    private void updateCharts() {
        disk.updateAttributes(); // Refresh disk attributes with current stats

        long currentBytesRead = disk.getReadBytes();
        long currentBytesWritten = disk.getWriteBytes();

        long readSpeed = (currentBytesRead - previousBytesRead) / 1024; // Calculate read speed in KB
        long writeSpeed = (currentBytesWritten - previousBytesWritten) / 1024; // Calculate write speed in KB

        previousBytesRead = currentBytesRead;
        previousBytesWritten = currentBytesWritten;

        // Add current speed data to the charts
        readSeries.getData().add(new XYChart.Data<>(timeInMilliseconds * TIME_SCALE / 1000.0, readSpeed));
        writeSeries.getData().add(new XYChart.Data<>(timeInMilliseconds * TIME_SCALE / 1000.0, writeSpeed));

        // Update cumulative read/write speeds and sample count for average calculation
        cumulativeReadSpeed += readSpeed;
        cumulativeWriteSpeed += writeSpeed;
        sampleCount++;

        // Calculate and display average speeds in chart titles
        double averageReadSpeed = (double) cumulativeReadSpeed / sampleCount;
        double averageWriteSpeed = (double) cumulativeWriteSpeed / sampleCount;

        readChart.setTitle(String.format("Disk Read Speed (KB/s) - Avg: %.2f KB/s", averageReadSpeed));
        writeChart.setTitle(String.format("Disk Write Speed (KB/s) - Avg: %.2f KB/s", averageWriteSpeed));

        timeInMilliseconds += UPDATE_INTERVAL; // Increment time for next data point

        // Remove old data if time exceeds max range to keep chart dynamic
        if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
            readSeries.getData().remove(0);
            writeSeries.getData().remove(0);
        }

        // Adjust x-axis bounds to show recent data within MAX_TIME_RANGE
        NumberAxis readXAxis = (NumberAxis) readChart.getXAxis();
        readXAxis.setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        readXAxis.setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);

        NumberAxis writeXAxis = (NumberAxis) writeChart.getXAxis();
        writeXAxis.setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        writeXAxis.setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
    }

    public VBox getDiskReadWriteCharts() {
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(readChart, writeChart);
        return vbox;
    }
}
