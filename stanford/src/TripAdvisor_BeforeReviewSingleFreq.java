import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class TripAdvisor_BeforeReviewSingleFreq {
    public static void main(String[] args) throws IOException,
            ClassNotFoundException {
 
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger(
                "taggers/english-left3words-distsim.tagger");
 
        // The sample string
        //String sample = "This is a sample text";
        File file = new File("data/2015-09-10-16-23-39outfile.txt");
        ArrayList<String> input = new ArrayList<String>();
        int scannerCount = 0;
        BufferedReader br = null;
        
        try {

            //Scanner sc = new Scanner(file);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            br.readLine();
            //sc.nextLine();
            String nextLine = null;

            while ((nextLine = br.readLine()) != null) {
            	//String current = sc.nextLine();
            	scannerCount++;
            	//System.out.println(scannerCount);
            	nextLine = nextLine.replace("<br>", "");
            	//current = current.replace(".", ". ");
            	//current = current.substring(current.indexOf("\t"));
                input.add(nextLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        	br.close();
            input.add("end");
            System.out.println("Scanner read is " + scannerCount);
            System.out.println("Array size is " + input.size());
        }
 
        // The tagged string
        //String tagged = tagger.tagString(sample);
        int counter = 0;
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        		new FileOutputStream("data/left.txt"), "utf-8"))) {
        	
        	String currentProperty = "start";
        	Map<String,Integer> currentEntityFreq = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        	
        	for (String review : input){
        		counter++;

        		//check if same property
        		if (review.indexOf("\t")==-1){
        			continue;
        		}
        		String lineProperty = review.substring(0,review.indexOf("\t"));
        		review = review.substring(review.indexOf("\t")+1);
        		
        		//then activate tag
        		String taggedReview = tagger.tagString(review);
        		
        		//System.out.println(review);
        		if (currentProperty=="start"){
        			currentProperty = lineProperty;      			
        		} else if (review.equals("end") || !lineProperty.equals(currentProperty)){
        			//write stuff to file
        			System.out.println(currentProperty);
        			writer.write(currentProperty);
        			writer.write("\n");
            		for (String entity : currentEntityFreq.keySet()){
            			//threshold here
            			if (currentEntityFreq.get(entity)>50){
            				writer.write(entity + " - " + currentEntityFreq.get(entity));
            				writer.write("\n");
            			}
            		}
            		writer.write("WRITTEN AT " + counter + "\n");
            		writer.write("\n\n\n");
        			
        			currentProperty = lineProperty;
        			currentEntityFreq = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        		}
        		
        		//ArrayList<String> entities = new ArrayList<String>();
        		String[] reviewTokens = taggedReview.split(" ");
        		ArrayList<String> reviewTokensList = new ArrayList<String>(Arrays.asList(reviewTokens));
        		//adding this extra token so that it accounts for a entity that occurs at the end of the sentence.
        		reviewTokensList.add("END_END");
        		ArrayList<String> currentEntityPhrase = new ArrayList<String>();
        		for (String reviewToken : reviewTokensList){
        			String[] tokenClass = reviewToken.split("_");
        			if(tokenClass[1].startsWith("NN")){
        				currentEntityPhrase.add(tokenClass[0]);
        			} else if (!currentEntityPhrase.isEmpty()) {
        				//entities.add(String.join(" ", currentEntityPhrase));
        				String phraseKey = String.join(" ", currentEntityPhrase);
        				//debug line
        				/*
        				if (currentProperty.equals("Park Plaza Westminster Bridge London")){
        					if (phraseKey.equals("Park Plaza Westminster Bridge London")){
            					System.out.println("PARK DETECTED - " + review);
            					System.out.println("taggedReview - " + taggedReview);
            					System.out.println("tokenClass[0] - " + tokenClass[0]);
            				} else {
            					System.out.println("NONE - " + phraseKey);
            				}
        				}
        				*/
        				
        				//check if phraseKey is part of the reviewed property name
        				if (currentProperty.toLowerCase().indexOf(phraseKey.toLowerCase())==-1){
        					//then you count/score it
        					int phraseUpperCaseCount = 0;
        					for (String s:currentEntityPhrase){
        						if(!s.toLowerCase().equals(s)){
        							phraseUpperCaseCount++;
        						}
        					}
        					int caseMultiplier = 1;
        					if (phraseUpperCaseCount>0 && phraseUpperCaseCount==currentEntityPhrase.size()){
        						caseMultiplier = 10;
        					} else if (phraseUpperCaseCount>0){
        						caseMultiplier = 7;
        					}
        					
        					//phraseKey = phraseKey.toLowerCase();
        					
        					if(currentEntityFreq.containsKey(phraseKey)){ //might have a comparator problem here
            					currentEntityFreq.put(phraseKey, currentEntityFreq.get(phraseKey) + 1*caseMultiplier);
            				} else {
            					currentEntityFreq.put(phraseKey, 1*caseMultiplier);
            				}
        				}
        				
        				
        				currentEntityPhrase.clear();
        			}
        		}
        		
        		//String listEntities = String.join(", ", entities);
        		//writer.write(listEntities);
        		//writer.write("\n");
        		
            }
        	writer.write("Total lines read: " + counter);
        }
        //need an extra blank line at the end or will miss last property and hashmap
 
        // Output the result
        //System.out.println(tagged);
    }
}