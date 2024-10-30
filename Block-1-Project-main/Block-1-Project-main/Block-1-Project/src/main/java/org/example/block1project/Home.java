package org.example.block1project;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.lang.reflect.Method;
import java.util.List;

public class Home {
    private VBox homePageLayout;
    private SystemInfo systemInfo;

    public Home() {
        homePageLayout = new VBox(20);
        systemInfo = new SystemInfo();
        Platform.runLater(this::updateHomePageInfo);
    }

    private void updateHomePageInfo() {
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        GlobalMemory memory = hal.getMemory();
        OperatingSystem os = systemInfo.getOperatingSystem();

        Baseboard baseboard = hal.getComputerSystem().getBaseboard();
        String manufacturer = baseboard.getManufacturer();
        String model = baseboard.getModel();
        String serialNumber = baseboard.getSerialNumber();
        String version = baseboard.getVersion();

        String osInfo = String.format("\nOS: %s %s (%s), Uptime: %s", // String output of OS version and PC uptime
            os.getFamily(),
            os.getVersionInfo().getVersion(),
            os.getManufacturer(),
            formatUptime(os.getSystemUptime()));
        Label osLabel = new Label(osInfo);

        // OSHI gathers general CPU information
        String cpuBrand = cpu.getProcessorIdentifier().getName();
        int cpuCores = cpu.getLogicalProcessorCount();
        double cpuLoad = cpu.getSystemCpuLoad(1000) * 100;

        Label cpuLabel = new Label(String.format("CPU: %s, %d cores, %.2f%% load", // String output of CPU brand, the amount of cores and Load %
            cpuBrand, cpuCores, cpuLoad));

        // OSHI gathers CPU cache information
        String cacheInfo = getCacheInfo(cpu);
        Label cacheLabel = new Label("CPU Cache Types: " + cacheInfo); // String output of CPU cache information

        // OSHI gathers general memory information
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;

        Label ramLabel = new Label(String.format("Memory: %.2f GiB used / %.2f GiB total", // String output of Memory information
            bytesToGiB(usedMemory), bytesToGiB(totalMemory)));

        // OSHI gathers Ram usage and size
        long totalStorage = getTotalDiskSpace();
        long freeStorage = getFreeDiskSpace();
        long usedStorage = totalStorage - freeStorage;

        Label storageLabel = new Label(String.format("Storage: %.2f GiB used / %.2f GiB total", // String output of how much RAM is used
            bytesToGiB(usedStorage), bytesToGiB(totalStorage)));

        // OSHI gathers PCI details
        String graphicsInfo = getGraphicsInfo(hal);
        Label graphicsLabel = new Label("Graphics: " + graphicsInfo); // String output of PCI information

        // OSHI gathers information on connected USB devices
        String usbInfo = getUSBInfo(hal);
        Label usbInfoLabel = new Label("USB Devices: " + usbInfo); // String output of conencted USB details

        // OSHI gathers general information on the motherboard
        Label motherboardInfoLabel = new Label(String.format("Motherboard: %s%n Model: %s%n Serial: %s%n Version: %s", // String output of motherboard information
            manufacturer, model, serialNumber, version));

        // OSHI gathers general disk information
        Label diskInfoLabel = new Label(getDiskInfo(hal));

        homePageLayout.getChildren().addAll(osLabel, cpuLabel, cacheLabel, ramLabel, storageLabel, graphicsLabel, usbInfoLabel, motherboardInfoLabel, diskInfoLabel);
    }
    // Method to retrieve cache information for each CPU cache
    private String getCacheInfo(CentralProcessor cpu) {
        StringBuilder cacheInfo = new StringBuilder();
        List<CentralProcessor.ProcessorCache> caches = cpu.getProcessorCaches();

        for (CentralProcessor.ProcessorCache cache : caches) {
            if (cache.getCacheSize() < 1024) {
                cacheInfo.append(String.format("L%d %s Cache, ", cache.getLevel(), cache.getType()));
            } else {
                cacheInfo.append(String.format("L%d %s Cache: %d KB, ", cache.getLevel(), cache.getType(), cache.getCacheSize() / 1024));
            }
        }

        if (cacheInfo.length() > 0) {
            cacheInfo.setLength(cacheInfo.length() - 2);
        } else {
            cacheInfo.append("No cache information available");
        }
        return cacheInfo.toString();
    }

    // Method to retrieve disk information from connected drives
    private String getDiskInfo(HardwareAbstractionLayer hal) {
        StringBuilder diskInfo = new StringBuilder("Disk Information:\n");
        List<HWDiskStore> diskStores = hal.getDiskStores();

        for (HWDiskStore disk : diskStores) {
            disk.updateAttributes(); // Updates disk attributes to retrieve latest values
            diskInfo.append(String.format("Name: %s\nModel: %s\nSerial: %s\nTotal Size:\t %.2f GiB\nFree Space:\t %.2f GiB\n",
                disk.getName(),
                disk.getModel(),
                disk.getSerial(),
                bytesToGiB(disk.getSize()),
                bytesToGiB(disk.getWriteBytes())));
        }

        return diskStores.isEmpty() ? "No disk information available" : diskInfo.toString();
    }

    // Calculates total disk space from all available file systems
    private long getTotalDiskSpace() {
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(store -> store.getTotalSpace())
                .sum();
    }

    // Calculates total free disk space from all available file systems
    private long getFreeDiskSpace() {
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(store -> store.getUsableSpace())
                .sum();
    }

    // Retrieves graphics card information
    private String getGraphicsInfo(HardwareAbstractionLayer hal) {
        StringBuilder graphicsInfo = new StringBuilder();
        for (GraphicsCard gpu : hal.getGraphicsCards()) {
            graphicsInfo.append(String.format("%s, VRam: %.2f GiB\n", gpu.getName(), bytesToGiB(gpu.getVRam())));
        }
        return graphicsInfo.toString();
    }

    // Retrieves connected USB device information
    private String getUSBInfo(HardwareAbstractionLayer hal) {
        StringBuilder usbInfoBuilder = new StringBuilder();
        List<UsbDevice> usbDevices = hal.getUsbDevices(true);

        for (int i = 0; i < usbDevices.size(); i++) {
            UsbDevice usbDevice = usbDevices.get(i);
            usbInfoBuilder.append(String.format("%s (Vendor ID: %s)", usbDevice.getName(), usbDevice.getVendorId()));
            if ((i + 1) % 3 == 0 && (i + 1) < usbDevices.size()) {
                usbInfoBuilder.append("\n");
            } else {
                usbInfoBuilder.append(", ");
            }
        }

        if (usbInfoBuilder.length() == 0) {
            return "No USB devices found";
        }
        usbInfoBuilder.setLength(usbInfoBuilder.length() - 2); // Remove trailing comma and space
        return usbInfoBuilder.toString();
    }

    // Converts bytes to GiB for display
    private double bytesToGiB(long bytes) {
        return bytes / (1024.0 * 1024 * 1024);
    }

    // Formats uptime in hours, minutes, and seconds
    public String formatUptime(long uptime) {
        long hours = uptime / 3600;
        long minutes = (uptime % 3600) / 60;
        long seconds = uptime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public VBox getHomePageLayout() {
        return homePageLayout;
    }
}
