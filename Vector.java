/*********Class to define vector calculations********/
package edu.asu.cse494;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.lucene.index.IndexReader;
import com.lucene.index.Term;
import com.lucene.index.TermDocs;
import com.lucene.index.TermEnum;


public class Vector{
	int count=0;
	int totalDoc;
	HashMap<Integer,Double> docMod = new HashMap<Integer,Double>();
	HashMap<Integer,Double> tfidfDocMod = new HashMap<Integer,Double>();
	
	public void  showVector()
	{
		try{
			IndexReader reader = IndexReader.open("result3index");
			totalDoc = reader.numDocs();
			// use the TermEnum object to iterate through all the terms in the index
			TermEnum termenum = reader.terms();
			System.out.println("Preparing your search engine ready for the first time use, it may take few moments...\n");
			
			/***********calculating TF |Mod| value************************************/
			while(termenum.next())
			{
				count=0;
				Term termval = termenum.term();
				 TermDocs termdocs = reader.termDocs(termval);
					
				 	while(termdocs.next())
					{
						count++;
												
						if(docMod.containsKey(termdocs.doc()))
						{
							docMod.put(termdocs.doc(),docMod.get(termdocs.doc())+(termdocs.freq()*termdocs.freq()));
						}
						else
						{
							docMod.put(termdocs.doc(), (double) termdocs.freq()*termdocs.freq());
						}
						
					}
				 	
				    /*************code for creating an index and |D| with tf-idf starts*************************************************/
					
				    TermDocs termdocsIdf = reader.termDocs(termval);
				    Double m=1.0;
				    if(count!=0)
				    {
				    m = Math.log(totalDoc/count);
				    }
				  while(termdocsIdf.next())
					{
						
						double temp=termdocsIdf.freq()*m;
						
						if(tfidfDocMod.containsKey(termdocsIdf.doc()))
						{
							tfidfDocMod.put(termdocsIdf.doc(),tfidfDocMod.get(termdocsIdf.doc())+temp*temp);
						}
						else
						{
							tfidfDocMod.put(termdocsIdf.doc(), (double) temp*temp);
						}
						
					}
				
				
			
			}
			Collection c = docMod.values();
			Iterator itr = c.iterator();
			int x=0;
		    while(itr.hasNext())
		    {
		    	docMod.put(x, 1/(Math.sqrt(docMod.get(x))));
		    	tfidfDocMod.put(x, 1/(Math.sqrt(tfidfDocMod.get(x))));
		    	x++;
		    	itr.next();
		    }
		//System.out.println(docMod);
		//System.out.println(tfidfDocMod);
		    /***************************Tf Mod value calculation ends*****************/
		    
		/***************************Tf-Idf Mod value creation starts here*******/
		
	/*************Tf-Idf mod value creation ends********************/
		}
		catch(IOException e){
		    System.out.println("IO Error has occured: "+ e);
		    return;
		}
	}

	/************code for searching user queries with tf
	 * @return ***********************************/
		public Map<Integer, Double> searchQuery(String stg) {
			// TODO Auto-generated method stub
			 HashMap<Integer, Double>cosMat = new HashMap<Integer, Double>();
			try {
			HashMap<String,Integer> queryTf = new HashMap<String,Integer>();
			HashMap<Integer,Double> DotPro = new HashMap<Integer,Double>();
			IndexReader reader = IndexReader.open("result3index");
			double queryMod = 0;
			String query[] = stg.split(" ");
			/********************Term frequency of query*************************************/
			
			for(int i=0; i<query.length;i++)
			{
				Term qTerm = new Term("contents", query[i]);
				TermDocs qtermdocs = reader.termDocs(qTerm);
				if(qtermdocs != null)
				{
					if(queryTf.containsKey(query[i]))
					{
						queryTf.put(query[i],queryTf.get(query[i])+1);
					}
					else
					{
						queryTf.put(query[i],1);
					}
				}
			}
			/*********************Mod value of query*******************************************/
			
			Iterator it = queryTf.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		         
			        /*********query dot product calculation starts**************************/
			     
		        	 String k=pairs.getValue().toString();
		 	        queryMod = queryMod+Double.parseDouble(k)*Double.parseDouble(k);
		 	       Term qTerm2 = new Term("contents",pairs.getKey().toString());
					TermDocs qtermdocs1 = reader.termDocs(qTerm2);
					if(qtermdocs1 != null)
					{
							while(qtermdocs1.next())
							{
								// iterating through all the indexes
							       	
							        if(DotPro.containsKey(qtermdocs1.doc()))
									{
							        	
										DotPro.put(qtermdocs1.doc(),DotPro.get(qtermdocs1.doc())+(qtermdocs1.freq()*queryTf.get(qTerm2.text())));
									}
									else
									{
										DotPro.put(qtermdocs1.doc(),(double) (qtermdocs1.freq()*queryTf.get(qTerm2.text())));
									}
								}
						
					}
			        /*********query dot product calculation ends**************************/
		        
		    }
		    queryMod = 1/Math.sqrt(queryMod);
		    /*******query mod value calculation ends********/
		    
		    /*************************** cosine similarity calculation starts****************/
		   
		    
		    
		    int z=0;
		    Iterator cosIt = DotPro.entrySet().iterator();
		    while (cosIt.hasNext()) // iterating through all the indexes
		        {
		    	Map.Entry pair = (Map.Entry)cosIt.next();
		    	z++;
		    		double x = Double.parseDouble(pair.getValue().toString())*queryMod*docMod.get(pair.getKey());
		    		cosMat.put((Integer) pair.getKey(), x);
		    		
		        }
		    /*************************** cosine similarity calculation ends****************/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return sortByValues(cosMat);
		}
		
