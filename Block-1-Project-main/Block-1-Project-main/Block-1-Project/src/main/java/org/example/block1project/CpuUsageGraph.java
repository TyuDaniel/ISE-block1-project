package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class CpuUsageGraph {

    private XYChart.Series<Number, Number> cpuSeries;
    private int timeInMilliseconds = 0;
    private static final int MAX_TIME_RANGE = 10000;
    private static final int UPDATE_INTERVAL = 100;
    private static final double TIME_SCALE = 0.7;
    private LineChart<Number, Number> lineChart;

    public CpuUsageGraph() {
        // Create the X and Y axes
        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1);
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setVisible(false);
        yAxis.setVisible(false);

        // Create a LineChart to display CPU usage over time
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("CPU Usage %");
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setLegendVisible(false);

        // Create a Series to hold the data
        cpuSeries = new XYChart.Series<>();
        cpuSeries.setName("CPU Usage");

        // Add the series to the LineChart
        lineChart.getData().add(cpuSeries);
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("-fx-background-color: transparent;");
        lineChart.setId("cpuClockChart");

        // Start a Timeline to update the CPU usage regularly
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateCpuUsage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Updates the CPU usage and add data to the graph
    private void updateCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double cpuUsage = osBean.getSystemCpuLoad() * 100;
        cpuUsage = Math.max(0, Math.min(100, cpuUsage));
        cpuSeries.getData().add(new XYChart.Data<>((timeInMilliseconds * TIME_SCALE / 1000.0), cpuUsage));
        timeInMilliseconds += UPDATE_INTERVAL;

        if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
            cpuSeries.getData().remove(0);
        }

        ((NumberAxis) cpuSeries.getChart().getXAxis()).setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
        ((NumberAxis) cpuSeries.getChart().getXAxis()).setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
    }

    // Return the LineChart for embedding in other GUIs
    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }
}
