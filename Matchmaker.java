import java.util.ArrayList;
import java.io.*;

/*
Manages lost and found reports.
Handles saving, loading, searching, and matching.
 */
public class Matchmaker {

    //Save file for lost smart reports.
    private static final String LOST_FILE  = "lost_items.txt";

    //Save file for found reports.
    private static final String FOUND_FILE = "found_items.txt";

    //Minimum score needed for a match to be shown.
    private static final int MIN_MATCH_SCORE = 2;

    //Names of the fields used in match scoring.
    private static final String[] SCORE_FIELDS = 
    {
        "item type",
        "color",
        "brand",
        "location",
        "date"
    };

    //Points value for each scoring fields
    private static final int[] SCORE_WEIGHTS = 
    {
        2,   //item type
        1,   //color
        1,   //brand
        1,   //location
        1    //date
    };

    private ArrayList<LostItem>  lostItems;
    private ArrayList<FoundItem> foundItems;

    private int lostIDCounter  = 1;
    private int foundIDCounter = 1;

    //Creates the matchmaker and loads saved data
    public Matchmaker() 
    {
        lostItems  = new ArrayList<LostItem>();
        foundItems = new ArrayList<FoundItem>();
        seedSampleDataIfFirstRun();
        loadFromFiles();
    }

    /*
    Clears all data and resets ID.
    Used only for testing.
    */
    protected void resetForTesting()
    {
        lostItems.clear();
        foundItems.clear();
        lostIDCounter  = 1;
        foundIDCounter = 1;
    }

    //Creates a saves a lost item report.
    public LostItem addLostItem(String reporterName, String contactInfo,
                                String itemType, String color, String brand,
                                String location, String date) 
    {
        LostItem item = new LostItem(lostIDCounter++, reporterName, contactInfo,
                                     itemType, color, brand, location, date);
        lostItems.add(item);
        saveLostItems();


        return item;
    }

    //Creates and saves a found item report.
    public FoundItem addFoundItem(String finderName, String heldAt,
                                  String itemType, String color, String brand,
                                  String location, String date) 
    {
        FoundItem item = new FoundItem(foundIDCounter++, finderName, heldAt,
                                       itemType, color, brand, location, date);
        foundItems.add(item);
        saveFoundItems();
        return item;
    }

    public ArrayList<LostItem> getLostItems()  
    { 
        return lostItems; 
    }

    public ArrayList<FoundItem> getFoundItems() 
    { 
        return foundItems; 
    }

    //Prints all lost reports.
    public void printAllLost() 
    {
        for (LostItem item : lostItems) 
        {
            System.out.println("  " + item);
        }
    }

    //Prints all found reports.
    public void printAllFound() 
    {
        for (FoundItem item : foundItems) 
        {
            System.out.println("  " + item);
        }
    }

    //Returns all matches for a lost item.
    public ArrayList<MatchResult> findMatches(LostItem lost) 
    {
        return findMatches(lost, Integer.MAX_VALUE);
    }

    //Returns up to a limited number of matches for a lost item.
    public ArrayList<MatchResult> findMatches(LostItem lost, int limit) 
    {
        ArrayList<MatchResult> results = new ArrayList<MatchResult>();

        for (FoundItem found : foundItems) 
        {
            int score = 0;
            ArrayList<String> matched = new ArrayList<String>();

            if (lost.getItemType().equals(found.getItemType())) 
            {
                score += SCORE_WEIGHTS[0];
                matched.add(SCORE_FIELDS[0]);
            }

            if (lost.getColor().equals(found.getColor())) 
            {
                score += SCORE_WEIGHTS[1];
                matched.add(SCORE_FIELDS[1]);
            }

            if (!lost.getBrand().equals("unknown") &&
                !found.getBrand().equals("unknown")) 
                {
                if (lost.getBrand().equals(found.getBrand())) 
                {
                    score += SCORE_WEIGHTS[2];
                    matched.add(SCORE_FIELDS[2]);
                }
            }

            if (lost.getLocation().equals(found.getLocation())) 
            {
                score += SCORE_WEIGHTS[3];
                matched.add(SCORE_FIELDS[3]);
            }

            if (datesAreClose(lost.getDate(), found.getDate(), 3)) 
            {
                score += SCORE_WEIGHTS[4];
                matched.add(SCORE_FIELDS[4]);
            }

            if (score >= MIN_MATCH_SCORE) 
            {
                String breakdown = "";
                for (int i = 0; i < matched.size(); i++) 
                {
                    if (i > 0) breakdown += ", ";
                    breakdown += matched.get(i);
                }
                results.add(new MatchResult(found, score, breakdown));
            }
        }

        for (int i = 0; i < results.size() - 1; i++) 
        {
            int maxIndex = i;
            for (int j = i + 1; j < results.size(); j++) 
            {
                if (results.get(j).getScore() > results.get(maxIndex).getScore()) 
                {
                    maxIndex = j;
                }
            }

            MatchResult temp = results.get(i);
            results.set(i, results.get(maxIndex));
            results.set(maxIndex, temp);
        }

        if (results.size() > limit) 
        {
            results = new ArrayList<MatchResult>(results.subList(0, limit));
        }

        return results;
    }

