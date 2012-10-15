package edu.asu.cse494;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class PageRankProb {
	
	double[] previousrank = new double[25053];
	double[] nextrank=new double[25053];
	double threshold=0.000001;
	
	public static void main(String[] args)
	{
		float dampingfactor = (float) 0.85;
		PageRankProb p = new PageRankProb();
		p.pagerank(dampingfactor);
		
	}
	public double getPageRank(int documentnumber,float dampingfactor)
	{
		
		pagerank(dampingfactor);
		return nextrank[documentnumber];
	}
	
	Double weight=0.4;
	/************Page Rank Vector Space function calculation starts here 
	 * @return *******************************/
	public Map<Integer, Double> pageRankVectorSpace(int[] testset,Double[] testsetsim,Double weight)
	{
		//int[] testset = {16971,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		//int[] testset = {9047,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		
		HashMap<Integer,Double> combinedScore = new HashMap<Integer, Double>();
			for(int i=0;i<testset.length;i++)
			{
				Double pagerank = getPageRank(testset[i],(float) 0.85);
				Double temp = (weight*pagerank)+((1-weight)*testsetsim[i]);
				combinedScore.put(testset[i], temp);
			}
			return sortByValues(combinedScore);
			//System.out.println(combinedScore);
		
		
	}
	/************Page Rank Vector Space ends*******************************/
	
	
	/**********code for finding the page rank of document corpus*******************/
	public void pagerank(float dampingfactor)
	{
		LinkAnalysis link =new LinkAnalysis();
		
		/****************first iteration. initializing default page ranks*****************************/
		//double a =1.0/25053;
		 for(int i=0;i<25053;i++)
			{
				
				nextrank[i]=1.0/25053;
				previousrank[i]=1.0/25053;
				//temprank[i]=1.0/25053;
			}
		 int count=0;
		while(true)
		{
			 /**************computing page rank of all nodes here****/
			double sinktotal=0.0;
			double nonprob=0.0;
			double[] newrank = new double[25053];
			 for(int i=0;i<25053;i++)
				{
				 nonprob+=previousrank[i];
				 	int[] forwardlinks = link.getLinks(i);
				 	if(forwardlinks.length!=0)
					{
						for(int len=0;len<forwardlinks.length;len++)
						{
							int doc= forwardlinks[len];
							newrank[doc]+=previousrank[i]/forwardlinks.length;
							
						}
					}
					else
					{
						sinktotal+=previousrank[i]/25053;
					}
				 
				
				}
			 double temp=0.0;
			 for(int i=0;i<25053;i++)
				{
				
				 nextrank[i]=(sinktotal+newrank[i])*(dampingfactor)+((1-dampingfactor)*nonprob/25053);
				 
				 temp+=(nextrank[i]-previousrank[i])*(nextrank[i]-previousrank[i]);
				 
				}
			
			 count++;
			 temp=Math.sqrt(temp);
			 if(temp<threshold)
			 {
				// System.out.println("converged at:"+count);
				 break;
				 
			 }
			 else
			 {
				 for(int i=0;i<25053;i++)
				 {
					 previousrank[i]=nextrank[i];
				 }
			 
			 }
		}
		double normvalue=getMaxValue(nextrank);
		for(int i=0;i<25053;i++)
		{
			nextrank[i]=nextrank[i]/normvalue;
			//System.out.println(nextrank[i]);
		}
		 
		
	}
	
	/****************custom method for sorting the map list****************************************************/
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
    	Comparator<K> valueComparator =  new Comparator<K>() {
    	    public int compare(K k1, K k2) {
    	        int compare = map.get(k2).compareTo(map.get(k1));
    	        if (compare == 0) return 1;
    	        else return compare;
    	    }
    	};
    	Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
    	sortedByValues.putAll(map);
    	
    	return sortedByValues;
    }
	
	
	/************Function for getting max and min value in an array *************************/
	public static double getMaxValue(double[] numbers){  
		  double maxValue = numbers[0];  
		  for(int i=1;i < numbers.length;i++){  
		    if(numbers[i] > maxValue){  
		      maxValue = numbers[i];  
		    }  
		  }  
		  return maxValue;  
		}  
		  
		public static double getMinValue(double[] numbers){  
		  double minValue = numbers[0];  
		  for(int i=1;i<numbers.length;i++){  
		    if(numbers[i] < minValue){  
		      minValue = numbers[i];  
		    }  
		  }  
		  return minValue;  
		} 
		
}
