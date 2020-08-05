// William Guenneugues
// public class YelpAnalysis

package hw6;

import java.io.*;
import java.util.*;
import hw6.Business;

public class YelpAnalysis { 
	public static void main(String[] arg) {
		
		// initialize streams to be used later on
		FileInputStream in = null;
        BufferedInputStream buff_stream = null;

        // initialize priority queue to be used later on 
        PriorityQueue<Business> businessQueue = new PriorityQueue<Business>();
        
        // initialize Map to be used later on
        HashMap<String,Integer> corpusDFCount = new HashMap<String,Integer>();
        
        // initialize Set to be used later on
        HashSet<String> word_corpus = new HashSet<String>();
        
        // initialize set of common words used later on to filter out common words
        HashSet<String> common_words = new HashSet<String>(Arrays.asList("a", "an", "is", "are", "was", "will", "be", "to", "or", "if", "and", "but", "i", "you", "we", "they", "us", "the", "in", "that", "have", "it", "not", "on", "with", "as", "do", "his", "him", "her", "he", "she", "my", "our", "so", "if"));

        // try block used to open date file
        try
        {
            // open data file in input stream then in buffered input stream
            in = new FileInputStream("src/hw6/yelpDatasetParsed_short.txt");
            buff_stream = new BufferedInputStream(in);

            // method reads file and creates business objects based on file data
            read_file(buff_stream,businessQueue,corpusDFCount,word_corpus,common_words);

            // close streams
            buff_stream.close();
            in.close();
        }
        
        // exception if file is not found
        catch(IOException e) 
        {
            System.out.println("File not found");
        }

        // outputs top ten businesses information 
        for(int i=0;i<10;i++)
        {
        	// initialize arraylist used to store tf-idf value
            ArrayList<Map.Entry<String,Double>> tfidf_value = new ArrayList<Map.Entry<String,Double>>();
            
            // pop business from priority queue in order 
            Business b = businessQueue.poll();

            // calculate and sort the tf-idf values of each word
            b.get_tfidf(tfidf_value,corpusDFCount,common_words);
            sort_tfidf(tfidf_value);

            // print out business info
            System.out.println(b);
            print_words(tfidf_value);
        }
    }

	// method reads buffered input stream, creates corresponding business objects, and stores the top 10 in priority queue
    private static void read_file(BufferedInputStream buff_stream, PriorityQueue<Business> businessQueue, Map<String,Integer> corpus, HashSet<String> word_corpus, HashSet<String> common_words)
    {
    	// declare variables to be used later on
        StringBuilder temp_string = new StringBuilder();
        String[] fields = new String[4]; 
        
        // try block reads stream 
        try
        {
        	// count used to keep track of which field we're on
            int count = 0;
            
            
            while(true)
            {
            	// loop reads next character
                int nextByte = buff_stream.read();
                if(nextByte == -1) break;
                char c = (char) nextByte;
                
                // skips character if it is not a word
                if(c != '{' && c != '\n' && c!= '\r')
                {
                	// if character is a comma or end bracket assigns value to corresponding business field 
                    if(c == ',' || c == '}')
                    {
                        fields[count] = temp_string.toString();
                        
                        // increments to next field
                        count++;
                        
                        if(count >= 4)
                        {
                            // adds review information to overall corpus
                            addDocumentCount(fields[3], corpus, word_corpus, common_words);
                            
                            // if queue is not full adds business to queue 
                            if(businessQueue.size() < 10)
                                businessQueue.add(new Business(fields));
                            
                            // if not compares business review length to peek of priority queue and replaces it if review length is longer
                            else { 
                            	
                            	// if review length is larger pops top elemetn and replaces it with current business
                                if(fields[3].length() > businessQueue.peek().get_review_length())
                                {
                                    businessQueue.poll();
                                    businessQueue.add(new Business(fields));
                                }
                            }
                            
                            // resets count
                            count = 0;
                        }
                        // clear the temp string
                        temp_string.delete(0, temp_string.length());
                    }
                    // adds character to temp string until it reaches a comma
                    else temp_string.append(c);
                }
            }
        }
        
        // exception thrown 
        catch(IOException e)
        {
            System.out.println("IO Exception");
            return;
        }
    }

    
    // takes words from the review and assigns them to a corpus to determine their frequency
    private static void addDocumentCount(String review, Map<String,Integer> corpus, HashSet<String> word_corpus, HashSet<String> common_words)
    {
        // create a Scanner to read from the review
        String word = "";
        Scanner scan = new Scanner(review);
        scan.useDelimiter(" ");

        while(scan.hasNext())
        {
            word = scan.next();
            
            // skips word if its a common word
            if(!common_words.contains(word)) 
            {
                // skips word if it appeared already
                if(!word_corpus.contains(word))
                {
                	// if corpus contains word adds 1 to its frequency
                    if(corpus.containsKey(word))
                        corpus.put(word,corpus.get(word)+1); 
                    
                    // if word not present adds word to corpus
                    else 
                    	corpus.put(word, 1); 
                 
                    // adds word to word_corpus
                    word_corpus.add(word);
                }
            }
        }
        
        // clears word_corpus and closes scanner
        word_corpus.clear();
        scan.close();
    }

    // outputs 30 most used words for each business along with their frequency
    public static <K,V> void print_words(List<Map.Entry<K,V>> list)
    {
        for(int i=0;i<30;i++)
        {
            K key = list.get(i).getKey();
            V val = list.get(i).getValue();
            
            // formats output to digits past decimal point
            System.out.format("("+key.toString()+",%.2f)",val);

        }
        System.out.println();
    }

    // sorts words in business according to frequency of word
    public static <K,V extends Comparable<V>> void sort_tfidf(List<Map.Entry<K,V>> list)
    {
        Collections.sort(list, new ScoreComparator<K,V>());
    }

    // private class used to define score comparator
    private static class ScoreComparator<K,V extends Comparable<V>> implements Comparator<Map.Entry<K,V>>
    {
    	// compares value and places one with biggest value on top
        public int compare(Map.Entry<K,V> o1, Map.Entry<K,V> o2)
        {
            return o2.getValue().compareTo(o1.getValue());
        }
    }
}
