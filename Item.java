/*
Base class for lost and found item report.
Stores the shared item details used for matching.
 */
public abstract class Item 
{

    //Separator used in the save files
    public static final String SEPARATOR = "|";

    //Instance Variables
    private String itemType; // "water bottle", "jacket"
    private String color; // "blue", "black"
    private String brand; // "nike", "apple", "unknown"
    private String location; // "gym", "cafeteria"
    private String date; // MM/DD/YYYY format

    //Creates an item with the shared matching fields
    public Item(String itemType, String color, String brand,
                String location, String date) 
    {
        this.itemType = clean(itemType).toLowerCase();
        this.color    = clean(color).toLowerCase();
        this.brand    = clean(brand).toLowerCase();
        this.location = clean(location).toLowerCase();
        this.date     = clean(date); 
    }

    //Returns the report type label
    public abstract String getReportType();

    //Returns the report ID
    public abstract int getReportID();

    public String getItemType() 
    { 
        return itemType; //return item type string (lowercase)
    } 
    public String getColor()    
    { 
        return color; //return color string (lowercase)
    } 
    public String getBrand()    
    { 
        return brand; //return brand string (lowercase)
    }
    public String getLocation() 
    { 
        return location; //return location string (lowercase)
    } 
    public String getDate()     
    { 
        return date; //return date string in MM/DD/YYYY format
    } 

    //Returns a summary of the item details.
    public String getSummary() 
    {
        return "Type: "      + cap(itemType)  +
               " | Color: "  + cap(color)     +
               " | Brand: "  + cap(brand)     +
               " | Location: " + cap(location) +
               " | Date: "   + date;
    }

    //Remove the separator and trims spaces
    private String clean(String s) 
    {
        return s.replace(SEPARATOR, "").trim();
    }

    //Capatalizes the first letter
    private String cap(String s) 
    {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

