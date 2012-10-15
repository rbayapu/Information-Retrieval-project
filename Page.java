package edu.asu.cse494;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

public class Page {
	double[] rank = new double[25053];
	double[] previousrank = new double[25053];
	double threshold = 0.0001;
	public static void main(String[] args)
	{
		Page p = new Page();
		//p.pagerank();
		p.pageRankVectorSpace();
	}
	
	public double getPageRank(int documentnumber)
	{
		pagerank();
		return rank[documentnumber];
	}
	
	public void pagerank()
	{
		LinkAnalysis link =new LinkAnalysis();
		Hashtable<Integer,LinkedList> hash = new Hashtable<Integer,LinkedList>();
		Hashtable<Integer,Double> pagerank = new Hashtable<Integer, Double>();
		//Hashtable<Integer,Double> pagerankpre = new Hashtable<Integer, Double>();
		
		/****************first iteration. crawling all the nodes and initializing default page ranks*****************************/
		 for(int i=0;i<25053;i++)
			{
				LinkedList l = new LinkedList();
				int[] n = link.getLinks(i);
								
				if(n.length>0)
				for(int k=0;k<n.length;k++)
				{
					l.add(n[k]);
				}
				hash.put(i, l);
				double a =1.0/25053;
				pagerank.put(i,a);
				//pagerankpre.put(i,0.0);
				rank[i]=a;
				previousrank[i]=0.0;
				//page.put(i, mno);
							//System.out.println(hash.get(0));
				
				//System.out.println(pagerank.get(i));
				
			}
		 /**************Calculating page rank of all the nodes | removing sink nodes*****************************/
		 for(int i=0;i<100;i++)
		 {
			 for(int page=0;page<25053;page++)
			 {
				// LinkedList lm = hash.get(page);
				 int[] forwardlinks = link.getLinks(page);
					if(forwardlinks.length!=0)
					{
						for(int len=0;len<forwardlinks.length;len++)
						{
							
							rank[forwardlinks[len]]+=rank[page]/forwardlinks.length;
						}
					}
					else
					{
						rank[page]+=rank[page]/25053;
					}
				 
			 }
			 
			 /********adding damping factor for the pageranks*******************/
				double converge=0.0;
				float dampingfactor=(float) 0.4;
				double dam = (double)(1.0-dampingfactor)/25053;
				
				for(int z=0;z<25053;z++)
				{
					rank[z]= dampingfactor*rank[z]+dam;
					//converge+=(rank[z]-previousrank[z])*(rank[z]-previousrank[z]);
					//System.out.println(rank[i]);
				}
				double normvalue = getMaxValue(rank);
				 for(int n=0;n<25053;n++)
				 {
					// System.out.println(i+" : "+rank[i]);
					 rank[n]=rank[n]/normvalue;
					 converge+=(rank[n]-previousrank[n])*(rank[n]-previousrank[n]);
					// System.out.println(rank[i]);
				 }
				
				/****converge test*******/
				if(converge<threshold)
				{
					System.out.println(i);
					break;
				}
				
				for(int z=0;z<25053;z++)
				{
					previousrank[z]=rank[z];
				}
				
				
				
		 }
		 
		 	//Arrays.sort(rank);
			
			//System.out.println(temp);
		 double temp=1.0;
		 int z=0;
		 double normvalue = getMaxValue(rank);
		 for(int i=0;i<25053;i++)
		 {
			// System.out.println(i+" : "+rank[i]);
			 rank[i]=rank[i]/normvalue;
			// System.out.println(i+" : "+rank[i]);
		 }
		 
		 //System.out.println(getMaxValue(rank));
		 
	}//pageRank function ends here
	
	/************Page Rank Vector Space function calculation starts here *******************************/
	public void pageRankVectorSpace()
	{
		//int[] testset = {16971,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		int[] testset = {9047,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		Double weight=1.0;
		HashMap<Integer,Double> combinedScore = new HashMap<Integer, Double>();
			for(int i=0;i<testset.length;i++)
			{
				Double pagerank = getPageRank(i);
				Double temp = (weight*pagerank)+((1-weight)*testset[i]);
				combinedScore.put(testset[i], temp);
			}
		
			System.out.println(combinedScore);
		
		
	}
	/************Page Rank Vector Space ends*******************************/
	
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