    //Finds a lost report by ID
    public LostItem findLostByID(int id) 
    {
        for (LostItem item : lostItems) 
        {
            if (item.getReportID() == id) return item;
        }

        return null;
    }

    //Searches lost report by item type
    public ArrayList<LostItem> searchLostByType(String keyword) 
    {
        ArrayList<LostItem> results = new ArrayList<LostItem>();
        String key = keyword.toLowerCase().trim();

        if (key.isEmpty()) return results;

        for (LostItem item : lostItems) 
        {
            if (item.getItemType().contains(key)) 
            {
                results.add(item);
            }
        }

        return results;
    }

    //Where our sample data is stored an ran
    private void seedSampleDataIfFirstRun() 
    {
        boolean lostFileMissing  = !new File(LOST_FILE).exists();
        boolean foundFileMissing = !new File(FOUND_FILE).exists();

        if (!lostFileMissing && !foundFileMissing) return;

        if (lostFileMissing) 
        {
            try {
                PrintWriter w = new PrintWriter(new FileWriter(LOST_FILE));
                // Format: id|reporterName|contactInfo|itemType|color|brand|location|date
                w.println("1|Marcus V.|mvettraino1@pvlearners.net|water bottle|blue|hydro flask|gym|04/07/2026");
                w.println("2|Sofia A.|sanderson205@pvlearners.net|jacket|black|nike|cafeteria|04/08/2026");
                w.println("3|Jaylen R.|jrose@pvlearners.net|headphones|white|apple|library|04/06/2026");
                w.println("4|Priya P.|ppatel5@pvlearners.net|lunch box|red|unknown|classroom 204|04/09/2026");
                w.println("5|Carlos B.|cbenavides1@pvlearners.net|calculator|black|ti|math hallway|04/05/2026");
                w.println("6|Aisha P.|apreciado@pvlearners.net|jacket|gray|adidas|gym|04/10/2026");
                w.println("7|Devon G.|dgivner1@pvlearners.net|water bottle|green|nalgene|cafeteria|04/07/2026");
                w.println("8|Max A.|magins1@pvlearners.net|earbuds|white|samsung|hallway|04/08/2026");
                w.println("9|Cole P.|cpatterson5@pvlearners.net|backpack|black|jansport|gym|04/06/2026");
                w.println("10|Nadia A.|nahern1@pvlearners.net|water bottle|blue|hydro flask|gym|04/08/2026");
                w.println("11|Kaden W.|kwilson18@pvlearners.net|headphones|black|sony|library|04/09/2026");
                w.println("12|Isabelle A.|iali1@pvlearners.net|jacket|black|north face|cafeteria|04/10/2026");
                w.println("13|Omar M.|omartinez1@pvlearners.net|phone|black|apple|classroom 101|04/07/2026");
                w.println("14|Zoe A.|zaguirre1@pvlearners.net|lunch box|purple|unknown|cafeteria|04/05/2026");
                w.println("15|Ethan A.|eallred2@pvlearners.net|calculator|black|casio|science room|04/08/2026");
                w.close();
            } 
            
            catch (IOException e) 
            {
                System.out.println("  Warning: Could not write sample lost data. (" + e.getMessage() + ")");
            }
        }

        if (foundFileMissing) 
        {
            try 
            {
                PrintWriter w = new PrintWriter(new FileWriter(FOUND_FILE));
                w.println("1|Coach Rivera|gym office|water bottle|blue|hydro flask|gym|04/07/2026");
                w.println("2|Ms. Chen|front office|jacket|black|nike|cafeteria|04/08/2026");
                w.println("3|Mr. Davis|library desk|headphones|white|apple|library|04/06/2026");
                w.println("4|Lunch Staff|cafeteria|lunch box|red|unknown|cafeteria|04/09/2026");
                w.println("5|Mr. Kim|room 204|calculator|black|ti|math hallway|04/05/2026");
                w.println("6|Janitor|gym lost box|jacket|gray|adidas|gym|04/10/2026");
                w.println("7|Coach Rivera|gym office|water bottle|green|unknown|gym|04/07/2026");
                w.println("8|Ms. Park|front office|earbuds|white|samsung|hallway|04/08/2026");
                w.println("9|Janitor|gym lost box|backpack|black|jansport|gym|04/06/2026");
                w.println("10|Coach Rivera|gym office|water bottle|blue|hydro flask|gym|04/09/2026");
                w.println("11|Mr. Davis|library desk|headphones|black|sony|library|04/09/2026");
                w.println("12|Ms. Chen|front office|jacket|black|north face|cafeteria|04/10/2026");
                w.println("13|Student Aide|room 101|phone|black|apple|classroom 101|04/07/2026");
                w.println("14|Lunch Staff|cafeteria|lunch box|purple|unknown|cafeteria|04/05/2026");
                w.println("15|Ms. Park|front office|calculator|black|casio|science room|04/08/2026");
                w.close();
            } 
            
            catch (IOException e) 
            {
                System.out.println("  Warning: Could not write sample found data. (" + e.getMessage() + ")");
            }
        }
    }

