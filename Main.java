import java.util.ArrayList;
import java.util.Scanner;

/*
Main class for Lost and Found Matchmaker
Handles the menu, user input, and calls the Matchmaker methods.
 */
public class Main 
{

    /*
    Menu Labels
    1D String Array used to store all menu options
    MENU_OPTIONS is final because the Menu Options do not change
    */
    private static final String[] MENU_OPTIONS = 
    {
        "Report a lost item", // 1
        "Report a found item",  // 2
        "Find matches for a lost item", // 3
        "View all lost reports", // 4
        "View all found reports", // 5
        "Search lost items by type", // 6
        "Exit" // 7
    };

    // Allowed year range for dates
    // Prim. Constants: int used to  validate acceptable date years
    private static final int DATE_MIN_YEAR = 2020;
    private static final int DATE_MAX_YEAR = 2035;

    static Matchmaker db = new Matchmaker();
    static Scanner scanner = new Scanner(System.in);

    // Starts the program and runs the menu until the user exits.
    public static void main(String[] args) 
    {
        printBanner();

        // Report how many records were loaded from the save files
        int lostCount  = db.getLostItems().size();
        int foundCount = db.getFoundItems().size();
        System.out.println("  >> " + lostCount  + " lost report(s) and " +
        foundCount + " found report(s) loaded.\n");

        String choice;

        // do-while: always show the menu at least once, then repeat until exit
        do 
        {
            printMenu();
            choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": reportLostItem();  break;
                case "2": reportFoundItem(); break;
                case "3": findMatches();     break;
                case "4": viewAllLost();     break;
                case "5": viewAllFound();    break;
                case "6": searchByType();    break;
                case "7":
                    System.out.println("\n  Goodbye! All reports are saved.\n");
                    break;
                default:
                    System.out.println("  Invalid choice. Please enter a number between 1 and 7.\n");
            }

        } 
        
