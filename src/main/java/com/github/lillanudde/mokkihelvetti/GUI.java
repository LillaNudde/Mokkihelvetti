package com.github.lillanudde.mokkihelvetti;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class GUI extends Application
{

public static void main(String[] args) 
{
    launch(args);
}



    @Override
    public void start(Stage primaryStage)
    {
        Button cabinManagement = new Button("Mökkien hallinta");
        Button reservationManagement = new Button("Majoitusvarausten hallinta");
        Button customerManagement = new Button("Asiakashallinta");
        Button billingManagement = new Button("Laskujen hallinta ja seuranta");
        Button accommodationReports = new Button("Majoittumisen raportointi");

        cabinManagement.setPrefWidth(250);
        cabinManagement.setPrefHeight(50);
        reservationManagement.setPrefWidth(250);
        reservationManagement.setPrefHeight(50);
        customerManagement.setPrefWidth(250);
        customerManagement.setPrefHeight(50);
        billingManagement.setPrefWidth(250);
        billingManagement.setPrefHeight(50);
        accommodationReports.setPrefWidth(250);
        accommodationReports.setPrefHeight(50);

        VBox buttonBox = new VBox(cabinManagement, reservationManagement, customerManagement, billingManagement, accommodationReports);
        buttonBox.setSpacing(25);
        buttonBox.setAlignment(Pos.CENTER);

        primaryStage.setTitle("GUI");
        BorderPane root = new BorderPane();
        root.setCenter(buttonBox);


        Scene scene = new Scene(root, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
