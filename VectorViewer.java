package edu.asu.cse494;
import com.lucene.analysis.Analyzer;

import com.lucene.analysis.StopAnalyzer;
import com.lucene.analysis.TokenStream;
import com.lucene.document.Document;
import com.lucene.index.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;


public class VectorViewer {
	int count_doc=0;
	int count_term=0;
	int temp=0;
	int k=50;
	String tf_idf_applet="The Top 10 results using TF-IDF values for Vector Space Model  are"+"\n\n\n";
	static ArrayList<Integer> tf_idf= new ArrayList<Integer>();
	Hashtable<Integer,Double>  hash_tf= new Hashtable<Integer,Double> ();
	Hashtable<Integer,Double>  hash_tf_idf= new Hashtable<Integer,Double> ();
	Hashtable<Integer,Double> hashforVectorSpace= new Hashtable<Integer,Double> ();
	String line="";
	double DocD[]=new double[25053];
	ArrayList<Integer>[] store = new ArrayList[25053];
	Hashtable<Integer,Double>[] ht_term_freq=new Hashtable[115875];
	String[] terms= new String[115875];
	double  idfVal[]= new double[115875];
	double DocD_tf_idf[]=new double[25053];
	Hashtable<String,Double> hashtable_term_idf = new Hashtable<String,Double>();
	
