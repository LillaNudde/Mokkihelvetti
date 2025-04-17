package com.github.lillanudde.mokkihelvetti;

public class Cabin
{
    private int cabinID;
    private int privateID;
    private int businessID;
    private double Price;
    private String Address;
    private boolean petsAllowed;
    private boolean hasAC;
    private boolean hasTerrace;
    private boolean hasSheets;
    private boolean isReserved;
    private int bedAmount;
    private int toiletAmount;
    private String description;

    public Cabin(int cabinID, int privateID, int businessID, double Price, String Address, boolean petsAllowed, boolean hasAC, boolean hasTerrace, boolean hasSheets, boolean isReserved, int bedAmount, int toiletAmount, String description)
    {
        this.cabinID = cabinID;
        this.privateID = privateID;
        this.businessID = businessID;
        this.Price = Price;
        this.Address = Address;
        this.petsAllowed = petsAllowed;
        this.hasAC = hasAC;
        this.hasTerrace = hasTerrace;
        this.hasSheets = hasSheets;
        this.isReserved = isReserved;
        this.bedAmount = bedAmount;
        this.toiletAmount = toiletAmount;
        this.description = description;
    }

    
}