    //Save all lost reports to file
    private void saveLostItems() 
    {
        try 

        {
            PrintWriter writer = new PrintWriter(new FileWriter(LOST_FILE));
            for (LostItem item : lostItems) 
            {
                writer.println(
                    item.getReportID()     + Item.SEPARATOR +
                    item.getReporterName() + Item.SEPARATOR +
                    item.getContactInfo()  + Item.SEPARATOR +
                    item.getItemType()     + Item.SEPARATOR +
                    item.getColor()        + Item.SEPARATOR +
                    item.getBrand()        + Item.SEPARATOR +
                    item.getLocation()     + Item.SEPARATOR +
                    item.getDate()
                );
            }
            writer.close();
        } 
        
        catch (IOException e) 
        {
            System.out.println("  Warning: Could not save lost items. (" + e.getMessage() + ")");
        }
    }

    //Save all found reports to file.
    private void saveFoundItems() 
    {
        try 
        
        {
            PrintWriter writer = new PrintWriter(new FileWriter(FOUND_FILE));
            for (FoundItem item : foundItems) 
            {
                writer.println(
                    item.getReportID()   + Item.SEPARATOR +
                    item.getFinderName() + Item.SEPARATOR +
                    item.getHeldAt()     + Item.SEPARATOR +
                    item.getItemType()   + Item.SEPARATOR +
                    item.getColor()      + Item.SEPARATOR +
                    item.getBrand()      + Item.SEPARATOR +
                    item.getLocation()   + Item.SEPARATOR +
                    item.getDate()
                );
            }

            writer.close();
        } 
        
        catch (IOException e) 
        {
            System.out.println("  Warning: Could not save found items. (" + e.getMessage() + ")");
        }
    }

    //Loads all saved reports from file
    private void loadFromFiles() 
    {
        loadLostItems();
        loadFoundItems();
    }

    //Loads lost reports from file
    private void loadLostItems() 
    {
        File file = new File(LOST_FILE);
        if (!file.exists()) return;

        try 
        
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) 
            {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                try 
                {
                    int id = Integer.parseInt(parts[0]);
                    lostItems.add(new LostItem(id, parts[1], parts[2],
                                               parts[3], parts[4], parts[5],
                                               parts[6], parts[7]));
                    if (id >= lostIDCounter) lostIDCounter = id + 1;
                } 
                
                catch (NumberFormatException e) 
                {
                    //skip this line and continue loading
                }
            }
            
            reader.close();
        } 
        
        catch (IOException e) 
        {
            System.out.println("  Warning: Could not read lost items file. (" + e.getMessage() + ")");
        }
    }

    //Loads found reports from file
    private void loadFoundItems() 
    {
        File file = new File(FOUND_FILE);
        if (!file.exists()) return;

        try 
        
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) 
            {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                try 
                
                {
                    int id = Integer.parseInt(parts[0]);
                    foundItems.add(new FoundItem(id, parts[1], parts[2],
                                                 parts[3], parts[4], parts[5],
                                                 parts[6], parts[7]));
                    if (id >= foundIDCounter) foundIDCounter = id + 1;
                } 
                
                catch (NumberFormatException e) 
                {
                    // Malformed ID — skip this line, continue loading
                }
            }
            
            reader.close();
        } 
        
        catch (IOException e) 
        
        {
            System.out.println("  Warning: Could not read found items file. (" + e.getMessage() + ")");
        }
    }

    //Check if two dates are within a certain number of days
    private boolean datesAreClose(String date1, String date2, int dayRange) 
    {
        try 
        
        {
            return Math.abs(dateToDays(date1) - dateToDays(date2)) <= dayRange;
        }
        
         catch (Exception e) 
        
        {
            return false;
        }
    }

    //Converts a date to an approximate day count. 
    private int dateToDays(String date) 
    {
        String[] parts = date.split("/");
        int month = Integer.parseInt(parts[0]);
        int day   = Integer.parseInt(parts[1]);
        int year  = Integer.parseInt(parts[2]);
        return (year * 365) + (month * 30) + day;
    }
}
