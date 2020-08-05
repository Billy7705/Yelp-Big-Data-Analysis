// William Guenneugues
// public class Business

package hw6;

import java.util.*;

public class Business implements Comparable<Business>
{
    private String businessID;
    private String businessName;
    private String businessAddress;
    private String review;
    private int reviewCharCount;

    // constructor uses array of string to initialize each field
    public Business(String[] fields)
    {
        businessID = fields[0];
        businessName = fields[1];
        businessAddress = fields[2];
        review = fields[3];
        reviewCharCount = review.length();
    }

    public String toString()
    {
        return "-------------------------------------------------------------------------------\n"
                + "Business ID: " + businessID + "\n"
                + "Business Name: " + businessName + "\n"
                + "Business Address: " + businessAddress + "\n"
                + "Character Count: " + reviewCharCount + "\n";
    }

    // method returns review length
    public int get_review_length() {
    	return reviewCharCount;
    	}

    // method to compare two businesses based on character count
    public int compareTo(Business that)
    {
        if(this.reviewCharCount < that.reviewCharCount) return -1;
        else if(this.reviewCharCount > that.reviewCharCount) return 1;
        else return 0;
    }

    // method to find tfidf score of word
    public void get_tfidf(List<Map.Entry<String,Double>> tfidf_scores, Map<String,Integer> corpus, HashSet<String> common_words)
    {
        // obtain the frequency map
        HashMap<String,Integer> frequency_map = getfrequency_map(common_words);
        for(String word: frequency_map.keySet())
        {
            // go over each entry in frequency map and calculate its tf-idf score
            if(corpus.get(word) >= 5)
            {
                double score = (double)(frequency_map.get(word))/(double)(corpus.get(word));
                tfidf_scores.add(new AbstractMap.SimpleEntry<String,Double>(word,score));
            }
            else tfidf_scores.add(new AbstractMap.SimpleEntry<String,Double>(word,0.0));
        }
    }

    // method returns frequency map for each word in review
    private HashMap<String,Integer> getfrequency_map(HashSet<String> common_words)
    {
    	// initializes map to be used later on
        HashMap<String,Integer> frequency_map = new HashMap<String,Integer>();
        
        String word = "";
        
        // initialize scanner
        Scanner scan = new Scanner(review);
        scan.useDelimiter(" ");

        while(scan.hasNext())
        {
            word = scan.next();
            
            // skip common words
            if(!common_words.contains(word))
            {
            	// if map contains word add 1 to frequency
                if(frequency_map.containsKey(word))
                    frequency_map.put(word,frequency_map.get(word)+1); 
                
                // if not in map add word to map
                else 
                	frequency_map.put(word, 1); 
            }
        }
        scan.close();
        return frequency_map;
    }
}
