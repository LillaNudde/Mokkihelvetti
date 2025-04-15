package com.github.lillanudde.mokkihelvetti;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class GUI extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        Button cabinManagement = new Button("Mökkien hallinta");
        Button reservationManagement = new Button("Majoitusvarausten hallinta");
        Button customerManagement = new Button("Asiakashallinta");
        Button billingManagement = new Button("Laskujen hallinta ja seuranta");
        Button accommodationReports = new Button("Majoittumisen raportointi");

        HBox topRow = new HBox(cabinManagement, reservationManagement);
        topRow.setAlignment(Pos.CENTER);
        topRow.setSpacing(10);
        HBox middleRow = new HBox(customerManagement, billingManagement);
        middleRow.setAlignment(Pos.CENTER);
        middleRow.setSpacing(10);
        HBox bottomRow = new HBox(accommodationReports);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setSpacing(10);
        VBox buttonBox = new VBox(topRow, middleRow, bottomRow);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        primaryStage.setTitle("GUI");
        BorderPane root = new BorderPane();
        root.setCenter(buttonBox);


        Scene scene = new Scene(root, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
