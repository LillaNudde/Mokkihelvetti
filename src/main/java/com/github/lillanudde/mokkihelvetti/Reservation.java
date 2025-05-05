package com.github.lillanudde.mokkihelvetti;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.SimpleDateFormat;

public class Reservation 
{
    private int reservationId;
    private int customerId;
    private int cabinId;
    private Date startDate;
    private Date endDate;
    private Date creationDate;
    private Date updateDate;
    // Database address "jdbc:sqlite:DISK:\\Path\\To\\File.db"
    private static final String DB_URL = "jdbc:sqlite:D:\\Documents\\Mökkihelvetti\\database.db";

    public int getReservationId() 
    {
        return reservationId;
    }
    public void setReservationId(int reservationId) 
    {
        this.reservationId = reservationId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) 
    {
        this.customerId = customerId;
    }
    public int getCabinId() 
    {
        return cabinId;
    }
    public void setCabinId(int cabinId) 
    {
        this.cabinId = cabinId;
    }
    public Date getStartDate() 
    {
        return startDate;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }
    public Date getEndDate() 
    {
        return endDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }
    public Date getCreationDate() 
    {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) 
    {
        this.creationDate = creationDate;
    }
    public Date getUpdateDate() 
    {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Reservation
    (
        int reservationId, int customerId, int cabinId, Date startDate, 
        Date endDate, Date creationDate, Date updateDate
    ) 
    {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.cabinId = cabinId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
    }

    // Getter for lowest available Reservation ID
    public static int getNewId() throws SQLException
    {
        String sql = "SELECT Varaus_Id FROM Varaukset ORDER BY Varaus_Id ASC";
        Set<Integer> existingIds = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                existingIds.add(rs.getInt("Varaus_Id"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        int id = 1;
        while (existingIds.contains(id))
        {
            id++;
        }
        return id;
    }

    // Getter for every Reservation in Varaukset table
    public static List<Reservation> getReservationsFromDatabase() 
    {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Varaukset";
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
    
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            // Loop through table
            while (rs.next()) {
                int reservationId = rs.getInt("Varaus_Id");
                int customerId = rs.getInt("Asiakas_id");
                int cabinId = rs.getInt("Huone_id");
    
                // Retrieve the date as a string and parse manually
                String startDateString = rs.getString("Alku_päivä");
                String endDateString = rs.getString("Loppu_päivä");
                String creationDateString = rs.getString("Luomispäivä");
                String updateDateString = rs.getString("Päivityspäivä");
    
                // Parse dates using SimpleDateFormat
                java.sql.Date startDate = startDateString != null ? new java.sql.Date(dateFormat.parse(startDateString).getTime()) : null;
                java.sql.Date endDate = endDateString != null ? new java.sql.Date(dateFormat.parse(endDateString).getTime()) : null;
                java.sql.Date creationDate = creationDateString != null ? new java.sql.Date(dateFormat.parse(creationDateString).getTime()) : null;
                java.sql.Date updateDate = updateDateString != null ? new java.sql.Date(dateFormat.parse(updateDateString).getTime()) : null;
    
                // Add to reservations list
                Reservation reservation = new Reservation(reservationId, customerId, cabinId, startDate,
                                                          endDate, creationDate, updateDate);
                reservations.add(reservation);
            }
        } catch (SQLException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    
    // Method for updating Reservation information
    public static void updateReservationInDatabase(Reservation reservation)
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE Varaukset SET Asiakas_id=?, Huone_id=?, Alku_päivä=?, Loppu_päivä=?, Luomispäivä=?, Päivityspäivä=? WHERE Varaus_Id=?"
        ))
        {
            stmt.setInt(1, reservation.getCustomerId());
            stmt.setInt(2, reservation.getCabinId());
            stmt.setDate(3, reservation.getStartDate());
            stmt.setDate(4, reservation.getEndDate());
            stmt.setDate(5, reservation.getCreationDate());
            stmt.setDate(6, reservation.getUpdateDate());
            stmt.setInt(7, reservation.getReservationId());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to create new Reservation
    public static void addReservationToDatabase(Reservation reservation) throws SQLException
    {
        String sql = "INSERT INTO Varaukset (Varaus_Id, Asiakas_id, Huone_id, Alku_päivä, Loppu_päivä, Luomispäivä, Päivityspäivä)" +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, reservation.getReservationId());
            stmt.setInt(2, reservation.getCustomerId());
            stmt.setInt(3, reservation.getCabinId());
            stmt.setDate(4, reservation.getStartDate());
            stmt.setDate(5, reservation.getEndDate());
            stmt.setDate(6, reservation.getCreationDate());
            stmt.setDate(7, reservation.getUpdateDate());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to remove Reservation
    public static void removeReservationFromDatabase(Reservation reservation)
    {
        String sql = "DELETE FROM Varaukset WHERE Varaus_Id=?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, reservation.getReservationId());
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
