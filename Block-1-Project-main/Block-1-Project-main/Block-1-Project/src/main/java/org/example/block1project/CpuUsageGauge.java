package org.example.block1project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class CpuUsageGauge {

    private Arc usedCpuArc;  // Arc representing used CPU
    private Arc freeCpuArc;  // Arc representing free CPU
    private CentralProcessor processor;  // OSHI CentralProcessor object
    private Text usedCpuText;  // Text to display used CPU
    private Text freeCpuText;  // Text to display free CPU
    private Label cpuUsageLabel;  // Label for CPU usage

    private long[] prevTicks;

    public CpuUsageGauge() {
        // Initialize OSHI components
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        processor = hal.getProcessor();

        // Initialize the array to hold CPU tick information
        prevTicks = processor.getSystemCpuLoadTicks();

        // Set up the arcs to form a semi-circle (half-gauge)
        usedCpuArc = new Arc(0, 0, 150, 150, 180, 0);  // Center (0,0), radius 150
        usedCpuArc.setType(ArcType.OPEN);  // Use OPEN instead of ROUND for a hollow look
        usedCpuArc.setStroke(Color.web("#ffa000"));
        usedCpuArc.setStrokeWidth(30);  // Set the desired thickness of the arc
        usedCpuArc.setFill(Color.TRANSPARENT);  // No fill to create the hollow center

        freeCpuArc = new Arc(0, 0, 150, 150, 360, 0);
        freeCpuArc.setType(ArcType.OPEN);
        freeCpuArc.setStroke(Color.GRAY);
        freeCpuArc.setStrokeWidth(30);
        freeCpuArc.setFill(Color.TRANSPARENT);

        // Create labels for CPU usage
        usedCpuText = new Text("Used: 0%");
        freeCpuText = new Text("Free: 100%");

        // Set the text color to black
        usedCpuText.setFill(Color.BLACK);
        freeCpuText.setFill(Color.BLACK);

        // Label for CPU usage percentage
        cpuUsageLabel = new Label("CPU Usage %");
        cpuUsageLabel.setTextFill(Color.BLACK); // Set to black as well

        // Start a Timeline to update the Arcs and labels regularly
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateCpuUsage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCpuUsage() {
        // Get current CPU load using the previous ticks
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100; // Convert to percentage
        System.arraycopy(processor.getSystemCpuLoadTicks(), 0, prevTicks, 0, prevTicks.length); // Update previous ticks

        double usedPercentage = cpuLoad * 180 / 100; // Convert to arc length (180 degrees)

        Platform.runLater(() -> {
            usedCpuArc.setLength(-usedPercentage);
            freeCpuArc.setLength(180 - usedPercentage);

            // Update the CPU usage text
            usedCpuText.setText(String.format("Used: %.2f%%", cpuLoad));
            freeCpuText.setText(String.format("Free: %.2f%%", 100 - cpuLoad));

            // Center the text horizontally within the Pane
            usedCpuText.setX(290 - usedCpuText.getLayoutBounds().getWidth() / 2);  // Adjust X to center
            freeCpuText.setX(290 - freeCpuText.getLayoutBounds().getWidth() / 2);  // Adjust X to center

            // Set Y position to avoid overlap
            usedCpuText.setY(180);  // Position for used CPU text
            freeCpuText.setY(210);  // Position for free CPU text
        });
    }

    public Pane getCpuUsagePane() {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-alignment: center;"); // Center alignment for VBox

        Region spacer = new Region();
        spacer.setPrefHeight(40); // Set the height of the spacer

        vbox.getChildren().addAll(spacer, cpuUsageLabel, usedCpuText, freeCpuText);
        Pane arcPane = new Pane();
        arcPane.getChildren().addAll(freeCpuArc, usedCpuArc);

        // Center the arcs in the pane
        double centerX = 250;  // Center for a 600 width pane
        double centerY = 200;  // Centered vertically
        usedCpuArc.setTranslateX(centerX);
        usedCpuArc.setTranslateY(centerY);
        freeCpuArc.setTranslateX(centerX);
        freeCpuArc.setTranslateY(centerY);

        arcPane.setPrefSize(600, 300);  // Adjust size to fit
        arcPane.setStyle("-fx-alignment: center;");  // Center alignment for Pane

        vbox.getChildren().add(arcPane);
        return vbox;
    }
}
