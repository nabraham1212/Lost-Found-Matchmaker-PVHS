//Stores one match between a lost item and a found item.
public class MatchResult 
{

    //Highest possible match score
    public static final int MAX_SCORE = 6;

    private FoundItem foundItem;
    private int score;
    private String scoreBreakdown;

    //Creates a match result
    public MatchResult(FoundItem foundItem, int score, String scoreBreakdown) 
    {
        this.foundItem      = foundItem;
        this.score          = score;
        this.scoreBreakdown = scoreBreakdown;
    }

    public FoundItem getFoundItem()      
    { 
        return foundItem; 
    }

    public int getScore()          
    { 
        return score; 
    }

    public String getScoreBreakdown() 
    { 
        return scoreBreakdown; 
    }

    //Return a label for the match strength
    public String getStrengthLabel() 
    {
        if (score >= 5) 
        {
            return "Excellent";
        } 
        else if (score >= 4) 
        {
            return "Strong";
        } 
        else if (score >= 3) 
        {
            return "Moderate";
        } 
        else 
        {
            return "Possible";
        }
    }

    //Returns a formatted string for this match result.
    public String toString() 
    {
        return "  Score: " + score + "/" + MAX_SCORE +
               "  [" + getStrengthLabel() + "]\n" +
               "  " + foundItem.toString() + "\n" +
               "  Fields matched: " + scoreBreakdown;
    }
}
