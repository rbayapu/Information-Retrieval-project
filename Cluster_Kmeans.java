package edu.asu.cse494;

import java.io.IOException;
import java.util.ArrayList;
import com.lucene.analysis.Analyzer;

import com.lucene.analysis.StopAnalyzer;
import com.lucene.analysis.TokenStream;
import com.lucene.document.Document;
import com.lucene.index.*;

import java.io.*;

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
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;

import com.lucene.index.IndexReader;
import com.lucene.search.IndexSearcher;
import com.lucene.search.Searcher;

public class Cluster_Kmeans {

	int numofK=3;
	int k=3;
	int ini_centers[]= new int[k]; 
	VectorViewer vv = new VectorViewer();
	Hashtable<Integer, Double> ht_term_freq[]=vv.getTermFreq();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Cluster_Kmeans csk= new Cluster_Kmeans();
		csk.cluster();
	
	}
	
	public void cluster() throws IOException
	{
		long t1=System.currentTimeMillis();
		
		vv.setK();
		vv.showVector();
		System.out.println("Number of terms is:"+vv.count_term);
		ArrayList<Integer> topN = new ArrayList<Integer>();
		topN=vv.getDocID();
		//double []idfVal=vv.getIdfVal();
		//int [][] store= new int[vv.count_term][1000];
		
		//IndexReader reader = IndexReader.open("result3index");
		Collections.sort(topN);
		
		//To get the initial centroids in a random way
		Random random = new Random();
		Boolean flag=true;
		for(int i=0;i<k;i++)
		{
			int pick = random.nextInt(topN.size());
			for(int j=0;j<=i;j++)
			{
				int docid=topN.get(pick);
				if(ini_centers[j]==docid)
				{
					flag=false;
					break;
				}
			}
			if(flag==true)
				ini_centers[i]=topN.get(pick);
			else
			{
				i--;
				flag=true;
			}
		}
		
		ArrayList<Integer> []PseudoIndex=vv.getPseudoInvertedIndex();
		//String terms[]=vv.getTerms();
		double[] modVal= vv.getMod();
		
		//HashTable to store the clusters
		
		Hashtable<Integer,Double> ht_center0= new Hashtable<Integer,Double>();
		Hashtable<Integer,Double> ht_center1= new Hashtable<Integer,Double>();
		Hashtable<Integer,Double> ht_center2= new Hashtable<Integer,Double>();
		
		//Arraylists to add and check for the clusters
		ArrayList<Integer> cent0_prev= new ArrayList<Integer>();
		ArrayList<Integer> cent1_prev= new ArrayList<Integer>();
		ArrayList<Integer> cent2_prev= new ArrayList<Integer>();
		ArrayList<Integer>[] cent_prev = new ArrayList[k];
		
		
		ArrayList<Integer> cent0_next= new ArrayList<Integer>();
		ArrayList<Integer> cent1_next= new ArrayList<Integer>();
		ArrayList<Integer> cent2_next= new ArrayList<Integer>();
		ArrayList<Integer>[] cent_next = new ArrayList[k];
		
		for(int i=0;i<topN.size();i++)
		{
			int doc= topN.get(i);
			double TermFreqProd=0;
			double finalTermFreq=0;
			//document which goes to which center depending on its similarity with the Initial Centers
			int finalDocCenter=0;
			for(int j=0;j<k;j++)
			{
				
				int center=ini_centers[j];
				//Term numbers in the Centroid Document
				ArrayList<Integer> CenterTermIndex= PseudoIndex[center];
				//Terms numbers in  the non-Centroid Documents
				ArrayList<Integer> docTermIndex=PseudoIndex[doc];
				
				//iterating through all the term numbers in the Centroid Document
				for(int a=0;a<CenterTermIndex.size();a++)
				{
					//Checking if the Centroid Document Term number exists in Non Centroid Documents
					if(BinarySearch(docTermIndex, CenterTermIndex.get(a)))
					{
						//Calculating the Term Freq--
						TermFreqProd+= ProductTermFreq(CenterTermIndex.get(a), center, doc);
					}
				}
				//Diving the Term Frequencies with their respective Modulus Values to get the Cosine Similarity
				TermFreqProd=TermFreqProd/(modVal[center]*modVal[doc]);
				//System.out.println("ModVal Center= "+modVal[center]+"  ModVal Doc= "+modVal[doc]);
				//Store the Bigger Similarity Value.
				if(finalTermFreq<TermFreqProd)
				{
					finalTermFreq=TermFreqProd;
					finalDocCenter=center;
				}
			}
			//System.out.println("Similarity Val= "+finalTermFreq+"  center= "+finalDocCenter);
			//Storing the Document with the Highest Similarity
			if(finalDocCenter==ini_centers[0])
			{
				cent0_prev.add((Integer)doc);
			}
			
			if(finalDocCenter==ini_centers[1])
			{
				cent1_prev.add((Integer)doc);
			}
			
			if(finalDocCenter==ini_centers[2])
			{
				cent2_prev.add((Integer)doc);
			}
		}
		
		System.out.println("Center 0  is:"+ini_centers[0]);
		System.out.print("The cluster is ");
		
		for(int i=0;i<cent0_prev.size();i++)
		{
			int doc=cent0_prev.get(i);
			System.out.print(doc+"  ");
		}
		
		System.out.println();
		System.out.println("Center 1  is:"+ini_centers[1]);
		System.out.print("The cluster is ");
		
		
		for(int j=0;j<cent1_prev.size();j++)
		{
			int doc=cent1_prev.get(j);
			System.out.print(doc+"  ");
		}
		
		System.out.println();
		System.out.println("Center 2  is:"+ini_centers[2]);
		System.out.print("The cluster is ");
		
		for(int k=0;k<cent2_prev.size();k++)
		{
			int doc=cent2_prev.get(k);
			System.out.print(doc+"  ");
		}
		System.out.println();
		System.out.println("Cluster 0:"+cent0_prev.size()+"   "+"Cluster 1:"+cent1_prev.size()+"   "+"Cluster 2:"+cent2_prev.size()+"   ");
		
		
		long t2=System.currentTimeMillis();
		System.out.println();
		System.out.println("Time Taken="+(t2-t1)/1000+" seconds");
		int count=0;
		while(true)
		{
			count++;
			for(int f=0;f<topN.size();f++)
			{	
				System.out.print(f+" ");
				double TermFreqProd0=0;
				double TermFreqProd1=0;
				double TermFreqProd2=0;
				double finalTermFreProd=0;
				int finalDocCenter=0;
				double ModVal0=0;
				double ModVal1=0;
				double ModVal2=0;
				
				int top_docs=topN.get(f);
				ArrayList<Integer> top_docIndex=PseudoIndex[top_docs];
				
				for(int a=0;a<cent0_prev.size();a++)
				{
					int doc0=cent0_prev.get(a);
					
					ArrayList<Integer> docIndex0= PseudoIndex[doc0];
					ArrayList<Integer> array0= new ArrayList<Integer>();
					for(int b=0;b<docIndex0.size();b++)
					{
						if(BinarySearch(top_docIndex, docIndex0.get(b)))
						{	
							TermFreqProd0+=ProductTermFreq(docIndex0.get(b), top_docs,doc0);
							double term_freq1=ht_term_freq[docIndex0.get(b)].get(doc0);
							double term_freq2=ht_term_freq[docIndex0.get(b)].get(top_docs);
							ModVal0+=Math.pow(term_freq2+term_freq1, 2);
							array0.add(docIndex0.get(b));
						}
						else
						{
							double term_freq=ht_term_freq[docIndex0.get(b)].get(doc0);
							ModVal0+=Math.pow(term_freq, 2);
						}
					}
					
					for(int c=0;c<top_docIndex.size();c++)
					{
						if(!(BinarySearch(docIndex0, top_docIndex.get(c))))
						{
							double term_freq=ht_term_freq[top_docIndex.get(c)].get(top_docs);
							ModVal0+=Math.pow(term_freq, 2);
						}
					}
					array0.clear();
					
				}
				//Modulus
				ModVal0=(double)Math.sqrt(ModVal0/cent0_prev.size());
				TermFreqProd0=(double)TermFreqProd0/(ModVal0*modVal[top_docs]);
				TermFreqProd0=(double)TermFreqProd0/cent0_prev.size();
				
				if(finalTermFreProd<TermFreqProd0)
				{
					finalTermFreProd=TermFreqProd0;
					finalDocCenter=0;
				}
				
				for(int a=0;a<cent1_prev.size();a++)
				{
					int doc1=cent1_prev.get(a);
					ArrayList<Integer> docIndex1= PseudoIndex[doc1];
					ArrayList<Integer> array1= new ArrayList<Integer>();
					for(int b=0;b<docIndex1.size();b++)
					{
						if(BinarySearch(top_docIndex, docIndex1.get(b)))
						{
							TermFreqProd1+=ProductTermFreq(docIndex1.get(b), top_docs,doc1);
							double term_freq1=ht_term_freq[docIndex1.get(b)].get(doc1);
							double term_freq2=ht_term_freq[docIndex1.get(b)].get(top_docs);
							ModVal1+=Math.pow(term_freq2+term_freq1, 2);
							array1.add(docIndex1.get(b));
						}
						else
						{
							double term_freq=ht_term_freq[docIndex1.get(b)].get(doc1);
							ModVal1+=Math.pow(term_freq, 2);
						}
					}
					
					for(int c=0;c<top_docIndex.size();c++)
					{
						if(!(BinarySearch(docIndex1, top_docIndex.get(c))))
						{
							double term_freq=ht_term_freq[top_docIndex.get(c)].get(top_docs);
							ModVal1+=Math.pow(term_freq, 2);
						}
					}
					array1.clear();
					
				}
				//Modulus
				ModVal1=(double)Math.sqrt(ModVal1/cent1_prev.size());
				TermFreqProd1=(double)TermFreqProd1/(ModVal1*modVal[top_docs]);
				TermFreqProd1=(double)TermFreqProd1/cent1_prev.size();
				
				if(finalTermFreProd<TermFreqProd1)
				{
					finalTermFreProd=TermFreqProd1;
					finalDocCenter=1;
				}
				
				for(int a=0;a<cent2_prev.size();a++)
				{
					int doc2=cent2_prev.get(a);
					ArrayList<Integer> docIndex2=PseudoIndex[doc2];
					ArrayList<Integer> array2= new ArrayList<Integer>();
					for(int b=0;b<docIndex2.size();b++)
					{
						if(BinarySearch(top_docIndex, docIndex2.get(b)))
						{
							TermFreqProd2+=ProductTermFreq(docIndex2.get(b), top_docs,doc2);
							double term_freq1=ht_term_freq[docIndex2.get(b)].get(doc2);
							double term_freq2=ht_term_freq[docIndex2.get(b)].get(top_docs);
							ModVal2+=Math.pow(term_freq2+term_freq1, 2);
							array2.add(docIndex2.get(b));
						}
						else
						{
							double term_freq=ht_term_freq[docIndex2.get(b)].get(doc2);
							ModVal2+=Math.pow(term_freq, 2);
						}
					}
					
					for(int c=0;c<top_docIndex.size();c++)
					{
						if(!(BinarySearch(docIndex2, top_docIndex.get(c))))
						{
							double term_freq=ht_term_freq[top_docIndex.get(c)].get(top_docs);
							ModVal2+=Math.pow(term_freq, 2);
						}
					}
					array2.clear();
				}
				//Modulus
				ModVal2=(double)Math.sqrt(ModVal2/cent2_prev.size());
				TermFreqProd2=(double)TermFreqProd2/(ModVal2*modVal[top_docs]);
				TermFreqProd2=(double)TermFreqProd2/cent2_prev.size();
				if(finalTermFreProd<TermFreqProd2)
				{
					finalTermFreProd=TermFreqProd2;
					finalDocCenter=2;
				}
				if((TermFreqProd0==finalTermFreProd) && (TermFreqProd1==finalTermFreProd) && (TermFreqProd2==finalTermFreProd))
				{
					finalDocCenter = random.nextInt(k);
					System.out.println("FinalTermFreProd= "+finalTermFreProd+"  TermFreProd0= "+TermFreqProd0+"  TermFreProd1= "+TermFreqProd1+"  TermFreProd2= "+TermFreqProd2+"  FinalDocCenter= "+finalDocCenter);
					System.out.println("Doc= "+top_docs);
				}
				else
				{	
					if(finalDocCenter==0)
					{
						ht_center0.put((Integer)top_docs, finalTermFreProd);
						cent0_next.add((Integer)top_docs);
						//System.out.println("topdocs for 0= "+top_docs);
					}
				
					if(finalDocCenter==1)
					{
						ht_center1.put((Integer)top_docs, finalTermFreProd);
						cent1_next.add((Integer)top_docs);
						//System.out.println("topdocs for 1= "+top_docs);
					}
				
					if(finalDocCenter==2)
					{
						ht_center2.put((Integer)top_docs, finalTermFreProd);
						cent2_next.add((Integer)top_docs);
						//System.out.println("topdocs for 2= "+top_docs);
					}
				//System.out.println("TermFreProd0= "+TermFreqProd0+"  TermFreProd1= "+TermFreqProd1+"  TermFreProd2= "+TermFreqProd2+"  FinalDocCenter= "+finalDocCenter);
				//System.out.println("Doc= "+top_docs);
				}
				TermFreqProd0=0;
				TermFreqProd1=0;
				TermFreqProd2=0;
				ModVal0=0;
				ModVal1=0;
				ModVal2=0;
			}
			
			/*************************************************************************************************************/
			System.out.println();
			System.out.print("The cluster 0 is ");
			for(int i=0;i<cent0_next.size();i++)
			{
				int doc=cent0_next.get(i);
				System.out.print(doc+"  ");
			}
			/*System.out.println("The cluster 0 is ");
			for(Enumeration<Double> e=ht_center0.elements();e.hasMoreElements();)
			{
				double doc=e.nextElement();
				System.out.print(doc+"  ");
			}*/
			System.out.println();
			//System.out.println("Center 1  is:"+ini_centers[1]);
			System.out.print("The cluster 1 is ");
			
			for(int j=0;j<cent1_next.size();j++)
			{
				int doc=cent1_next.get(j);
				System.out.print(doc+"  ");
			}
			
			System.out.println();
			//System.out.println("Center 2  is:"+ini_centers[2]);
			System.out.print("The cluster 2 is ");
			for(int k=0;k<cent2_next.size();k++)
			{
				int doc=cent2_next.get(k);
				System.out.print(doc+"  ");
			}
			System.out.println();
			System.out.print("Sizes are: Cluster 0= "+cent0_next.size()+"  Cluster 1= "+cent1_next.size()+"  Cluster 2= "+cent2_next.size());
			System.out.println("Near Break");
			/**********************************************************************************************************/
			if(CheckEquality(cent0_next, cent0_prev))
			{
				if(CheckEquality(cent1_next, cent1_prev))
				{
					if(CheckEquality(cent2_next, cent2_prev))
						break;
				}
			}	
			if(count==5)
				break;
		
			
			cent0_prev.clear();
			cent1_prev.clear();
			cent2_prev.clear();
			if(cent0_prev.isEmpty() && cent1_prev.isEmpty() && cent2_prev.isEmpty())
			{
				System.out.println("Centroid previous are Empty");
			}
			for(int i=0;i<cent0_next.size();i++)
			{
				cent0_prev.add(cent0_next.get(i));
			}
			
			for(int j=0;j<cent1_next.size();j++)
			{
				cent1_prev.add(cent1_next.get(j));
			}
			
			for(int k=0;k<cent2_next.size();k++)
			{
				cent2_prev.add(cent2_next.get(k));
			}
			
			cent0_next.clear();
			cent1_next.clear();
			cent2_next.clear();
			
			if(cent0_next.isEmpty() && cent1_next.isEmpty() && cent2_next.isEmpty())
			{
				System.out.println("Centroid next are Empty");
			}
			
			ht_center0.clear();
			ht_center1.clear();
			ht_center2.clear();
			
			if(ht_center0.isEmpty() && ht_center1.isEmpty() && ht_center2.isEmpty())
			{
				System.out.println("hashtables are Empty");
			}
		}
			
		
	}
	
	public boolean CheckEquality(ArrayList<Integer> a1, ArrayList<Integer> a2)
	{
		Collections.sort(a1);
		Collections.sort(a2);
		if(a1.size()==a2.size())
		{
			for(int i=0;i<a1.size();i++)
			{
				if(a1.get(i)!=a2.get(i))
					return false;
			}
			return true;
		}
		else	
		{
			return false;
		}
		
	}
	
	public double ProductTermFreq(int term, int doc1,int doc2) throws IOException
	{
		double term_freq_doc1=ht_term_freq[term].get(doc1);
		double term_freq_doc2=ht_term_freq[term].get(doc2);
		//System.out.println("term freq doc1= "+term_freq_doc1+"  Term freq doc2= "+term_freq_doc2);
		double product=1;
		product=term_freq_doc1*term_freq_doc2;
		return product;
	}
	
	public boolean BinarySearch(ArrayList<Integer> array, int docid)
	{
		int start=0,index;
		int end=array.size()-1;
		//index=(start+end)/2;

		while(start<=end)
		{
			//System.out.println("Searching in binary");
			index=(start+end)/2;
			if(array.get(index)==docid)
				return true;
			if(array.get(index)<docid)
			{
				start=index+1;
			}
			else
			{
				end=index-1;
			}
			
		}
		return false;
	}
	
}
