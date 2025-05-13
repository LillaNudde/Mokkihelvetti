package com.github.lillanudde.mokkihelvetti;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Invoice 
{
    private int invoiceId;
    private int reservationId;
    private int clientId;
    private String invoiceType;
    private int invoicePrice;
    private Date invoiceDueDate;
    // Database address "jdbc:sqlite:DISK:\\Path\\To\\File.db"
    private static final String DB_URL = "jdbc:sqlite:C:\\Mökkihelvetti\\database.db";

    public int getInvoiceId() 
    {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) 
    {
        this.invoiceId = invoiceId;
    }

    public int getReservationId() 
    {
        return reservationId;
    }

    public void setReservationId(int reservationId) 
    {
        this.reservationId = reservationId;
    }

    public int getClientId() 
    {
        return clientId;
    }

    public void setClientId(int clientId) 
    {
        this.clientId = clientId;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) 
    {
        this.invoiceType = invoiceType;
    }

    public int getInvoicePrice() 
    {
        return invoicePrice;
    }

    public void setInvoicePrice(int invoicePrice)
    {
        this.invoicePrice = invoicePrice;
    }

    public Date getInvoiceDueDate() 
    {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(Date invoiceDueDate) 
    {
        this.invoiceDueDate = invoiceDueDate;
    }

    public Invoice
    (
        int invoiceId,
        int reservationId,
        int clientId, 
        String invoiceType, 
        int invoicePrice,
        Date invoiceDueDate
    ) 
    {
        this.invoiceId = invoiceId;
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.invoiceType = invoiceType;
        this.invoicePrice = invoicePrice;
        this.invoiceDueDate = invoiceDueDate;
    }
    
    // Getter for lowest available Invoice ID
    public static int getNewId() throws SQLException 
    {
        String sql = "SELECT LaskuID FROM Laskut ORDER BY LaskuID ASC";
        Set<Integer> existingIds = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) 
            {
            while (rs.next()) 
            {
                existingIds.add(rs.getInt("LaskuID"));
            }
            }

        int id = 1;
        while (existingIds.contains(id)) 
        {
            id++;
        }
        return id;
    }
    
    // Getter for every Invoice in Laskut table
    public static List<Invoice> getInvoicesFromDatabase() 
    {
            List<Invoice> invoices = new ArrayList<>();
            String sql = "SELECT * FROM Laskut";
        
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) 
            {
        
                // Loop through table
                while (rs.next()) 
                {
                    int invoiceId = rs.getInt("LaskuID");
                    int reservationId = rs.getInt("Varaus_id");
                    int clientId = rs.getInt("Asiakas_id");
                    String invoiceType = rs.getString("Laskuntyyppi");
                    int invoicePrice = rs.getInt("Laskunsumma");

                    String invoiceDueDateSring = rs.getString("ViimeinenMaksuPäivä");
                    Date invoiceDueDate = StringToDate(invoiceDueDateSring);
        
                    // Create Invoice object and add to list
                    Invoice invoice = new Invoice(invoiceId, reservationId, clientId, invoiceType, invoicePrice, invoiceDueDate);
                    invoices.add(invoice);
                }
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
            return invoices;
        }

    // Method for updating Invoice information
    public static void updateInvoiceInDatabase(Invoice invoice)
    {
        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE Laskut SET Varaus_id=?, Asiakas_id=?, Laskuntyyppi=?, Laskunsumma=?, ViimeinenMaksuPäivä=? WHERE LaskuID=?"
        ))
        {
            stmt.setInt(1, invoice.getReservationId());
            stmt.setInt(2, invoice.getClientId());
            stmt.setString(3, invoice.getInvoiceType());
            stmt.setInt(4, invoice.getInvoicePrice());
            stmt.setString(5, DateToString(invoice.getInvoiceDueDate()));
            stmt.setInt(6, invoice.getInvoiceId());

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Method to create new Invoice
    public static void addInvoiceToDatabase(Invoice invoice) throws SQLException
    {
        String sql = "INSERT INTO Laskut (LaskuID, Varaus_id, Asiakas_id, Laskuntyyppi, Laskunsumma, ViimeinenMaksuPäivä)" +
        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, invoice.getInvoiceId());
            stmt.setInt(2, invoice.getReservationId());
            stmt.setInt(3, invoice.getClientId());
            stmt.setString(4, invoice.getInvoiceType());
            stmt.setInt(5, invoice.getInvoicePrice());
            stmt.setString(6, DateToString(invoice.getInvoiceDueDate()));

            stmt.executeUpdate();
        }
    }

    // Method to remove Invoice
    public static void removeInvoiceFromDatabase(Invoice invoice) throws SQLException
    {
        String sql = "DELETE FROM Laskut WHERE LaskuID=?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
        PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, invoice.getInvoiceId());
            stmt.executeUpdate();
        }
    }

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String DateToString(Date date)
    {
        String formattedDate = "";
        try
        {
            formattedDate = formatter.format(date);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return formattedDate;
    }

    public static java.sql.Date StringToDate(String date) 
    {
        if (date == null || date.isEmpty()) 
        {
            return new java.sql.Date(System.currentTimeMillis());
        }

        try 
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = formatter.parse(date);
            return new java.sql.Date(utilDate.getTime());
        } catch (Exception e) 
        {
            e.printStackTrace();
            return new java.sql.Date(System.currentTimeMillis());
        }
    }    

}
