package com.github.lillanudde.mokkihelvetti;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;


public class GUI extends Application
{

    // Single Scene and BorderPane to be re-used. Allows the use of a single window that can be updated
    private Scene scene; 
    private BorderPane mainLayout;

    // Start method
    @Override
    public void start(Stage primaryStage)
    {

        primaryStage.setTitle("GUI");

        mainLayout = new BorderPane();
        scene = new Scene(mainLayout, 1920, 1080);

        // Show main menu upon launch
        showMainMenu();

        primaryStage.setScene(scene);
        primaryStage.show();

    
    }

    // Main Method
    public static void main(String[] args) 
    {
        launch(args);
    }

    // Button machine
    private Button createMenuButton(String label, Runnable action)
    {
        Button button = new Button(label);
        button.setPrefSize(250, 50);
        button.setOnAction(e -> action.run());
        return button;
    }

    // Main menu
    private void showMainMenu()
    {
       VBox buttonBox = new VBox(
            createMenuButton("Mökkien hallinta", this::showCabinManagement),
            createMenuButton("Majoitusvarausten hallinta", this::showReservationManagement),
            createMenuButton("Asiakashallinta", this::showCustomerManagement),
            createMenuButton("Laskujen hallinta ja seuranta", this::showBillingManagement),
            createMenuButton("Majoittumisen raportointi", this::showAccommodationReports)
       );
       buttonBox.setSpacing(25);
       buttonBox.setAlignment(Pos.CENTER);

       mainLayout.setCenter(buttonBox);
    }

    private void showCabinManagement() 
    {
        // Clear the window
        mainLayout.setCenter(new VBox());

        // Dataless TableView
        TableView<Cabin> cabinTable = new TableView<>();

        // Column hell
        TableColumn<Cabin, Integer> cabinId = new TableColumn<>("Mökki ID");
        cabinId.setCellValueFactory(new PropertyValueFactory<>("cabinId"));
        
        TableColumn<Cabin, Integer> customerId = new TableColumn<>("Asiakas ID");
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        
        TableColumn<Cabin, Integer> weeklyPrice = new TableColumn<>("Viikkohinta");
        weeklyPrice.setCellValueFactory(new PropertyValueFactory<>("weeklyPrice"));
        
        TableColumn<Cabin, String> cabinAddress = new TableColumn<>("Osoite");
        cabinAddress.setCellValueFactory(new PropertyValueFactory<>("cabinAddress"));
        
        TableColumn<Cabin, Boolean> petsAllowed = new TableColumn<>("Sallitaanko lemmikit");
        petsAllowed.setCellValueFactory(new PropertyValueFactory<>("petsAllowed"));
        
        TableColumn<Cabin, Boolean> airConditioning = new TableColumn<>("Ilmastointi");
        airConditioning.setCellValueFactory(new PropertyValueFactory<>("airConditioning"));
        
        TableColumn<Cabin, Boolean> terrace = new TableColumn<>("Terassi");
        terrace.setCellValueFactory(new PropertyValueFactory<>("terrace"));
        
        TableColumn<Cabin, Boolean> sheets = new TableColumn<>("Liinavaatteet");
        sheets.setCellValueFactory(new PropertyValueFactory<>("sheets"));
        
        TableColumn<Cabin, Boolean> reserved = new TableColumn<>("Varattu");
        reserved.setCellValueFactory(new PropertyValueFactory<>("reserved"));
        
        TableColumn<Cabin, Integer> bedAmount = new TableColumn<>("Sängyt");
        bedAmount.setCellValueFactory(new PropertyValueFactory<>("bedAmount"));
        
        TableColumn<Cabin, Integer> wcAmount = new TableColumn<>("Vessat");
        wcAmount.setCellValueFactory(new PropertyValueFactory<>("wcAmount"));
        
        TableColumn<Cabin, String> summary = new TableColumn<>("Tiivistelmä");
        summary.setCellValueFactory(new PropertyValueFactory<>("summary"));
        

        // Columns to table
        cabinTable.getColumns().addAll(
            cabinId, customerId, weeklyPrice, cabinAddress, 
            petsAllowed, airConditioning, terrace, sheets, 
            reserved, bedAmount, wcAmount, summary);

        List<Cabin> cabins = Cabin.getCabinsFromDatabase();

        ObservableList<Cabin> data = FXCollections.observableArrayList(cabins);
        cabinTable.setItems(data);

        mainLayout.setCenter(cabinTable);

        Button backButton = new Button("Takaisin");
        backButton.setOnAction(e -> showMainMenu());

        mainLayout.setTop(backButton);

        System.out.println("Cabin Management opened.");
    }

    private void showReservationManagement() 
    {
        System.out.println("Reservation Management opened.");
    }

    private void showCustomerManagement() 
    {
        System.out.println("Customer Management opened.");
    }

    private void showBillingManagement() 
    {
        System.out.println("Billing Management opened.");
    }

    private void showAccommodationReports() 
    {
        System.out.println("Accommodation Reports opened.");
    }

}
