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

public class Report
{
    private int reportId;
    private int reservationId;
    private String summary;
    // Database address "jdbc:sqlite:DISK:\\Path\\To\\File.db"
    private static final String DB_URL = "jdbc:sqlite:C:\\Mökkihelvetti\\database.db";

    public int getReportId() 
    {
        return reportId;
    }

    public void setReportId(int reportId) 
    {
        this.reportId = reportId;
    }

    public int getReservationId() 
    {
        return reservationId;
    }

    public void setReservationId(int reservationId) 
    {
        this.reservationId = reservationId;
    }

    public String getSummary() 
    {
        return summary;
    }

    public void setSummary(String summary) 
    {
        this.summary = summary;
    }

    public Report(int reportId, int reservationId, String summary) 
    {
        this.reportId = reportId;
        this.reservationId = reservationId;
        this.summary = summary;
    }

    // Getter for lowest available Report ID
    public static int getNewId() throws SQLException 
    {
        String sql = "SELECT Raportti_id FROM Raportit ORDER BY Raportti_id ASC";
        Set<Integer> existingIds = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) 
            {
            while (rs.next()) 
            {
                existingIds.add(rs.getInt("Raportti_id"));
            }
            }

        int id = 1;
        while (existingIds.contains(id)) 
        {
            id++;
        }
        return id;
    }

    // Getter for every Report in Raportit table
    public static List<Report> getReportsFromDatabase() 
    {
            List<Report> reports = new ArrayList<>();
            String sql = "SELECT * FROM Raportit";
        
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) 
            {
        
                // Loop through table
                while (rs.next()) 
                {
                    int reportId = rs.getInt("Raportti_id");
                    int reservationId = rs.getInt("Varaus_id");
                    String summary = rs.getString("Raportintiivistelmä");

        
                    // Create Report object and add to list
                    Report report = new Report(reportId, reservationId, summary);
                    reports.add(report);
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
            return reports;
        }
        
    // Method for updating Report information
    public static void updateReportInDatabase(Report report)
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE Raportit SET Varaus_id=?, Raportintiivistelmä=? WHERE Raportti_id=?"
        ))
        {
            stmt.setInt(1, report.getReservationId());
            stmt.setString(2, report.getSummary());
            stmt.setInt(3, report.getReportId());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to create new Report
    public static void addReportToDatabase(Report report) throws SQLException
    {
        String sql = "INSERT INTO Raportit (Raportti_id, Varaus_id, Raportintiivistelmä)" +
        "VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, report.getReportId());
            stmt.setInt(2, report.getReservationId());
            stmt.setString(3, report.getSummary());

            stmt.executeUpdate();
        }
    }

    // Method to remove Report
    public static void removeReportFromDatabase(Report report) throws SQLException
    {
        String sql = "DELETE FROM Raportit WHERE Raportti_id=?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, report.getReportId());
            stmt.executeUpdate();
        }
    }

}
