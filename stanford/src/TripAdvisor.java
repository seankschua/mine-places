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
 
public class TripAdvisor {
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
        		new FileOutputStream("data/left_oneperreview.txt"), "utf-8"))) {
        	
        	String currentProperty = "start";
        	Map<String,Integer> currentPropertyEntityScore = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        	Map<String,Integer> currentPropertyEntityFreq = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        	
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
            		for (String entity : currentPropertyEntityScore.keySet()){
            			
            			//repeated threshold condition, inverted
            			if(!(currentPropertyEntityFreq.get(entity)>0 && currentPropertyEntityScore.get(entity)/currentPropertyEntityFreq.get(entity) >= 5 && currentPropertyEntityScore.get(entity)>50)){
            				continue;
            			}
            			
            			//lumping misspellings or combining variations here
            			//this will probably eat the most processing time
            			for (String entity2 : currentPropertyEntityScore.keySet()){
            				
            				//weakness here is a snowball -- if there's a 3, 4, 5; the middle will prevail.
            				if(currentPropertyEntityFreq.get(entity)==0 || currentPropertyEntityFreq.get(entity2)==0 || entity.equalsIgnoreCase(entity2)){
                				continue;
                			}
            				
            				if(StringSimilarity.similarity(entity,entity2)>=0.8){
            					System.out.println(entity + " VS " + entity2);
            					int entityFreq = currentPropertyEntityFreq.get(entity);
            					int entityFreq2 = currentPropertyEntityFreq.get(entity2);
            					int entityFreqSum = entityFreq + entityFreq2;
            					int entityFreqScore = currentPropertyEntityScore.get(entity);
            					int entityFreqScore2 = currentPropertyEntityScore.get(entity2);
            					int entityFreqScoreSum = entityFreqScore + entityFreqScore2;
            					
                				if(entityFreq>=entityFreq2){
                					System.out.println(entity + " WINS");
                					currentPropertyEntityFreq.put(entity, entityFreqSum);
                					currentPropertyEntityScore.put(entity, entityFreqScoreSum);
                					currentPropertyEntityFreq.put(entity2,0);
                					currentPropertyEntityScore.put(entity2,0);
                				} else {
                					System.out.println(entity2 + " WINS");
                					currentPropertyEntityFreq.put(entity2, entityFreqSum);
                					currentPropertyEntityScore.put(entity2, entityFreqScoreSum);
                					currentPropertyEntityFreq.put(entity,0);
                					currentPropertyEntityScore.put(entity,0);
                				}
                			}
            			}
            			
            			//threshold here
            			//if (currentPropertyEntityScore.get(entity)>50){
            			if (currentPropertyEntityFreq.get(entity)>0 && currentPropertyEntityScore.get(entity)/currentPropertyEntityFreq.get(entity) >= 5 && currentPropertyEntityScore.get(entity)>50){
            				writer.write(entity + " - " + currentPropertyEntityScore.get(entity) + " - " + currentPropertyEntityFreq.get(entity));
            				writer.write("\n");
            			}
            		}
            		writer.write("WRITTEN AT " + counter + "\n");
            		writer.write("\n\n\n");
        			
        			currentProperty = lineProperty;
        			currentPropertyEntityScore = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        			currentPropertyEntityFreq = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        		}
        		
        		//ArrayList<String> entities = new ArrayList<String>();
        		String[] reviewTokens = taggedReview.split(" ");
        		ArrayList<String> reviewTokensList = new ArrayList<String>(Arrays.asList(reviewTokens));
        		//adding this extra token so that it accounts for a entity that occurs at the end of the sentence.
        		reviewTokensList.add("END_END");
        		ArrayList<String> currentEntityPhrase = new ArrayList<String>();
        		Map<String,Integer> currentLineEntityCollection = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        		String previousTokenPOS = "NO";
        		String previous2TokenPOS = "NO";
        		for (String reviewToken : reviewTokensList){
        			String[] tokenClass = reviewToken.split("_");
        			if(tokenClass[1].startsWith("NN") || (previousTokenPOS.startsWith("NN") && tokenClass[1].startsWith("IN"))){
        				currentEntityPhrase.add(tokenClass[0]);
        			} else if (!currentEntityPhrase.isEmpty()) {
        				
        				if (previousTokenPOS.startsWith("IN") && previous2TokenPOS.startsWith("NN")){
        					currentEntityPhrase.clear();
        					continue;
        				}
        				
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
        				
        				//modifications to the entity phraseKey here
        				//phraseKey = phraseKey.replaceAll("[^a-zA-Z0-9\\s]", "");
        				
        				//check if phraseKey is part of the reviewed property name, cut it out if yes
        				if (currentProperty.toLowerCase().indexOf(phraseKey.toLowerCase())==-1){
        					//adding to the line's collection of nouns/nounphrases
        					//using TreeMap to bypass Case issues
        					//System.out.println("phraseKey is " + phraseKey);
        					if(!currentLineEntityCollection.containsKey(phraseKey)){ //might have a comparator problem here
        						
        						//one weakness here is that the capitalisation multiplier will be influenced by how the reviewer spells the phrase/noun the first time
        						//it doesn't matter when scoring the reviews - the display will be the first mention, but each instance is scored seperately
        						
        						currentLineEntityCollection.put(phraseKey, 1);
            				}
        				}
        				
        				
        				currentEntityPhrase.clear();
        			}
        			if (reviewToken.equals("END_END")){
        				//System.out.println("END_END TRIGGERED");
        				//System.out.println("Line collection size is at " + currentLineEntityCollection.keySet().size());
        				for (String phraseKey : currentLineEntityCollection.keySet()){
        					//debug
        					//System.out.println(phraseKey);
        					
        					//count/scoring it
        					int phraseUpperCaseCount = 0;
        					String[] splitPhraseKey = phraseKey.split(" ");
        					for (String s:splitPhraseKey){
        						if(!s.toLowerCase().equals(s)){
        							phraseUpperCaseCount++;
        						}
        					}
        					int caseMultiplier = 1;
        					if (phraseUpperCaseCount>0 && phraseUpperCaseCount==splitPhraseKey.length){
        						caseMultiplier = 10;
        					} else if (phraseUpperCaseCount>0){
        						caseMultiplier = 7;
        					}
        					       					
        					//phraseKey = phraseKey.toLowerCase();
        					
        					if(currentPropertyEntityScore.containsKey(phraseKey)){ //might have a comparator problem here
            					currentPropertyEntityScore.put(phraseKey, currentPropertyEntityScore.get(phraseKey) + 1*caseMultiplier);
            					currentPropertyEntityFreq.put(phraseKey, currentPropertyEntityFreq.get(phraseKey) + 1);
            				} else {
            					currentPropertyEntityScore.put(phraseKey, 1*caseMultiplier);
            					currentPropertyEntityFreq.put(phraseKey, 1);
            				}
        					
        					//then clear the line collection
        					currentLineEntityCollection = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        				}
        				
        			}
        			previous2TokenPOS = previousTokenPOS;
        			previousTokenPOS = tokenClass[1];
        			
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