module org.example.block1project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.management;
    //requires com.github.oshi;
    requires oshi.demo;
    requires java.desktop;

    opens org.example.block1project to javafx.fxml;
    exports org.example.block1project;
}