		/************code for searching user queries with tf-idf
		 * @return 
		 * @return 
		 * @return ***********************************/
			public Map<Integer, Double> searchTfidfQuery(String stg) {
				
				// TODO Auto-generated method stub
				HashMap<Integer, Double>cosMat = new HashMap<Integer, Double>();
			try{
				IndexReader reader = IndexReader.open("result3index");
				HashMap<String,Integer> queryTf = new HashMap<String,Integer>();
				HashMap<Integer,Double> DotPro = new HashMap<Integer,Double>();

				double queryMod = 0;
				String query[] = stg.split(" ");
				/********************Term frequency of query*************************************/
				for(int i=0; i<query.length;i++)
				{
					Term qTerm = new Term("contents", query[i]);
					TermDocs qtermdocs = reader.termDocs(qTerm);
					if(qtermdocs != null)
					{
						if(queryTf.containsKey(query[i]))
						{
							queryTf.put(query[i],queryTf.get(query[i])+1);
						}
						else
						{
							queryTf.put(query[i],1);
						}
					}
				}
				System.out.println(queryTf);
				/*********************Mod value of query*******************************************/
				
				Iterator it = queryTf.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        
				        /*********query dot product calculation starts**************************/
				    String k=pairs.getValue().toString();
		 	        queryMod = queryMod+(Double.parseDouble(k)*Double.parseDouble(k));
		 	    
		 	       Term qTerm2 = new Term("contents",pairs.getKey().toString());
		 	  					TermDocs qtermdocs1 = reader.termDocs(qTerm2);
		 	  					if(qtermdocs1 != null)
		 	  					{
		 	  							while(qtermdocs1.next())
		 	  							{
		 	  								int count=0;
		 	  								// iterating through all the indexes
		 	  								Term qTerm3 = new Term("contents",pairs.getKey().toString());
		 	  								TermDocs qtermdocs3 = reader.termDocs(qTerm3);
		 	  								while(qtermdocs3.next())
		 	  								{
		 	  									count++;
		 	  								}
		 	  									double m = qtermdocs1.freq()*Math.log(totalDoc/count);
		 	  							       	
		 	  							        if(DotPro.containsKey(qtermdocs1.doc()))
		 	  									{
		 	  							        	
		 	  										DotPro.put(qtermdocs1.doc(),DotPro.get(qtermdocs1.doc())+(m*queryTf.get(qTerm2.text())));
		 	  									}
		 	  									else
		 	  									{
		 	  										DotPro.put(qtermdocs1.doc(),(double) (m*queryTf.get(qTerm2.text())));
		 	  									}
		 	  								}
		 	  						
		 	  					}
		 	  			        /*********query dot product calculation ends**************************/
		 	  		        
		 	  		    }
		 	  		    queryMod = 1/Math.sqrt(queryMod);
		 	  		     
		 	  		    /*******query mod value calculation ends********/
		 	  		 
	
			    
			    /*************************** cosine similarity calculation starts****************/
			   
			    
			    Iterator cosIt = DotPro.entrySet().iterator();
			    while (cosIt.hasNext()) // iterating through all the indexes
			        {
			    	Map.Entry pair = (Map.Entry)cosIt.next();
			    	
			    		double x = (double) (Float.parseFloat(pair.getValue().toString())*queryMod*tfidfDocMod.get(pair.getKey()));
			    		cosMat.put((Integer) pair.getKey(), x);
			    		
			        }
			    /*************************** cosine similarity calculation ends****************/
			   
			 }
			catch(Exception e)
				{
				
				}
			 return sortByValues(cosMat); 
			}
			/************code for searching user queries with tf-idf ends***********************************/


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

		/*************function for checking the input **********************************/
		public static boolean isInteger(String s){

		    if(s.isEmpty())return false;
		    for (int i = 0; i <s.length();++i){
		        char c = s.charAt(i);
		        if(!Character.isDigit(c) && c !='-')
		            return false;
		    }

		    return true;
		}
		
		/******************************************************************************************************
		 * 
		 *  	main function starts here
		 ******************************************************************************************************/
		public static void main(String[] args)
		{
			Vector CSE494Viewer = new Vector();
			long start1 = System.currentTimeMillis();
			CSE494Viewer.showVector();
			System.out.println("Mod calculations done in " +(System.currentTimeMillis()-start1)/1000F+"seconds");
	  			     
			int div =0,rotate=0;
			System.out.println("Search engine is ready to use now:");
			System.out.println("Search using:");
			System.out.println("1: TF");
			System.out.println("2: TF/IDF");
			System.out.print("Please select (1/2):");
			String input="";
			
					BufferedReader in1 = new BufferedReader(new InputStreamReader(System.in));
					 try {
						 while(true)
						 {
							 
							 if(isInteger(input=in1.readLine()))
							 {	 
								 div =Integer.parseInt(input);
								 if(div == 1 || div == 2)
								 	{
									 rotate=1;
									 break;
								 	}
								 else
									 System.out.print("Please select a correct option(1/2):");
							 }
							 else
							 {
								 System.out.print("Please select a correct option(1/2):");
								 
							 }
						 }
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("Error: please enter an option");
						e1.printStackTrace();
					}
			 BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			 try {
				 IndexReader reader = IndexReader.open("result3index");
					
				 while (true) 
		     
				 	{
					 div=0;
					 if(rotate>1)
					 {
						 	System.out.println("\nSearch using:");
							System.out.println("1: TF");
							System.out.println("2: TF/IDF");
							System.out.print("Please select (1/2):");
						 while(true)
						 {
							 if(isInteger(input=in1.readLine()))
							 {
								 div =Integer.parseInt(input);
								 if(div == 1 || div == 2)
								 	{
									 break;
								 	}
								 else
									 System.out.print("Please select a correct option(1/2):");
							 }
							 else
							 {
								 System.out.print("Please select a correct option(1/2):");
								 
							 }
						 }
					 }
					 
					 System.out.print("Query: ");
					 String line;
		    	  		line = in.readLine();
		    	  		line=line.toLowerCase();
							    	  		
		    	  		if (line.length() == -1)
		    				  break;
		    	  		
		    	  		 Map<Integer, Double>cosMat = new HashMap<Integer, Double>();
		    	  		long start = System.currentTimeMillis();
		    	  			switch(div)
		    	  			{
		    	  			case 1:cosMat= CSE494Viewer.searchQuery(line); break;
		    	  			case 2: cosMat= CSE494Viewer.searchTfidfQuery(line); break;
		    	  			default: cosMat= CSE494Viewer.searchQuery(line); break;
		    	  			}
		    	  		System.out.println(cosMat.size()+" Total matching documents shown in:" +(System.currentTimeMillis()-start)/1000F+"seconds");
		    	  		rotate++;
		    	  		/**************Displaying results:*****************************************/
		    	  		Iterator res = cosMat.entrySet().iterator();
		    	  		int count=0,val=0;
		    	  		
		    		    while (res.hasNext()) // iterating through all the indexes
		    		        {
		    		    	Map.Entry pair = (Map.Entry)res.next();
		    		    	count++; val++;
		    		    	System.out.println(val+") "+ reader.document(Integer.parseInt(pair.getKey().toString())).get("url")+ " : Document no: "+pair.getKey()+" : similarity: "+ pair.getValue());
		    		    	
		    		    	if(count>=10)
		    		    		{
		    		    		count=0;
		    		    		System.out.println("More?(Y/N)");
		    		    		BufferedReader out = new BufferedReader(new InputStreamReader(System.in));
		    		    		String ch = out.readLine().toLowerCase();
		    		    		if(ch.charAt(0)=='n')
		    		    			{
		    		    			 break;
		    		    			}
		    		    		}
		    		        }
		    		
					}
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
		}
		
	}
