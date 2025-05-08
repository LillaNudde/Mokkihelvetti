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

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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
            createMenuButton("Asiakashallinta", this::showClientManagement),
            createMenuButton("Laskujen hallinta ja seuranta", this::showInvoiceManagement),
            createMenuButton("Majoittumisen raportointi", this::showAccommodationReports)
       );
       buttonBox.setSpacing(25);
       buttonBox.setAlignment(Pos.CENTER);

       mainLayout.setCenter(buttonBox);
    }

    // Cabin Management menu
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

        // Add Cabins to list
        List<Cabin> cabins = Cabin.getCabinsFromDatabase();
        ObservableList<Cabin> data = FXCollections.observableArrayList(cabins);
        cabinTable.setItems(data);

        // Show list
        mainLayout.setCenter(cabinTable);

        // Buttons
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


        // Show buttons
        HBox topButtonBox = new HBox(backButton, modifyButton, addButton, deleteButton);

        mainLayout.setTop(topButtonBox);

        System.out.println("Cabin Management opened.");
    }

    // Cabin edit window
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
        
                        // Update table in database
                        Cabin.updateCabinInDatabase(cabin);
        
                        // Close edit window and refresh table
                        window.close();
                        showCabinManagement(); // .refresh NOT POSSIBLE WITHOUT VOODOO
        
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

        // Show layout
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

    // Cabin add window
    private void openCabinAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää Mökki");

        // Get lowest available ID
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

        // Modifiable fields
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

        // Text for each text field
        customerField.setPromptText("Asiakas ID");
        priceField.setPromptText("Viikkohinta");
        addressField.setPromptText("Osoite");
        bedField.setPromptText("Sänkyjen määrä");
        wcField.setPromptText("Vessojen määrä");
        summaryField.setPromptText("Tiivistelmä");

        // Save button
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

        // Show layout
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

    // Reservation management menu
    private void showReservationManagement() 
    {

        // Clear the window
        mainLayout.setCenter(null);

        // TableView
        TableView<Reservation> reservationTable = new TableView<>();

        // Column hell
        TableColumn<Reservation, Integer> reservationId = new TableColumn<>("Varaus ID");
        reservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<Reservation, Integer> customerId = new TableColumn<>("Asiakas ID");
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Reservation, Integer> cabinId = new TableColumn<>("Mökki ID");
        cabinId.setCellValueFactory(new PropertyValueFactory<>("cabinId"));

        TableColumn<Reservation, Date> startDate = new TableColumn<>("Alkamispäivä");
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Reservation, Date> endDate = new TableColumn<>("Loppumispäivä");
        endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<Reservation, Date> creationDate = new TableColumn<>("Luomispäivä");
        creationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

        TableColumn<Reservation, Date> updateDate = new TableColumn<>("Päivityspäivä");
        updateDate.setCellValueFactory(new PropertyValueFactory<>("updateDate"));

        // Columns to table
        reservationTable.getColumns().addAll(
            reservationId, customerId, cabinId,
            startDate, endDate, creationDate, updateDate);

        // Add Reservations to list
        List<Reservation> reservations = Reservation.getReservationsFromDatabase();
        ObservableList<Reservation> data = FXCollections.observableArrayList(reservations);
        reservationTable.setItems(data);

        // Show list
        mainLayout.setCenter(reservationTable);

        // Buttons
        Button backButton = new Button("Takaisin");
        backButton.setOnAction(e -> showMainMenu());

        Button modifyButton = new Button("Muokkaa");
        modifyButton.setOnAction(e ->
        {
            Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null)
            {
                openReservationEditWindow(selectedReservation, data);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Valitse rivi muokattavaksi");
                alert.showAndWait();
            }
        });

        Button addButton = new Button("Lisää");
        addButton.setOnAction(e -> openReservationAddWindow());

        Button deleteButton = new Button("Poista");
        deleteButton.setOnAction(e ->
        {
            Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

            if (selectedReservation == null)
            {
                Alert error = new Alert(Alert.AlertType.WARNING, "Valitse Varaus poistaaksesi");
                error.showAndWait();
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista poisto");
            confirmation.setHeaderText("Oletko varma, että haluat poistaa varauksen?");
            confirmation.setContentText("Poistoa EI VOI perua");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        Reservation.removeReservationFromDatabase(selectedReservation);

                        reservationTable.getItems().remove(selectedReservation);
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

        // Show buttons
        HBox topButtonBox = new HBox(backButton, modifyButton, addButton, deleteButton);

        mainLayout.setTop(topButtonBox);

        System.out.println("Reservation Management opened.");
    }

    // Reservation edit window
    private void openReservationEditWindow(Reservation reservation, ObservableList<Reservation> tableData)
    {
        // Pop-up window
        Stage window = new Stage();
        window.setTitle("Muokkaa varauksen tietoja");

        // Modifiable fields
        TextField customerField = new TextField(String.valueOf(reservation.getCustomerId()));
        TextField cabinField = new TextField(String.valueOf(reservation.getCabinId()));
        TextField startField = new TextField(String.valueOf(reservation.getStartDate()));
        TextField endField = new TextField(String.valueOf(reservation.getEndDate()));

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
                        reservation.setCustomerId(Integer.parseInt(customerField.getText()));
                        reservation.setCabinId(Integer.parseInt(cabinField.getText()));
                        LocalDate localStart = LocalDate.parse(startField.getText()); // Parse to YYYY-MM-DD
                        Date sqlStart = Date.valueOf(localStart);                     // Convert to java.sql.Date
                        reservation.setStartDate(sqlStart);                           // Set with .sql. date
                        LocalDate localEnd = LocalDate.parse(endField.getText());
                        Date sqlEnd = Date.valueOf(localEnd);
                        reservation.setEndDate(sqlEnd);
                        LocalDate localUpdate = LocalDate.now();
                        Date sqlUpdate = Date.valueOf(localUpdate);
                        reservation.setUpdateDate(sqlUpdate);

                        // Update table in database
                        Reservation.updateReservationInDatabase(reservation);

                        // Close edit window and refresh table
                        window.close();
                        showReservationManagement(); // NOT EVEN TESTING .refresh
                    }
                    catch (NumberFormatException nfe)
                    {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Jokin kenttä sisältää virheellistä tietoa");
                        error.showAndWait();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Alert error = new Alert(Alert.AlertType.ERROR, "Virhe Tietojen Tallennuksessa");
                        error.showAndWait();
                    }
                }
            });
        });

        // Show layout
        VBox layout = new VBox(
            customerField, cabinField, startField, endField, saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    // Reservation add window
    private void openReservationAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää varaus");

        // Get lowest available ID
        int newId;
        try
        {
            newId = Reservation.getNewId();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Tietokantavirhe ID haussa");
            error.showAndWait();
            return;
        }

        // Modifiable fields
        TextField customerField = new TextField();
        TextField cabinField = new TextField();
        TextField startField = new TextField();
        TextField endField = new TextField();

        // Text for each field
        customerField.setPromptText("Asiakas ID");
        cabinField.setPromptText("Mökki ID");
        startField.setPromptText("Alkamispäivä (YYYY-MM-DD)");
        endField.setPromptText("Loppumispäivä (YYYY-MM-DD)");

        // Save button
        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            try
            {
                LocalDate localStart = LocalDate.parse(startField.getText());
                Date sqlStart = Date.valueOf(startField.getText());
                LocalDate localEnd = LocalDate.parse(endField.getText());
                Date sqlEnd = Date.valueOf(localEnd);
                LocalDate currentDate = LocalDate.now();
                Date sqlDate = Date.valueOf(currentDate);

                Reservation newReservation = new Reservation(
                    newId, 
                    Integer.parseInt(customerField.getText()), 
                    Integer.parseInt(cabinField.getText()),
                    sqlStart,
                    sqlEnd,
                    sqlDate,
                    sqlDate
                );

                Reservation.addReservationToDatabase(newReservation);

                window.close();
                showReservationManagement();
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
            }
        });

        // Show layout
        VBox layout = new VBox(
            new Label("Asiakas ID:"), customerField,
            new Label("Mökki ID:"), cabinField,
            new Label("Alkamispäivä (YYYY-MM-DD):"), startField,
            new Label("Loppumispäivä (YYYY-MM-DD):"), endField,
            saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    private void showClientManagement() 
    {
        // Clear the window
        mainLayout.setCenter(null);

        // Dataless TableView
        TableView<Client> clientTable = new TableView<>();

        // Column hell
        TableColumn<Client, Integer> clientId = new TableColumn<>("Asiakas ID");
        clientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        TableColumn<Client, String> name = new TableColumn<>("Nimi");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Client, String> email = new TableColumn<>("Sähköposti");
        email.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Client, Boolean> corporate = new TableColumn<>("Yritysasiakas");
        corporate.setCellValueFactory(new PropertyValueFactory<>("corporate"));

        TableColumn<Client, String> corporateId = new TableColumn<>("Y-Tunnus");
        corporateId.setCellValueFactory(new PropertyValueFactory<>("corporateId"));

        TableColumn<Client, String> phoneNumber = new TableColumn<>("Puhelinnumero");
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Columns to table
        clientTable.getColumns().addAll(
            clientId, name, email,
            corporate, corporateId, phoneNumber);

        // Add Clients to list
        List<Client> clients = Client.getClientsFromDatabase();
        ObservableList<Client> data = FXCollections.observableArrayList(clients);
        clientTable.setItems(data);

        // Show list
        mainLayout.setCenter(clientTable);

        // Buttons
        Button backButton = new Button("Takaisin");
        backButton.setOnAction(e -> showMainMenu());

        Button modifyButton = new Button("Muokkaa");
        modifyButton.setOnAction(e ->
        {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null)
            {
                openClientEditWindow(selectedClient, data);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Valitse rivi muokattavaksi");
                alert.showAndWait();
            }
        });

        Button addButton = new Button("Lisää");
        addButton.setOnAction(e -> openClientAddWindow());

        Button deleteButton = new Button("Poista");
        deleteButton.setOnAction(e ->
        {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();

            if (selectedClient == null)
            {
                Alert error = new Alert(Alert.AlertType.WARNING, "Valitse Asiakas poistaaksesi");
                error.showAndWait();
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista Poisto");
            confirmation.setHeaderText("Oletko varma, että haluat poistaa asiakkaan?");
            confirmation.setContentText("Poistoa EI VOI perua");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        Client.removeClientFromDatabase(selectedClient);

                        clientTable.getItems().remove(selectedClient);
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

        System.out.println("Customer Management opened.");
    }

    private void openClientEditWindow(Client client, ObservableList<Client> tableData)
    {
        // Pop-up window
        Stage window = new Stage();
        window.setTitle("Muokkaa asiakkaan tietoja");

        // Modifiable fields
        TextField nameField = new TextField(String.valueOf(client.getName()));
        TextField emailField = new TextField(String.valueOf(client.getEmail()));
        CheckBox corporateBox = new CheckBox("Yritysasiakas");
        corporateBox.setSelected(client.getCorporate());
        TextField corporateIdField = new TextField(String.valueOf(client.getCorporateId()));
        TextField phoneNumberField = new TextField(String.valueOf(client.getPhoneNumber()));

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
                        client.setName(nameField.getText());
                        client.setEmail(emailField.getText());
                        client.setCorporate(corporateBox.isSelected());
                        client.setCorporateId(corporateIdField.getText());
                        client.setPhoneNumber(phoneNumberField.getText());

                        // Update table in database
                        Client.updateClientInDatabase(client);

                        // Close edit window and refresh table
                        window.close();
                        showClientManagement(); // .refresh NOT POSSIBLE WITHOUT VOODOO
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

        // Show layout
        VBox layout = new VBox(
            nameField, emailField, corporateBox,
            corporateIdField, phoneNumberField, saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    // Cabin add window
    private void openClientAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää asiakas");

        // Get lowest available ID
        int newId;
        try
        {
            newId = Client.getNewId();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Tietokantavirhe ID haussa");
            error.showAndWait();
            return;
        }

        // Modifiable fields
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        CheckBox corporateBox = new CheckBox("Yritysasiakas");
        TextField corporateIdField = new TextField();
        TextField phoneNumberField = new TextField();

        // Text for each field
        nameField.setPromptText("Nimi");
        emailField.setPromptText("Sähköposti");
        corporateIdField.setPromptText("Y-Tunnus");
        phoneNumberField.setPromptText("Puhelinnumero");

        // Save button
        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            try
            {
                Client newClient = new Client(
                    newId,
                    nameField.getText(),
                    emailField.getText(),
                    corporateBox.isSelected(),
                    corporateIdField.getText(),
                    phoneNumberField.getText()
                );

                Client.addClientToDatabase(newClient);

                window.close();
                showClientManagement();
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

        // Show layout
        VBox layout = new VBox(
            new Label("Nimi:"), nameField,
            new Label("Sähköposti"), emailField,
            corporateBox,
            new Label("Y-Tunnus"), corporateIdField,
            new Label("Puhelinnumero"), phoneNumberField,
            saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    private void showInvoiceManagement() 
    {
        // Clear the window
        mainLayout.setCenter(null);

        // Dataless TableView
        TableView<Invoice> invoiceTable = new TableView<>();

        // Column hell
        TableColumn<Invoice, Integer> invoiceId = new TableColumn<>("Lasku ID");
        invoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));

        TableColumn<Invoice, Integer> reservationId = new TableColumn<>("Varaus ID");
        reservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<Invoice, Integer> clientId = new TableColumn<>("Asiakas ID");
        clientId.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        TableColumn<Invoice, String> invoiceType = new TableColumn<>("Laskun tyyppi");
        invoiceType.setCellValueFactory(new PropertyValueFactory<>("invoiceType"));

        TableColumn<Invoice, Integer> invoicePrice = new TableColumn<>("Laskun summa");
        invoicePrice.setCellValueFactory(new PropertyValueFactory<>("invoicePrice"));

        TableColumn<Invoice, Date> invoiceDueDate = new TableColumn<>("Eräpäivä");
        invoiceDueDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDueDate"));

        // Columns to table
        invoiceTable.getColumns().addAll(
            invoiceId, reservationId, clientId,
            invoiceType, invoicePrice, invoiceDueDate
        );

        // Add Invoices to list
        List<Invoice> invoices = Invoice.getInvoicesFromDatabase();
        ObservableList<Invoice> data = FXCollections.observableArrayList(invoices);
        invoiceTable.setItems(data);

        // Show list
        mainLayout.setCenter(invoiceTable);

        // Buttons
        Button backButton = new Button("Takaisin");
        backButton.setOnAction(e -> showMainMenu());

        Button modifyButton = new Button("Muokkaa");
        modifyButton.setOnAction(e ->
        {
            Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
            if (selectedInvoice != null)
            {
                openInvoiceEditWindow(selectedInvoice, data);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Valitse rivi muokattavaksi");
                alert.showAndWait();
            }
        });

        Button addButton = new Button("Lisää");
        addButton.setOnAction(e -> openInvoiceAddWindow());

        Button deleteButton = new Button("Poista");
        deleteButton.setOnAction(e ->
        {
            Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
            
            if (selectedInvoice == null)
            {
                Alert warning = new Alert(Alert.AlertType.WARNING, "Valitse Lasku poistaaksesi");
                warning.showAndWait();
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista Poisto");
            confirmation.setHeaderText("Oletko varma, että haluat poistaa laskun?");
            confirmation.setContentText("Poistoa EI VOI perua");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        Invoice.removeInvoiceFromDatabase(selectedInvoice);

                        invoiceTable.getItems().remove(selectedInvoice);
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

        // Show buttons
        HBox topButtonBox = new HBox(backButton, modifyButton, addButton, deleteButton);

        mainLayout.setTop(topButtonBox);


        System.out.println("Billing Management opened.");
    }

    private void openInvoiceEditWindow(Invoice invoice, ObservableList<Invoice> tableData)
    {
        // Pop-up window
        Stage window = new Stage();
        window.setTitle("Muokkaa laskun tietoja");

        // Modifiable fields
        TextField reservationField = new TextField(String.valueOf(invoice.getReservationId()));
        TextField clientField = new TextField(String.valueOf(invoice.getClientId()));
        TextField typeField = new TextField(String.valueOf(invoice.getInvoiceType()));
        TextField priceField = new TextField(String.valueOf(invoice.getInvoicePrice()));
        TextField dueDateField = new TextField(String.valueOf(invoice.getInvoiceDueDate()));

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
                        invoice.setReservationId(Integer.parseInt(reservationField.getText()));
                        invoice.setClientId(Integer.parseInt(clientField.getText()));
                        invoice.setInvoiceType(typeField.getText());
                        invoice.setInvoicePrice(Integer.parseInt(priceField.getText()));

                        LocalDate dueDate = LocalDate.parse(dueDateField.getText());
                        Date sqlDueDate = Date.valueOf(dueDate);
                        invoice.setInvoiceDueDate(sqlDueDate);

                        // Update table in database
                        Invoice.updateInvoiceInDatabase(invoice);

                        // Close edit window and refresh table
                        window.close();
                        showInvoiceManagement(); // VOODOOBAN
                    }
                    catch (NumberFormatException nfe)
                    {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Jokin kenttä sisältää virheellistä tietoa");
                        error.showAndWait();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Alert error = new Alert(Alert.AlertType.ERROR, "Virhe Tietojen Tallennuksessa");
                        error.showAndWait();
                    }
                }
            });
        });

        // Show layout
        VBox layout = new VBox(
            reservationField, clientField,
            typeField, priceField,
            dueDateField, saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();

    }

    // Invoice add window
    private void openInvoiceAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää lasku");

        // Get lowest available ID
        int newId;
        try
        {
            newId = Invoice.getNewId();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Tietokantavirhe ID haussa");
            error.showAndWait();
            return;
        }

        // Modifiable fields
        TextField reservationField = new TextField();
        TextField clientField = new TextField();
        TextField typeField = new TextField();
        TextField priceField = new TextField();
        TextField dueDateField = new TextField();

        // Text for each field
        reservationField.setPromptText("Varaus ID");
        clientField.setPromptText("Asiakas ID");
        typeField.setPromptText("Laskun tyyppi");
        priceField.setPromptText("Laskun summa");
        dueDateField.setPromptText("Eräpäivä (YYYY-MM-DD)");

        // Save button
        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            try
            {
                LocalDate dueDate = LocalDate.parse(dueDateField.getText());
                Date sqlDueDate = Date.valueOf(dueDate);

                Invoice newInvoice = new Invoice(
                    newId,
                    Integer.parseInt(reservationField.getText()),
                    Integer.parseInt(clientField.getText()),
                    typeField.getText(),
                    Integer.parseInt(priceField.getText()),
                    sqlDueDate
                );

                Invoice.addInvoiceToDatabase(newInvoice);

                window.close();
                showInvoiceManagement();
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
            }
        });

        // Show layout
        VBox layout = new VBox(
            new Label("Varaus ID:"), reservationField,
            new Label("Asiakas ID:"), clientField,
            new Label("Laskun tyyppi:"), typeField,
            new Label("Laskun summa:"), priceField,
            new Label("Eräpäivä (YYYY-MM-DD):"), dueDateField,
            saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    private void showAccommodationReports() 
    {

        // Cleart the window
        mainLayout.setCenter(null);

        // Dataless TableView
        TableView<Report> reportTable = new TableView<>();

        // Column heaven :)
        TableColumn<Report, Integer> reportId = new TableColumn("Raportti ID");
        reportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));

        TableColumn<Report, Integer> reservationId = new TableColumn<>("Varaus ID");
        reservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<Report, String> summary = new TableColumn<>("Tiivistelmä");
        summary.setCellValueFactory(new PropertyValueFactory<>("summary"));

        // Columns to table
        reportTable.getColumns().addAll(reportId, reservationId, summary);

        // Add Reports to list
        List<Report> reports = Report.getReportsFromDatabase();
        ObservableList<Report> data = FXCollections.observableArrayList(reports);
        reportTable.setItems(data);

        // Show list
        mainLayout.setCenter(reportTable);

        // Buttons
        Button backButton = new Button("Takaisin");
        backButton.setOnAction(e -> showMainMenu());

        Button modifyButton = new Button("Muokkaa");
        modifyButton.setOnAction(e ->
        {
            Report selectedReport = reportTable.getSelectionModel().getSelectedItem();
            if (selectedReport != null)
            {
                openReportEditWindow(selectedReport, data);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Valitse rivi muokattavaksi");
                alert.showAndWait();
            }
        });
        
        Button addButton = new Button("Lisää");
        addButton.setOnAction(e -> openReportAddWindow());

        Button deleteButton = new Button("Poista");
        deleteButton.setOnAction(e ->
        {
            Report selectedReport = reportTable.getSelectionModel().getSelectedItem();

            if (selectedReport == null)
            {
                Alert error = new Alert(Alert.AlertType.WARNING, "Valitse Asiakas poistaaksesi");
                error.showAndWait();
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Vahvista Poisto");
            confirmation.setHeaderText("Oletko varma, että haluat poistaa raportin?");
            confirmation.setContentText("Poistoa EI VOI perua");

            confirmation.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    try
                    {
                        Report.removeReportFromDatabase(selectedReport);

                        reportTable.getItems().remove(selectedReport);
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

        System.out.println("Accommodation Reports opened.");
    }

    private void openReportEditWindow(Report report, ObservableList<Report> tableData)
    {
        Stage window = new Stage();
        window.setTitle("Muokkaa raporttia");

        // Modifiable fields
        TextField reservationField = new TextField(String.valueOf(report.getReservationId()));
        TextField summaryField = new TextField(String.valueOf(report.getSummary()));

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
                        report.setReservationId(Integer.parseInt(reservationField.getText()));
                        report.setSummary(summaryField.getText());

                        // Update table in database
                        Report.updateReportInDatabase(report);

                        // Close edit window and refresh table
                        window.close();
                        showClientManagement(); // Voodooban
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

        summaryField.setPrefSize(250, 250);

        // Show layout
        VBox layout = new VBox(
            reservationField, summaryField, saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

    // Report add window
    private void openReportAddWindow()
    {
        Stage window = new Stage();
        window.setTitle("Lisää raportti");

        // Get lowest available ID
        int newId;
        try
        {
            newId = Report.getNewId();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Tietokantavirhe ID haussa");
            error.showAndWait();
            return;
        }

        // Modifiable fields
        TextField reservationField = new TextField();
        TextField summaryField = new TextField();

        // Text for each field
        reservationField.setPromptText("Varaus ID");
        summaryField.setPromptText("Tiivistelmä");

        // Save button
        Button saveButton = new Button("Tallenna");
        saveButton.setOnAction(e ->
        {
            try
            {
                Report newReport = new Report
                (
                    newId, 
                    Integer.parseInt(reservationField.getText()), 
                    summaryField.getText()
                );

                Report.addReportToDatabase(newReport);

                window.close();
                showAccommodationReports();
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

        summaryField.setPrefSize(250, 250);

        // Show layout
        VBox layout = new VBox(
            new Label("Varaus ID:"), reservationField,
            new Label("Tiivistelmä"), summaryField,
            saveButton
        );

        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        window.setScene(new Scene(layout, 400, 800));
        window.show();
    }

}
