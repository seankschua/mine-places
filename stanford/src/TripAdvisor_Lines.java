import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class TripAdvisor_Lines {
    public static void main(String[] args) throws IOException,
            ClassNotFoundException {
 
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger(
                "taggers/english-left3words-distsim.tagger");
 
        // The sample string
        //String sample = "This is a sample text";
        File file = new File("data/2015-09-10-16-23-39outfile.txt");
        ArrayList<String> input = new ArrayList<String>();
        
        try {

            Scanner sc = new Scanner(file);
            sc.nextLine();

            while (sc.hasNextLine()) {
            	String current = sc.nextLine();
            	current = current.replace("<br>", "");
            	//current = current.replace(".", ". ");
            	current = current.substring(current.indexOf("\t"));
                input.add(current);
            }
            sc.close();
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        // The tagged string
        //String tagged = tagger.tagString(sample);
        ArrayList<String> output = new ArrayList<String>();
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        		new FileOutputStream("data/left.txt"), "utf-8"))) {
        	for (String review : input){
        		String taggedReview = tagger.tagString(review);
        		ArrayList<String> entities = new ArrayList<String>();
        		String[] reviewTokens = taggedReview.split(" ");
        		ArrayList<String> currentEntityPhrase = new ArrayList<String>();
        		for (String reviewToken : reviewTokens){
        			String[] tokenClass = reviewToken.split("_");
        			if(tokenClass[1].startsWith("NN")){
        				currentEntityPhrase.add(tokenClass[0]);
        			} else if (!currentEntityPhrase.isEmpty()) {
        				entities.add(String.join(" ", currentEntityPhrase));
        				currentEntityPhrase.clear();
        			}
        		}
        		if(!currentEntityPhrase.isEmpty()){
        			entities.add(String.join(" ", currentEntityPhrase));
        		}
        		String listEntities = String.join(", ", entities);
        		writer.write(listEntities);
        		writer.write("\n");
            }
        }
 
        // Output the result
        //System.out.println(tagged);
    }
}