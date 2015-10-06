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
 
public class TripAdvisor_Orig {
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
        		new FileOutputStream("data/left_orig.txt"), "utf-8"))) {
        	for (String review : input){
        		writer.write(tagger.tagString(review));
        		writer.write("\n");
            }
        }
 
        // Output the result
        //System.out.println(tagged);
    }
}