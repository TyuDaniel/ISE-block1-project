package org.example.block1project;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;

import java.util.List;

public class Battery {

    public static void getBatteryInfo(VBox vbox) {
        // Initialize OSHI components
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();

        // Retrieve power sources
        List<PowerSource> powerSources = hal.getPowerSources();

        // Check if power sources are available
        if (powerSources.isEmpty()) {
            vbox.getChildren().add(new Label("No battery information available."));
            return;
        }

        // Display battery information for each power source
        for (PowerSource battery : powerSources) {
            double current = battery.getCurrentCapacity();
            double max = battery.getMaxCapacity();
            double currentPercentage = (current / max) * 100;
            Label name = new Label("Battery: " + battery.getName());
            Label remainingCapacity = new Label("Remaining Capacity: " + Math.round(currentPercentage) + "%");
            Label powerUsage = new Label("Power Usage (W): " + Math.abs(battery.getPowerUsageRate() / 1000));
            Label isCharging = new Label("Is Charging: " + battery.isCharging());
            Label voltage = new Label("Voltage (V): " + battery.getVoltage());
            name.setStyle("-fx-font-size: 25px; ");
            remainingCapacity.setStyle("-fx-font-size: 25px;");
            powerUsage.setStyle("-fx-font-size: 25px;");
            isCharging.setStyle("-fx-font-size: 25px;");
            voltage.setStyle("-fx-font-size: 25px;");

            // Add labels to VBox
            vbox.getChildren().addAll(name, remainingCapacity, powerUsage, isCharging, voltage);
        }
    }
}