	//static ArrayList<Integer> tf_idf_all=new ArrayList<Integer>();
	//display the vector
	public void  showVector()
	{
		//System.out.println("query is"+line);
		// lists the vector
		for(int i=0;i<25053;i++)
		{
			store[i]= new ArrayList<Integer>();
		}
		try{
			long t1=System.currentTimeMillis();
			IndexReader reader = IndexReader.open("result3index");
			//System.out.println(" Number of Docs in Index :" + reader.numDocs());
			

			
			//check time after parsing the query
			long t3=System.currentTimeMillis(); 
			int num_docs=reader.numDocs();
			double num_docs_d=reader.numDocs();
			
			
			
			// use the TermEnum object to iterate through all the terms in the index
			
			TermEnum termenum = reader.terms();
			//System.out.println("Printing the Terms and the Frequency \n");
			
			//To store the squares of the term frequencies
			//DocD=new double[num_docs];
			
			//double DocD_tf_idf[]=new double[num_docs];
		
			int temp=0;	
			
			//Iterate through all the terms within the field->"contents"
			while( termenum.next() && termenum.term().field().equals("contents"))
			{
				ht_term_freq[count_term]= new Hashtable<Integer, Double>();
				temp++;
				
				Term termval = termenum.term();
				//System.out.println("The Term :" + termval.text() + " and # of docs containing the term :"+termenum.docFreq());
				//Storing the term<-->IDF in the hash table
				double idf = Math.log(num_docs_d/termenum.docFreq());
				//System.out.println(idf);
				hashtable_term_idf.put((String)termval.text(),idf);
				terms[count_term]=(String)termval.text();
				idfVal[count_term]= idf;
				//
				TermDocs termdoc= reader.termDocs(termval);
				int count=0;
				
				//Iterate through all the documents containing the specific termval term; 
				while(termdoc.next())
				{
					int docnum=termdoc.doc();
					//int last=gotoLast(store[docnum]);
					store[docnum].add(count_term);
					count_doc++;
					//calculating the |D|---adding the squares of d`s
					DocD[termdoc.doc()]=(double) (DocD[termdoc.doc()]+(double)(Math.pow(termdoc.freq(), 2)));
					//calculating the |D| using the IDF
					double idf_cal=((Double) hashtable_term_idf.get(termval.text().toString())).doubleValue();
					DocD_tf_idf[termdoc.doc()]=(double) (DocD_tf_idf[termdoc.doc()]+(double)(Math.pow(termdoc.freq()*idf_cal, 2)));
					count++;
					ht_term_freq[count_term].put(termdoc.doc(), (double)(termdoc.freq()*idf_cal));
						
				}
				count_term++;
			}
			long t4=System.currentTimeMillis(); 
			
		
			//Getting the square root so that the |D| can be calculated.
			for(int i=0;i<num_docs;i++)
			{
				DocD[i]=(double) Math.sqrt(DocD[i]);
				DocD_tf_idf[i]=(double) Math.sqrt(DocD_tf_idf[i]);
			}
			
			//Arrays to store the Similarity Matrix
			double SimD_tf[]= new double[num_docs];
			double SimD_tf_idf[]= new double[num_docs];
			
			
			//Parsing the Query
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Query: ");
			
			String line = in.readLine();
			
			//Split the query into individual words
			String[] parse_String=line.split("\\s");
			
			//Setting the value of k
			//setK();
			
			for(int j=0;j<parse_String.length;j++)
			{
				String str_search=parse_String[j];
				
				//System.out.println("The term is "+str_search);
				
				
				//Terms are indexed as lowercase letters and hence changing the case.
				str_search=str_search.toLowerCase();
				
				//Check if the term search exists in the hash table or not
				if(hashtable_term_idf.containsKey(str_search))
				{
					//System.out.println("term found is "+str_search);
					Term term_search = new Term("contents",str_search);
					
					TermDocs search_TD= reader.termDocs(term_search);
					//System.out.println("term is "+term_search+" the IDF is"+hashtable.get(str_search).toString());
	
					//System.out.println("The IDF of the term= "+((Double) hashtable_term_idf.get(str_search)).floatValue());
					
					int count2=0;
					//iterate through the documents containing the specific term.
					while(search_TD.next())
					{
						//System.out.println("Document id is "+search_TD.doc()+"  # of occurrences of term in the doc= "+search_TD.freq());
						//Calculation of the Similarity Matrix-just tf distance
						SimD_tf[search_TD.doc()]=SimD_tf[search_TD.doc()]+(1*search_TD.freq());
						//System.out.println("Un-normalized= "+(float)SimD_tf[search_TD.doc()]);
						
						//Calculation of the Similarity Matrix- tf_IDF distance
						double idf_final=((Double) hashtable_term_idf.get(str_search)).doubleValue();
						SimD_tf_idf[search_TD.doc()]=SimD_tf_idf[search_TD.doc()]+(1*search_TD.freq()*idf_final);
						//System.out.println("Document ID= "+search_TD.doc()+" TF is "+search_TD.freq());
						count2++;
					}
					//System.out.println("Number of results found= "+count2);
					
				}	
				else
				{
					System.out.println("Term Doesnot Exist, please re-enter a new term");
				}	
			}
			
			Map<Double,Double> map_sort_tf=new HashMap<Double,Double>();
			Map<Double,Double> map_sort_tf_idf=new HashMap<Double,Double>();
			
			//Hashtable hash_tf= new Hashtable();
			//Hashtable hash_tf_idf= new Hashtable();
			Object[] myArray_tf;
			Object[] myArray_tf_idf;
			MyComparator comparator= new MyComparator();
			
			
			for(int i=0;i<num_docs;i++)
			{
				
				if(SimD_tf[i]!=0)
				{
					SimD_tf[i]=SimD_tf[i]/DocD[i];
					/*if(i==1134)
						System.out.println("yes 1134 for tf");*/
					map_sort_tf.put((double) i,(double)(SimD_tf[i]));
					//hash_tf.put(i,(double)(SimD_tf[i]));
					//System.out.println("Simtfidf="+map_sort_tf.get(i));
					hash_tf.put(i,SimD_tf[i]);
				}
				else
				{
					SimD_tf[i]=-999;
					//System.out.println("Simtfidf="+SimD_tf[i]);
				}
				if(SimD_tf_idf[i]!=0)
				{
					
					SimD_tf_idf[i]=SimD_tf_idf[i]/DocD_tf_idf[i];
					map_sort_tf_idf.put((double) i,(double)(SimD_tf_idf[i]));
					//hash_tf_idf.put(i, (double)(SimD_tf_idf[i]));
					
					hash_tf_idf.put(i,SimD_tf_idf[i]);
				}
				else
				{
					SimD_tf_idf[i]=-999;
				}
			}
			
			myArray_tf=hash_tf.entrySet().toArray();
			Arrays.sort(myArray_tf,(Comparator)comparator);
			
			//System.out.println("TF Distance");
			
			int count_tf=0;
			for(int i=0;i<myArray_tf.length;i++)
			{
				count_tf++;
				//System.out.println("DocID ="+((Map.Entry)myArray_tf[i]).getKey() + "TF Value= "+(((Map.Entry)myArray_tf[i]).getValue()));
				if(count_tf>=1)
					break;
			}
			
			//System.out.println("TF-IDF Distance");
			
			myArray_tf_idf=hash_tf_idf.entrySet().toArray();
			Arrays.sort(myArray_tf_idf,(Comparator)comparator);
			
		//	UrlParser urlp=new UrlParser();
			
			
			int count_tf_idf=0;
			for(int i=0;i<myArray_tf_idf.length;i++)
			{
				int docid=(Integer) ((Map.Entry)myArray_tf_idf[i]).getKey();
				double key=(Double) (((Map.Entry)myArray_tf_idf[i]).getValue());
				
				//System.out.println("DocID ="+docid+"TF-IDF Value= "+key);
				//tf_idf_all.add(docid);
				hashforVectorSpace.put(docid, key);
				count_tf_idf++;
				//UrlParser urlp= new UrlParser(); 
				if(count_tf_idf<=k)
				{
					String url=reader.document(docid).getField("url").toString();
					tf_idf.add(docid);
				//	 tf_idf_applet+="doc#= "+docid+" url is:  "+urlp.returnUrl(url)+"\n";
					// System.out.println("doc#= "+docid+" url is:  "+urlp.returnUrl(url));
				}
			}
			
			
			long t5=System.nanoTime();
			//System.out.println("The Descending order of documents on basis of TF distance");
			//sortMapValues(map_sort_tf);

			//System.out.println("The Descending order of documents on basis of TF-IDF distance");
			//sortMapValues(map_sort_tf_idf);
				
			//System.out.println(" Total terms : " + count_term);
			long t2=System.currentTimeMillis();
			long t6=System.nanoTime();
			//verify if you are getting the correct term frequency
			
			/*System.out.println("Time Taken ="+(t2-t3)+" milliseconds for the entire program");
			System.out.println("Time Taken ="+(t4-t3)+" milliseconds to precompute D");
			System.out.println("Time Taken ="+(t6-t5)+" nanoseconds for sorting the hashmap");
			System.out.println(" Number of Docs in Index :" + reader.numDocs());*/	
			//if(hashtable_term_idf.containsKey("asu"))
				//System.out.println(hashtable_term_idf.get("asu"));
		}
		catch(IOException e){
		    System.out.println("IO Error has occured: "+ e);
		    return;
		}
	}

	
	//new addition
	class MyComparator implements Comparator
	{
		public int compare(Object o1,Object o2)
		{
			if( ((Double)((Map.Entry)o1).getValue()).doubleValue() < ((Double)((Map.Entry)o2).getValue()).doubleValue() ){
				return(1);
			}else if( ((Double)((Map.Entry)o1).getValue()).doubleValue() > ((Double)((Map.Entry)o2).getValue()).doubleValue() ){
				return(-1);
			}else{
				return(0);
			}
		}
	}
	
