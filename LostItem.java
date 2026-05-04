/*
Stores a lost item report.
Inherits item detail from Item and adds reporter name, contact info, and report ID.
*/
public class LostItem extends Item 
{

    private int    reportID;
    private String reporterName;
    private String contactInfo;   // @pvlearners.net email or 6+ digit student ID

    //Creates a lost item report

    public LostItem(int reportID, String reporterName, String contactInfo,
                    String itemType, String color, String brand,
                    String location, String date) 
    {
        super(itemType, color, brand, location, date);
        this.reportID     = reportID;
        this.reporterName = reporterName.replace(Item.SEPARATOR, "").trim();
        this.contactInfo  = contactInfo.replace(Item.SEPARATOR, "").trim();
    }

    //Returns the report type
    public String getReportType() 
    {
        return "LOST";
    }

    //Returns the report ID
    public int getReportID() 
    {
        return reportID;
    }


    public String getReporterName() 
    { 
        return reporterName; 
    }

    public String getContactInfo()  
    { 
        return contactInfo; 
    }

    //Returns a string for this lost item report
    public String toString() 
    {
        return "[" + getReportType() + " #" + reportID + "] " + getSummary() +
               " | Reported by: " + reporterName +
               " | Contact: "     + contactInfo;
    }
}
