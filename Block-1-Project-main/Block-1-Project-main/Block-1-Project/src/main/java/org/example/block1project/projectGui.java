package org.example.block1project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class projectGui extends Application {
    // Hardware monitoring components
    private CpuUsageGraph cpuLineGraph;
    private RamUsageGauge ramUsage;
    private CpuUsageGauge cpuUsageGauge;
    private CpuClockGraph cpuClockGraph;
    private Home home;
    private Battery battery;
    private CpuTemperatureGraph cpuTemp;
    private FanSpeedGraph fan;
    private NetworkUsage network;
    private CpuFrequencyChart cpuFrequencyChart;
    private DiskReadWriteGraph diskReadWriteGraph;

    @Override
    public void start(Stage primaryStage) {
        // Initialising new objects to easily call functions later
        cpuLineGraph = new CpuUsageGraph();
        ramUsage = new RamUsageGauge();
        cpuUsageGauge = new CpuUsageGauge();
        cpuClockGraph = new CpuClockGraph();
        home = new Home();
        battery = new Battery();
        cpuTemp = new CpuTemperatureGraph();
        network = new NetworkUsage();
        cpuFrequencyChart = new CpuFrequencyChart();
        diskReadWriteGraph = new DiskReadWriteGraph();

        // Centres the Battery information
        VBox batteryVBox = new VBox(10);
        batteryVBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Battery.getBatteryInfo(batteryVBox);

        // Initialises a grid layout for the line graphs on the CPU Tab
        GridPane cpuPage = new GridPane();
        cpuPage.setHgap(20);
        cpuPage.setVgap(20);
        cpuPage.setStyle("-fx-padding: 20;");
        cpuPage.add(cpuLineGraph.getLineChart(), 0, 0);
        cpuPage.add(cpuClockGraph.getClockChart(), 1, 0);
        cpuPage.add(CpuTemperatureGraph.getLineChart(), 0, 1);
        cpuPage.add(cpuFrequencyChart.getFrequencyChart(), 1, 1);

        // Initialises the layout for the accelerometers on the Memory Tab
        GridPane memoryGrid = new GridPane();
        memoryGrid.setHgap(20);
        memoryGrid.setVgap(20);
        memoryGrid.setStyle("-fx-padding: 20; -fx-alignment: center;");
        memoryGrid.add(ramUsage.getRamUsagePane(), 0, 0);
        memoryGrid.add(cpuUsageGauge.getCpuUsagePane(), 1, 0);

        // Creates the Tab Section
        TabPane tabPane = new TabPane();

        // Creates a new Tab for each section of the project
        Tab homeTab = new Tab("Home");
        homeTab.setContent(home.getHomePageLayout());
        homeTab.setClosable(false);

        Tab cpuTab = new Tab("CPU");
        cpuTab.setContent(cpuPage);
        cpuTab.setClosable(false);

        Tab memoryTab = new Tab("Memory");
        memoryTab.setContent(memoryGrid);
        memoryTab.setClosable(false);

        Tab batteryTab = new Tab("Battery");
        batteryTab.setContent(batteryVBox);
        batteryTab.setClosable(false);

        Tab networkTab = new Tab("Network");
        networkTab.setContent(network.getNetworkUsageInfo());
        networkTab.setClosable(false);

        Tab diskTab = new Tab("Disk");
        diskTab.setContent(diskReadWriteGraph.getDiskReadWriteCharts());
        diskTab.setClosable(false);

        tabPane.getTabs().addAll(homeTab, cpuTab, memoryTab, batteryTab, networkTab, diskTab);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        // Creates the stage
        Scene scene = new Scene(root, 1100, 600);
        scene.getStylesheets().add(getClass().getResource("/org/example/block1project/styles.css").toExternalForm()); // References CSS code

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hardware Monitor");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
