package com.github.lillanudde.mokkihelvetti;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Cabin
{
    // Fields
    private int cabinId;
    private int customerId;
    private int weeklyPrice;
    private String cabinAddress;
    private boolean petsAllowed;
    private boolean airConditioning;
    private boolean terrace;
    private boolean sheets;
    private boolean reserved;
    private int bedAmount;
    private int wcAmount;
    private String summary;
    // Database address "jdbc:sqlite:DISK:\\Path\\To\\File.db"
    private static final String DB_URL = "jdbc:sqlite:D:\\Documents\\Mökkihelvetti\\database.db";

    // Getters & Setters
    public int getCabinId() 
    {
        return cabinId;
    }

    public void setCabinId(int cabinId) 
    {
        this.cabinId = cabinId;
    }

    public int getCustomerId() 
    {
        return customerId;
    }

    public void setCustomerId(int customerId) 
    {
        this.customerId = customerId;
    }

    public int getWeeklyPrice() 
    {
        return weeklyPrice;
    }

    public void setWeeklyPrice(int weeklyPrice) 
    {
        this.weeklyPrice = weeklyPrice;
    }

    public String getCabinAddress() 
    {
        return cabinAddress;
    }

    public void setCabinAddress(String cabinAddress) 
    {
        this.cabinAddress = cabinAddress;
    }

    public boolean isPetsAllowed() 
    {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) 
    {
        this.petsAllowed = petsAllowed;
    }

    public boolean isAirConditioning() 
    {
        return airConditioning;
    }

    public void setAirConditioning(boolean airConditioning) 
    {
        this.airConditioning = airConditioning;
    }

    public boolean isTerrace() 
    {
        return terrace;
    }

    public void setTerrace(boolean terrace) 
    {
        this.terrace = terrace;
    }

    public boolean isSheets() 
    {
        return sheets;
    }

    public void setSheets(boolean sheets) 
    {
        this.sheets = sheets;
    }

    public boolean isReserved() 
    {
        return reserved;
    }

    public void setReserved(boolean reserved) 
    {
        this.reserved = reserved;
    }

    public int getBedAmount() 
    {
        return bedAmount;
    }

    public void setBedAmount(int bedAmount) 
    {
        this.bedAmount = bedAmount;
    }

    public int getWcAmount() 
    {
        return wcAmount;
    }

    public void setWcAmount(int wcAmount)
    {
        this.wcAmount = wcAmount;
    }

    public String getSummary() 
    {
        return summary;
    }

    public void setSummary(String summary) 
    {
        this.summary = summary;
    }

    // Constructor
    public Cabin
    (
        int cabinId, int customerId, int weeklyPrice, String cabinAddress,
        boolean petsAllowed, boolean airConditioning, boolean terrace, boolean sheets,
        boolean reserved, int bedAmount, int wcAmount, String summary
    )
    {
        this.cabinId = cabinId;
        this.customerId = customerId;
        this.weeklyPrice = weeklyPrice;
        this.cabinAddress = cabinAddress;
        this.petsAllowed = petsAllowed;
        this.airConditioning = airConditioning;
        this.terrace = terrace;
        this.sheets = sheets;
        this.reserved = reserved;
        this.bedAmount = bedAmount;
        this.wcAmount = wcAmount;
        this.summary = summary;
    }

    // Getter for lowest available Cabin ID
    public static int getNewId() throws SQLException 
    {
        String sql = "SELECT Huone_id FROM Mökit ORDER BY Huone_id ASC";
        Set<Integer> existingIds = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) 
            {
            while (rs.next()) 
            {
                existingIds.add(rs.getInt("Huone_id"));
            }
            }

        int id = 1;
        while (existingIds.contains(id)) 
        {
            id++;
        }
        return id;
    }

    // Getter for every Cabin in Mökit table
    public static List<Cabin> getCabinsFromDatabase() 
    {
            List<Cabin> cabins = new ArrayList<>();
            String sql = "SELECT * FROM Mökit";
        
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) 
            {
        
                // Loop through table
                while (rs.next()) 
                {
                    int cabinId = rs.getInt("huone_id");
                    int customerId = rs.getInt("Asiakas_id");
                    int weeklyPrice = rs.getInt("ViikkoHinta");
                    String cabinAddress = rs.getString("Osoite");
                    boolean petsAllowed = rs.getBoolean("Sallitaanko_lemmikit");
                    boolean airConditioning = rs.getBoolean("Ilmastointi");
                    boolean terrace = rs.getBoolean("Terassi");
                    boolean sheets = rs.getBoolean("Liinavaatteet");
                    boolean reserved = rs.getBoolean("Varattu");
                    int bedAmount = rs.getInt("Montako_sänkyä");
                    int wcAmount = rs.getInt("Montako_vessa");
                    String summary = rs.getString("tiivistelmä");
        
                    // Create Cabin object and add to list
                    Cabin cabin = new Cabin(cabinId, customerId, weeklyPrice, cabinAddress, petsAllowed, airConditioning,
                            terrace, sheets, reserved, bedAmount, wcAmount, summary);
                    cabins.add(cabin);
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
            return cabins;
        }
        
    // Method for updating Cabin information
    public static void updateCabinInDatabase(Cabin cabin)
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE Mökit SET ViikkoHinta=?, Osoite=?, Sallitaanko_lemmikit=?, Ilmastointi=?, Terassi=?, Liinavaatteet=?, Varattu=?, Montako_sänkyä=?, Montako_vessa=?, tiivistelmä=? WHERE Huone_id=?"
        ))
        {
            stmt.setInt(1, cabin.getWeeklyPrice());
            stmt.setString(2, cabin.getCabinAddress());
            stmt.setBoolean(3, cabin.isPetsAllowed());
            stmt.setBoolean(4, cabin.isAirConditioning());
            stmt.setBoolean(5, cabin.isTerrace());
            stmt.setBoolean(6, cabin.isSheets());
            stmt.setBoolean(7, cabin.isReserved());
            stmt.setInt(8, cabin.getBedAmount());
            stmt.setInt(9, cabin.getWcAmount());
            stmt.setString(10, cabin.getSummary());
            stmt.setInt(11, cabin.getCabinId());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to create new Cabin
    public static void addCabinToDatabase(Cabin cabin) throws SQLException
    {
        String sql = "INSERT INTO Mökit (Huone_id, Asiakas_id, ViikkoHinta, Osoite, Sallitaanko_lemmikit, Ilmastointi, Terassi, Liinavaatteet, Varattu, Montako_sänkyä, Montako_vessa, tiivistelmä)" +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, cabin.getCabinId());
            stmt.setInt(2, cabin.getCustomerId());
            stmt.setInt(3, cabin.getWeeklyPrice());
            stmt.setString(4, cabin.getCabinAddress());
            stmt.setBoolean(5, cabin.isPetsAllowed());
            stmt.setBoolean(6, cabin.isAirConditioning());
            stmt.setBoolean(7, cabin.isTerrace());
            stmt.setBoolean(8, cabin.isSheets());
            stmt.setBoolean(9, cabin.isReserved());
            stmt.setInt(10, cabin.getBedAmount());
            stmt.setInt(11, cabin.getWcAmount());
            stmt.setString(12, cabin.getSummary());

            stmt.executeUpdate();
        }
    }

    // Method to remove Cabin
    public static void removeCabinFromDatabase(Cabin cabin) throws SQLException
    {
        String sql = "DELETE FROM Mökit WHERE Huone_id=?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, cabin.getCabinId());
            stmt.executeUpdate();
        }
    }

    
}


    