	public Hashtable<Integer, Double>[] getTermFreq()
	{
		//System.out.println(ht_term_freq[1123]);
		return ht_term_freq;
	}
	public double[] getIdfVal()
	{
		//if(hashtable_term_idf.containsKey("asu"))
			
		return idfVal;
	}
	
	public String[] getTerms()
	{
		return terms;
	}
	
	public double[] getMod()
	{
		return DocD_tf_idf;
	}
	
	public ArrayList<Integer>[] getPseudoInvertedIndex()
	{
		int final_count=0;
		for(int i=0;i<store.length;i++)
		{
			//System.out.println("Entering for1 "+i);
			int count=0;
			//System.out.print("for doc="+i+": ");
			
			for(int j=0;j<store[i].size();j++)
			{
				//System.out.print(store[i].get(j)+"  ");
				count++;
			}
			//System.out.println();
			if(final_count<count)
				final_count=count;
		}
		//System.out.println("final count= "+final_count);
		return store;
	}
	
	public String stringForApplet()
	{
		//System.out.println(tf_idf_applet);
		return  tf_idf_applet;
	}
	
	public Hashtable<Integer,Double> getVectorSpaceAll()
	{
		return hashforVectorSpace;
	}
	
	public ArrayList<Integer> getDocID()
	{
		return tf_idf;
	}
	
	public void setQuery(String querystr)
	{
		this.line=querystr;
	}
	
	public static void main(String[] args)
	{
		//long t1=System.currentTimeMillis();
		//VectorViewer CSE494Viewer = new VectorViewer();
		//CSE494Viewer.showVector();
		//CSE494Viewer.getPseudoInvertedIndex();
		//CSE494Viewer.getTermFreq();
		//System.out.println(" Total terms : " + CSE494Viewer.count_term);
		//long t2=System.currentTimeMillis();
		//System.out.println("Time taken"+(t2-t1)*1000);
	}
	
	public void setK() throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the value of k for Authorities and Hubs: ");
		
		String line = in.readLine();
		
		int k = Integer.parseInt(line);
		
		this.k=k;
	}
	
}