        while (!choice.equals("7"));
    }

    //Collects and saves a lost item report.
    static void reportLostItem() 
    {
        System.out.println("\n--- REPORT A LOST ITEM ---");

        String name     = promptRequired("Your full name");
        String contact  = promptContact();
        String type     = promptRequired("Item type (e.g. water bottle, jacket, headphones)");
        String color    = promptRequired("Color (e.g. blue, black, red)");
        String brand    = promptRequired("Brand (or type 'unknown')");
        String location = promptRequired("Where did you last have it? (e.g. gym, cafeteria)");
        String date     = promptDate("Date lost (MM/DD/YYYY)");

        LostItem item = db.addLostItem(name, contact, type, color, brand, location, date);
        System.out.println("\n  ✓ Lost item report saved!");
        System.out.println("  " + item + "\n");
    }

    //Collects and saves a found item report.
    static void reportFoundItem() 
    {
        System.out.println("\n--- REPORT A FOUND ITEM ---");

        String name     = promptRequired("Your full name");
        String heldAt   = promptRequired("Where is the item being held? (e.g. front office, room 101)");
        String type     = promptRequired("Item type (e.g. water bottle, jacket, headphones)");
        String color    = promptRequired("Color (e.g. blue, black, red)");
        String brand    = promptRequired("Brand (or type 'unknown')");
        String location = promptRequired("Where was it found? (e.g. gym, hallway)");
        String date     = promptDate("Date found (MM/DD/YYYY)");

        FoundItem item = db.addFoundItem(name, heldAt, type, color, brand, location, date);
        System.out.println("\n  ✓ Found item report saved!");
        System.out.println("  " + item + "\n");
    }

    //Shows matches for a selected lost item
    static void findMatches() 
    {
        System.out.println("\n--- FIND MATCHES FOR A LOST ITEM ---");

        if (db.getLostItems().isEmpty()) 
        {
            System.out.println("  No lost item reports on file.\n");
            return;
        }

        if (db.getFoundItems().isEmpty()) 
        {
            System.out.println("  No found item reports on file yet. Nothing to match against.\n");
            return;
        }

        // Shows the list of current lost reports for the user to choose from
        System.out.println("  Current lost reports:");
        for (LostItem item : db.getLostItems()) 
        {
            System.out.println("    #" + item.getReportID() +
                               " — " + item.getItemType() +
                               " (" + item.getColor() + ")" +
                               " — reported by " + item.getReporterName());
        }

        // Get and validate the report ID
        System.out.print("\n  Enter the Lost Report ID to find matches for: ");
        int id;
        try 
        {
            id = Integer.parseInt(scanner.nextLine().trim());
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("  Invalid input. Please enter a number from the list above.\n");
            return;
        }

        LostItem target = db.findLostByID(id);
        if (target == null) 
        {
            System.out.println("  No lost report found with ID #" + id +
                               ". Please choose an ID from the list above.\n");
            return;
        }

        System.out.println("\n  Finding matches for:");
        System.out.println("  " + target);

        // Use the overloaded findMatches(LostItem, int) - limit 5 results
        int MAX_DISPLAY = 5;
        ArrayList<MatchResult> matches = db.findMatches(target, MAX_DISPLAY);

        if (matches.isEmpty()) 
        {
            System.out.println("\n  No strong matches found yet. Check back as more found items are reported.\n");
            return;
        }

        // Display count accurately - shows how many are being shown
        int total = db.findMatches(target).size();
        String header = matches.size() + " match" + (matches.size() == 1 ? "" : "es");
        if (total > MAX_DISPLAY) 
        {
            header += " shown (top " + MAX_DISPLAY + " of " + total + " total)";
        } 
        else 
        {
            header += " found";
        }

        System.out.println("\n  " + header.toUpperCase() + ":");
        System.out.println("  " + "=".repeat(65));

        for (int i = 0; i < matches.size(); i++) 
        {
            System.out.println("\n  Match #" + (i + 1));
            System.out.println(matches.get(i));
        }

        System.out.println("\n  " + "=".repeat(65) + "\n");
    }

    // Displays all lost reports
    static void viewAllLost() 
    {
        int total = db.getLostItems().size();
        System.out.println("\n--- ALL LOST REPORTS (" + total + " total) ---");
        if (total == 0) 
        {
            System.out.println("  No lost reports on file.\n");
            return;
        }
        db.printAllLost();
        System.out.println();
    }

    // Displays all found reports
    static void viewAllFound() 
    {
        int total = db.getFoundItems().size();
        System.out.println("\n--- ALL FOUND REPORTS (" + total + " total) ---");
        if (total == 0) 
        {
            System.out.println("  No found reports on file.\n");
            return;
        }
        db.printAllFound();
        System.out.println();
    }

    // Searches lost report by item type.
    static void searchByType() 
    {
        System.out.println("\n--- SEARCH LOST ITEMS BY TYPE ---");
        System.out.print("  Enter item type keyword (e.g. jacket, water bottle): ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) 
        {
            System.out.println("  Please enter a keyword to search.\n");
            return;
        }

        ArrayList<LostItem> results = db.searchLostByType(keyword);
        if (results.isEmpty()) 
        {
            System.out.println("  No lost items found matching: \"" + keyword + "\"\n");
        } 
        else 
        {
            System.out.println("  Found " + results.size() +
                               " result(s) for \"" + keyword + "\":");
            for (LostItem item : results) {
                System.out.println("  " + item);
            }
            System.out.println();
        }
    }

    // Prompts until the user enters a non-blank value.
    static String promptRequired(String label) 
    {
        String input = "";
        while (input.isEmpty()) 
        {
            System.out.print("  " + label + ": ");
            input = scanner.nextLine().replace(Item.SEPARATOR, "").trim();
            if (input.isEmpty()) 
            {
                System.out.println("  This field cannot be blank. Please enter a value.");
            }
        }

        return input;
    }

    // Prompts the user to enter a valid pvlearners email or school ID
    static String promptContact() 
    {
        String  input = "";
        boolean valid = false;

        while (!valid) 
        {
            System.out.print("  Contact: pvlearners.net Email or School ID: ");
            input = scanner.nextLine().replace(Item.SEPARATOR, "").trim();

            if (input.isEmpty()) 
            {
                // Nested-if: blank is its own case before format checking
                System.out.println("  This field cannot be blank.");
            } 
            else if (isValidPVEmail(input)) 
            {
                valid = true;
            } 
            else if (input.matches("\\d{6,}")) 
            {
                valid = true;
            } 
            else 
            {
                System.out.println("  Invalid contact. Enter your @pvlearners.net email");
                System.out.println("  (e.g. nabraham1@pvlearners.net) or your 7-digit School ID.");
            }
        }
        
        return input;
    }

    //Keeps prompting the user until they enter a valid date
    static String promptDate(String label) 
    {
        String  input = "";
        boolean valid = false;

        while (!valid) 
        {
            System.out.print("  " + label + ": ");
            input = scanner.nextLine().trim();

            if (input.isEmpty()) 
            {
                System.out.println("  This field cannot be blank. Please enter a date.");
            } 
            else if (!isValidDate(input)) 
            {
                System.out.println("  Invalid date. Use MM/DD/YYYY with:");
                System.out.println("    - Month: 01 to 12");
                System.out.println("    - Day: 01 to 31");
                System.out.println("    - Year: " + DATE_MIN_YEAR + " to " + DATE_MAX_YEAR);
                System.out.println("  Example: 04/15/2026");
            } 
            else 
            {
                valid = true;
            }
        }
        
        return input;
    }

    //Checks if the email matches the @pvlearners.net format
    static boolean isValidPVEmail(String input) 
    {
        return input.matches("[a-zA-Z]+[1-9]@pvlearners\\.net");
    }

    //Checks if the data is in a MM/DD/YYYY format and range.
    static boolean isValidDate(String date) 
    {
        //Format check - must be exactly ##/##/####
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) return false;

        //Range check - parse each part and verify values
        int month = Integer.parseInt(date.substring(0, 2));
        int day   = Integer.parseInt(date.substring(3, 5));
        int year  = Integer.parseInt(date.substring(6));

        if (month < 1 || month > 12)              return false;
        if (day   < 1 || day   > 31)              return false;
        if (year  < DATE_MIN_YEAR || year > DATE_MAX_YEAR) return false;

        return true;
    }

    //Prints the program banner
    static void printBanner() 
    {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║     PARADISE VALLEY HIGH SCHOOL                     ║");
        System.out.println("  ║     LOST & FOUND MATCHMAKER                         ║");
        System.out.println("  ║     APCSA Final Project — 2025-2026                 ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    //Prints the menu 
    static void printMenu() 
    {
        System.out.println("  MENU:");
        for (int i = 0; i < MENU_OPTIONS.length; i++) 
        {
            System.out.println("   " + (i + 1) + ". " + MENU_OPTIONS[i]);
        }
        System.out.print("\n  Enter choice (1-" + MENU_OPTIONS.length + "): ");
    }
}
