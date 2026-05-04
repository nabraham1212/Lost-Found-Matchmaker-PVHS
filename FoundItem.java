/*
Stores a found item report
Inherits item details from Item and adds finder name, hold location, and report ID
 */
public class FoundItem extends Item {

    private int    reportID;
    private String finderName;
    private String heldAt;
    
//Creates a found item report.

    public FoundItem(int reportID, String finderName, String heldAt,
                     String itemType, String color, String brand,
                     String location, String date) 
    {
        super(itemType, color, brand, location, date);
        this.reportID   = reportID;
        this.finderName = finderName.replace(Item.SEPARATOR, "").trim();
        this.heldAt     = heldAt.replace(Item.SEPARATOR, "").trim();
    }

 //Returns the report type
 
    public String getReportType() 
    {
        return "FOUND";
    }

//Returns the report ID

    public int getReportID() 
    {
        return reportID;
    }


    public String getFinderName() 
    { 
        return finderName; 
    }

  
    public String getHeldAt() 
    { 
        return heldAt; 
    }

   
//Returns a string for this found item report
    public String toString() 
    {
        return "[" + getReportType() + " #" + reportID + "] " + getSummary() +
               " | Found by: "     + finderName +
               " | Currently at: " + heldAt;
    }
}

