package com.github.lillanudde.mokkihelvetti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Client 
{
    private int clientId;
    private String name;
    private String email;
    private boolean corporate;
    private String corporateId;
    private String phoneNumber;
    // Database address "jdbc:sqlite:DISK:\\Path\\To\\File.db"
    private static final String DB_URL = "jdbc:sqlite:C:\\Mökkihelvetti\\database.db";

    public int getClientId() 
    {
        return clientId;
    }

    public void setClientId(int clientId) 
    {
        clientId = clientId;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public Boolean getCorporate() 
    {
        return corporate;
    }

    public void setCorporate(Boolean corporate) 
    {
        this.corporate = corporate;
    }

    public String getCorporateId() 
    {
        return corporateId;
    }

    public void setCorporateId(String corporateId)
    {
        this.corporateId = corporateId;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public Client(int clientId, String name, String email, Boolean corporate, String corporateId, String phoneNumber) 
    {
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.corporate = corporate;
        this.corporateId = corporateId;
        this.phoneNumber = phoneNumber;
    }

    // Getter for lowest available Client ID
    public static int getNewId() throws SQLException 
    {
        String sql = "SELECT Asiakas_id FROM Asiakkaat ORDER BY Asiakas_id ASC";
        Set<Integer> existingIds = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) 
            {
            while (rs.next()) 
            {
                existingIds.add(rs.getInt("Asiakas_id"));
            }
            }

        int id = 1;
        while (existingIds.contains(id)) 
        {
            id++;
        }
        return id;
    }
    
    // Getter for every Client in Asiakkaat table
    public static List<Client> getClientsFromDatabase() 
    {
            List<Client> clients = new ArrayList<>();
            String sql = "SELECT * FROM Asiakkaat";
        
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) 
            {
        
                // Loop through table
                while (rs.next()) 
                {
                    int clientId = rs.getInt("Asiakas_id");
                    String name = rs.getString("Nimi");
                    String email = rs.getString("Sähköposti");
                    boolean corporate = rs.getBoolean("Yritysasiakas");
                    String corporateId = rs.getString("Ytunnus");
                    String phoneNumber = rs.getString("Kontaktihenkilönpuhelinnumero");
        
                    // Create Client object and add to list
                    Client client = new Client(clientId, name, email, corporate, corporateId, phoneNumber);
                    clients.add(client);
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
            return clients;
        }

    // Method for updating Client information
    public static void updateClientInDatabase(Client client)
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE Asiakkaat SET Nimi=?, Sähköposti=?, Yritysasiakas=?, Ytunnus=?, Kontaktihenkilönpuhelinnumero=? WHERE Asiakas_id=?"
        ))
        {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setBoolean(3, client.getCorporate());
            stmt.setString(4, client.getCorporateId());
            stmt.setString(5, client.getPhoneNumber());
            stmt.setInt(6, client.getClientId());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to create new Client
    public static void addClientToDatabase(Client client) throws SQLException
    {
        String sql = "INSERT INTO Asiakkaat (Asiakas_id, Nimi, Sähköposti, Yritysasiakas, Ytunnus, Kontaktihenkilönpuhelinnumero)" +
        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, client.getClientId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getEmail());
            stmt.setBoolean(4, client.getCorporate());
            stmt.setString(5, client.getCorporateId());
            stmt.setString(6, client.getPhoneNumber());

            stmt.executeUpdate();
        }
    }

    // Method to remove Client
    public static void removeClientFromDatabase(Client client) throws SQLException
    {
        String sql = "DELETE FROM Asiakkaat WHERE Asiakas_id=?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, client.getClientId());
            stmt.executeUpdate();
        }
    }

    
}
