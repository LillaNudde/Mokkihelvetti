package com.github.lillanudde.mokkihelvetti;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
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
        // Clear the window
        mainLayout.setTop(null);
        mainLayout.setCenter(null);

        // Create VBox with buttons and their functions
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
        mainLayout.setCenter(null);

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

        Button modifyButton = new Button("Muokkaa");
        modifyButton.setOnAction(e ->
        {
            Cabin selectedCabin = cabinTable.getSelectionModel().getSelectedItem();
            if (selectedCabin != null)
            {
                openCabinEditWindow(selectedCabin, data);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Valitse rivi muokattavaksi");
                alert.showAndWait();
            }
        });


        Button addButton = new Button("Lisää");
        addButton.setOnAction(e -> openCabinAddWindow());

        Button deleteButton = new Button("Poista");
        deleteButton.setOnAction(e ->
        {
            Cabin selectedCabin = cabinTable.getSelectionModel().getSelectedItem();

            if (selectedCabin == null)
            {
                Alert error = new Alert(Alert.AlertType.WARNING, "Valitse Mökki poistaaksesi");
                error.showAndWait();
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista Poisto");
            confirmation.setHeaderText("Oletko varma, että haluat poistaa mökin?");
            confirmation.setContentText("Poistoa EI VOI perua");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        Cabin.removeCabinFromDatabase(selectedCabin);

                        cabinTable.getItems().remove(selectedCabin);
                    }
                    
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Alert error = new Alert(Alert.AlertType.ERROR, "Virhe poistamisessa");
                        error.showAndWait();
                    }
                }
                    
            });

        });



        HBox topButtonBox = new HBox(backButton, modifyButton, addButton, deleteButton);

        mainLayout.setTop(topButtonBox);

        System.out.println("Cabin Management opened.");
    }

    private void openCabinEditWindow(Cabin cabin, ObservableList<Cabin> tableData)
    {
        // Pop-up window
        Stage window = new Stage();
        window.setTitle("Muokkaa mökin tietoja");

        // Modifiable fields
        TextField priceField = new TextField(String.valueOf(cabin.getWeeklyPrice()));
        TextField addressField = new TextField(String.valueOf(cabin.getCabinAddress()));
        CheckBox petBox = new CheckBox("Lemmikit Sallittu");
        petBox.setSelected(cabin.isPetsAllowed());
        CheckBox airConBox = new CheckBox("Ilmastointi");
        airConBox.setSelected(cabin.isAirConditioning());
        CheckBox terraceBox = new CheckBox("Terassi");
        terraceBox.setSelected(cabin.isTerrace());
        CheckBox sheetBox = new CheckBox("Liinavaatteet");
        sheetBox.setSelected(cabin.isSheets());
        CheckBox reservationBox = new CheckBox("Varattu");
        reservationBox.setSelected(cabin.isReserved());
        TextField bedField = new TextField(String.valueOf(cabin.getBedAmount()));
        TextField wcField = new TextField(String.valueOf(cabin.getWcAmount()));
        TextField summaryField = new TextField(String.valueOf(cabin.getSummary()));

        // Save button
        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista muutokset");
            confirmation.setHeaderText("Oletko varma, että haluat tallentaa muutokset?");
            confirmation.setContentText("Tallennusta EI VOI perua!");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        cabin.setWeeklyPrice(Integer.parseInt(priceField.getText()));
                        cabin.setCabinAddress(addressField.getText());
                        cabin.setPetsAllowed(petBox.isSelected());
                        cabin.setAirConditioning(airConBox.isSelected());
                        cabin.setTerrace(terraceBox.isSelected());
                        cabin.setSheets(sheetBox.isSelected());
                        cabin.setReserved(reservationBox.isSelected());
                        cabin.setBedAmount(Integer.parseInt(bedField.getText()));
                        cabin.setWcAmount(Integer.parseInt(wcField.getText()));
                        cabin.setSummary(summaryField.getText());
        
                        // Add SQL DB Update here
                        Cabin.updateCabinInDatabase(cabin);
        
                        // Close edit window and refresh table
                        window.close();
                        showCabinManagement(); // .refresh NOT POSSIBLE WITHOUT FUCKERY
        
                    }
                    catch (NumberFormatException nfe)
                    {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Jokin kenttä sisältää virheellistä tietoa.");
                        error.showAndWait();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Alert error = new Alert(Alert.AlertType.ERROR, "Virhe Tietojen Tallennuksessa.");
                        error.showAndWait();
                    }
                }
            });


        });

        VBox layout = new VBox(
            priceField, addressField, petBox, 
            airConBox, terraceBox, sheetBox,
            reservationBox, bedField, wcField, 
            summaryField, saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();

    }

    private void openCabinAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää Mökki");


        int newId;
        try
        {
            newId = Cabin.getNewId();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Tietokantavirhe ID haussa");
            error.showAndWait();
            return;
        }

        TextField customerField = new TextField();
        TextField priceField = new TextField();
        TextField addressField = new TextField();
        CheckBox petBox = new CheckBox("Lemmikit Sallittu");
        CheckBox airConBox = new CheckBox("Ilmastointi");
        CheckBox terraceBox = new CheckBox("Terassi");
        CheckBox sheetBox = new CheckBox("Liinavaatteet");
        CheckBox reservationBox = new CheckBox("Varattu");
        TextField bedField = new TextField();
        TextField wcField = new TextField();
        TextField summaryField = new TextField();

        customerField.setPromptText("Asiakas ID");
        priceField.setPromptText("Viikkohinta");
        addressField.setPromptText("Osoite");
        bedField.setPromptText("Sänkyjen määrä");
        wcField.setPromptText("Vessojen määrä");
        summaryField.setPromptText("Tiivistelmä");

        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            try
            {
                Cabin newCabin = new Cabin(
                    newId,
                    Integer.parseInt(customerField.getText()),
                    Integer.parseInt(priceField.getText()),
                    addressField.getText(),
                    petBox.isSelected(),
                    airConBox.isSelected(),
                    terraceBox.isSelected(),
                    sheetBox.isSelected(),
                    reservationBox.isSelected(),
                    Integer.parseInt(bedField.getText()),
                    Integer.parseInt(wcField.getText()),
                    summaryField.getText()
                    );

                    Cabin.addCabinToDatabase(newCabin);

                    window.close();
                    showCabinManagement();
                    
            }
            catch (NumberFormatException ex)
            {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Jokin kenttä sisältää virheellistä tietoa");
                error.showAndWait();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Virhe Tietojen Tallennuksessa");
                error.showAndWait();
            }
        });

        VBox layout = new VBox(
            new Label("Asiakas ID:"), customerField,
            new Label("Viikkohinta:"), priceField,
            new Label("Osoite:"), addressField,
            petBox, airConBox, terraceBox, sheetBox, reservationBox,
            new Label ("Sängyt:"), bedField,
            new Label("Vessat:"), wcField,
            new Label("Tiivistelmä:"), summaryField,
            saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();

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
