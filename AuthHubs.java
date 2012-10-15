package edu.asu.cse494;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AuthHubs {
	HashMap<Integer,Double> authorityval = new HashMap<Integer,Double>();
	HashMap<Integer,Double> hubval = new HashMap<Integer,Double>();
	
	public void Authority(int[] testset)
	{
		//int[] testset = {16971,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		LinkAnalysis link = new LinkAnalysis();
		Set<Integer> baseset= new HashSet<Integer>();
		double threshold = 0.000001;
		double[][] hubmatrix;
		double[][] hubprematrix;
		double[][] autmatrix;
		double[][] autprematrix;
		for(int i=0;i<testset.length;i++)
		{
			baseset.add(testset[i]);
			int[] x=link.getLinks(testset[i]);
			for(int z=0;z<x.length;z++)
			{
				baseset.add(x[z]);
			}
			x=link.getCitations(testset[i]);
			for(int z=0;z<x.length;z++)
			{
				baseset.add(x[z]);
			}
		}
		
		
		/***sorting baseset******/
		
		List<Integer> sortedList = asSortedList(baseset);
		int x=sortedList.size();
		int[][] adjacency = new int[x+1][x+1];
		int[][] adjtranspose = new int[x][x];
		for(int i=1;i<x+1;i++)
		{
			adjacency[0][i] =sortedList.get(i-1);
			adjacency[i][0] =sortedList.get(i-1);
			
		}
		//System.out.println(sortedList.get(x-1));
		for(int i=1;i<x+1;i++)
		{
			int[] n = link.getLinks(adjacency[i][0]);
			for(int j=0;j<n.length;j++)
			{
				for(int b=1;b<=x;b++)
				if(n[j]==adjacency[0][b])
				{
					adjacency[i][b]=1;
				}
			}
			
		}
		
		
		/**********Adjacency Transpose calculation starts here***************************/
		for(int i=1;i<x+1;i++)
		{
			for(int j=1;j<x+1;j++)
			{
				adjtranspose[j-1][i-1] = adjacency[i][j];
			}
			
			
		}
		/*********Transpose calculation ends*****************/
		/*
		for(int i=0;i<x;i++)
		{
			for(int j=0;j<x;j++)
			{
				System.out.print("  "+adjtranspose[i][j]+"  ");
			}
			System.out.println("\n");
			
		}
		*/
		/**********Hub matrix declaration*****************/
		hubmatrix = new double[x][1];
		autmatrix = new double[x][1];
		hubprematrix = new double[x][1];
		autprematrix = new double[x][1];
		for(int i=0;i<x;i++)
		{
			hubmatrix[i][0] =1.0;
			autmatrix[i][0] =0.0;
		}
		/*
		for(int i=0;i<x;i++)
		{
			System.out.println(hubmatrix[i][0]);
		}
		*/
		/*******Hub matrix calculation done*******/
		
		/********Authority and Hub Matrix multiplication starts here******/
		//System.out.println(autmatrix[1].length);
		
			for(int len=0;len<200;len++)
			{
				double norm=0;
				double norm1=0;
				 for(int i = 0; i < x; i++) {
					  for(int j = 0; j < 1; j++) {
					  for(int k = 0; k < x; k++){
					  
					  autmatrix[i][j]+= adjtranspose[i][k]*hubmatrix[k][j];
					  norm+=autmatrix[i][j]*autmatrix[i][j];
					  
					   }
					  }  
					 }
				 for(int i = 0; i < x; i++) {
					  for(int j = 0; j < 1; j++) {
					  for(int k = 0; k < x; k++){
					 					    
					  hubmatrix[i][j]+= adjtranspose[i][k]*autmatrix[k][j];
					  norm1+=hubmatrix[i][j]*hubmatrix[i][j];
					  }
					  }  
					 }
				
				 for(int i=0;i<x;i++)
				 {
					 for(int j = 0; j < 1; j++) 
					 {
						// System.out.println(autmatrix[i][j]);
						 autmatrix[i][j] = (double) (autmatrix[i][j]/Math.sqrt(norm));
						 hubmatrix[i][j] = (double) (hubmatrix[i][j]/Math.sqrt(norm1));
					 }
				 }
				
				 double hubcount =0;
				 double autcount =0;
				 
				 for(int i=0;i<x;i++)
				 {
					 
					 for(int j = 0; j < 1; j++) {
						 
						// System.out.println(autmatrix[i][j]);
						 hubcount+= (hubmatrix[i][j]-hubprematrix[i][j])*(hubmatrix[i][j]-hubprematrix[i][j]);
						 autcount+= (autmatrix[i][j]-autprematrix[i][j])*(autmatrix[i][j]-autprematrix[i][j]);
						 /*
						 if(hubprematrix[i][j]==hubmatrix[i][j]&&autprematrix[i][j]==autmatrix[i][j])
						 {
							 count=1;
						 }
						 else
							 break;
							 */
					 }
					// System.out.println("\n");
				 }
				// System.out.println("Iteration "+len);
				 if(hubcount<threshold && autcount<threshold)
				 {
					 System.out.println(len);
					 break;
					 
				 }
				
				 for(int i=0;i<x;i++)
				 {
					 for(int j = 0; j < 1; j++) {
						 hubprematrix[i][j]=hubmatrix[i][j];
						 autprematrix[i][j]=autmatrix[i][j];
					 }
				 }
			}
			
			
		/**********Storing authority and hub values in hashmap********************/	
			//HashMap<Integer,Double> authorityval = new HashMap<Integer,Double>();
			//HashMap<Integer,Double> hubval = new HashMap<Integer,Double>();
			
			
		
		 for(int i=0;i<x;i++)
			{
				for(int j=0;j<1;j++)
				{
					//System.out.print("  "+autmatrix[i][j]+"  ");
					authorityval.put(sortedList.get(i), autmatrix[i][j]);
					hubval.put(sortedList.get(i), hubmatrix[i][j]);
				}
				//System.out.println("\n");
				
			}
		// System.out.println(authorityval);
		
		
		/********Authority and Hub Matrix multiplication ends here******/
			
		
	}
	
	public Map<Integer, Double> getAutorityRank()
	{
		return sortByValues(authorityval);
	}
	
	public Map<Integer, Double> getHubRank()
	{
		return sortByValues(hubval);
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
	
	
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	public static void main(String[] args)
	{
		AuthHubs auth = new AuthHubs();
		int[] testset = {16971,16674,18361,16903,16859,15138,16278,18343,14663,16910};
		auth.Authority(testset);
	}
}
