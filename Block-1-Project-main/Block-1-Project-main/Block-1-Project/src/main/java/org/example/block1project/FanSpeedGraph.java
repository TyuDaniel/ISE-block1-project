package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public class FanSpeedGraph {

    private XYChart.Series<Number, Number> fanSeries;
    private int timeInMilliseconds = 0;
    private static final int MAX_TIME_RANGE = 10000; // 10 seconds
    private static final int UPDATE_INTERVAL = 100; // Update every 100 ms
    private static final double TIME_SCALE = 0.7;
    private LineChart<Number, Number> lineChart;

    public FanSpeedGraph() {
        // setup chart and timeline
        NumberAxis xAxis = new NumberAxis(0, MAX_TIME_RANGE / 1000, 1);
        NumberAxis yAxis = new NumberAxis(0, 5000, 500); // Adjust y axis as needed
        xAxis.setVisible(false);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Fan Speed (RPM)");
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setLegendVisible(false);

        fanSeries = new XYChart.Series<>();
        fanSeries.setName("Fan Speed");

        lineChart.getData().add(fanSeries);
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("-fx-background-color: transparent;");

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(UPDATE_INTERVAL), event -> updateFanSpeed())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Update fan speed data
    private void updateFanSpeed() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        Sensors sensors = hal.getSensors();
        double fanSpeed = sensors.getFanSpeeds()[0]; // first fan speed 

        // valid fan speed chec
        if (fanSpeed != -1) {
            fanSeries.getData().add(new XYChart.Data<>((timeInMilliseconds * TIME_SCALE / 1000.0), fanSpeed));
            timeInMilliseconds += UPDATE_INTERVAL;

            if (timeInMilliseconds * TIME_SCALE > MAX_TIME_RANGE) {
                fanSeries.getData().remove(0);
            }

            // Update the X-axis range
            ((NumberAxis) fanSeries.getChart().getXAxis()).setLowerBound((timeInMilliseconds * TIME_SCALE - MAX_TIME_RANGE) / 1000.0);
            ((NumberAxis) fanSeries.getChart().getXAxis()).setUpperBound((timeInMilliseconds * TIME_SCALE) / 1000.0);
        }
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }
}
