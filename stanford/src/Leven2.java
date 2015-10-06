import java.util.Arrays;

public class Leven2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LevenshteinDistance("cat","cate");
		
	}
	
	public static int LevenshteinDistance (CharSequence lhs, CharSequence rhs) {                          
	    int len0 = lhs.length() + 1;                                                     
	    int len1 = rhs.length() + 1;                                                     
	                                                                                    
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	                                                                                    
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	                                                                                    
	    // dynamically computing the array of distances                                  
	                                                                                    
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {
	    	
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;
	        
	        System.out.println("cost: " + Arrays.toString(cost));
	    	System.out.println("newcost: " + Arrays.toString(newcost));
	                                                                                    
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             
	                                                                                    
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;    
	            
	            System.out.println("i: " + i + ", j: " + j);
	            System.out.println("cost_replace: (cost" + (i-1) + ")" + cost[i - 1] + " + " + match + " = " + (cost_replace));
	            System.out.println("cost_insert: (cost" + (i) + ")" + cost[i] + " + 1 = " + (cost_insert));
	            System.out.println("cost_delete: (newcost" + (i-1) + ")" + newcost[i - 1] + " + 1 = " + (cost_delete));
	                                                                                    
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	            
	            System.out.println("newcost: " + Arrays.toString(newcost));
	        }                                                                           
	                                                                                    
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	                                                                                    
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
	}

}
