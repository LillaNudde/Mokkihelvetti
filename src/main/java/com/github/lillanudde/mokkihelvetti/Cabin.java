package com.github.lillanudde.mokkihelvetti;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Cabin
{

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

    public Cabin(
        int cabinId, int customerId, int weeklyPrice, String cabinAddress,
        boolean petsAllowed, boolean airConditioning, boolean terrace, boolean sheets,
        boolean reserved, int bedAmount, int wcAmount, String summary
        ){
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

        public static List<Cabin> getCabinsFromDatabase() 
        {
            List<Cabin> cabins = new ArrayList<>();
            String sql = "SELECT * FROM Mökit";
        
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\Documents\\Mökkihelvetti\\database.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) 
            {
        
                // Loop through table
                while (rs.next()) {
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
}


    

