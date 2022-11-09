module com.example.scraping {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires sib.api.v3.sdk;
    requires java.sql;

    requires htmlunit;
    requires mysql.connector.java;
    requires java.desktop;

    opens com.example.scraping to javafx.fxml;
    exports com.example.scraping;
}