package edu.asu.cse494;
//import ValueComparator;

//import MyComparator;

import com.lucene.index.*;
import java.util.*;
import java.io.*;


public class VectorVwr {
	int count=0;
	ArrayList<Integer> docs_tf_idf=  new ArrayList<Integer>();
	ArrayList<Double> tfidfsim=  new ArrayList<Double>();
	
	//display the vector
	public void  showVector(String querys)
	{


		// lists the vector
		try{
			String query_search=querys.toLowerCase();
			Long t1= System.nanoTime();
			long TF1= System.nanoTime();
			long IDF1= System.nanoTime();
			IndexReader reader = IndexReader.open("result3index");

			//System.out.println(" Number of Docs in Index :" + reader.numDocs());
			HashMap hash_IDF= new HashMap();
			TermEnum termenum = reader.terms();
			double array_D[]=new double [reader.numDocs()];
			double array_idf[]=new double [reader.numDocs()];
			double idf=reader.numDocs();
			//System.out.println("Printing the Terms and the Frequency \n");

			while(termenum.next() && termenum.term().field().equals("contents"))
			{

				count++;
				Term termval = termenum.term();
				hash_IDF.put(termval.text().toString(),Math.log(idf/termenum.docFreq()));

				//Add following here to retrieve the <docNo,Freq> pair for each term
				TermDocs termdocs = reader.termDocs(termval);


				while(termdocs.next())
				{
					array_D[termdocs.doc()]= array_D[termdocs.doc()]+termdocs.freq()*termdocs.freq();

					//ArrayList();
					double idf_val= ((Double) hash_IDF.get(termval.text().toString()));
					array_idf[termdocs.doc()]=array_idf[termdocs.doc()]+ (termdocs.freq()*termdocs.freq()*(idf_val*idf_val));

				}	
			}

			for(int i=0;i<reader.numDocs();i++)
			{
				array_D[i]=(double) Math.sqrt(array_D[i]);
				array_idf[i]=(double) Math.sqrt(array_idf[i]);

			}	
			double similarity[]=new double[reader.numDocs()];
			double similarity_idf[]=new double[reader.numDocs()];

			StringTokenizer tokens= new StringTokenizer(query_search);

			while(tokens.hasMoreTokens())
			{
				String search=tokens.nextToken();
				search=search.toLowerCase();
				Term newterm= new Term("contents",search);
				TermDocs search_termDocs=reader.termDocs(newterm);


				while(search_termDocs.next())
				{
					similarity[search_termDocs.doc()]=similarity[search_termDocs.doc()]+search_termDocs.freq();

					double idf_val=((Double)hash_IDF.get(search)).doubleValue();
					similarity_idf[search_termDocs.doc()]=similarity_idf[search_termDocs.doc()]+(search_termDocs.freq()*idf_val);
				}

			}
			Hashtable ht = new Hashtable();
			Hashtable ht_idf = new Hashtable();
			Object[] myArray;
			Object[] myArray_idf;
			MyComparator myComparator = new MyComparator();
			for(int i=0;i<reader.numDocs();i++)
			{
				if(similarity[i]!=0)
				{
					similarity[i]=similarity[i]/array_D[i];
					similarity_idf[i]=similarity_idf[i]/array_idf[i];

					ht.put(i, similarity[i]);
					ht_idf.put(i, similarity_idf[i]);
				}
				else
				{
					similarity[i]=-7654;
					similarity_idf[i]=-7654;
				}
			}
			myArray = ht.entrySet().toArray();
			Long m1= System.nanoTime();
			Arrays.sort(myArray,(Comparator)myComparator);
			Long m2= System.nanoTime();
			int count=0;
			for(int i=0;i<myArray.length;++i) {
				count++;
				if(count>=10)
					break;
			}
			long TF2= System.nanoTime();

		
			myArray_idf=ht_idf.entrySet().toArray();
			Arrays.sort(myArray_idf,(Comparator)myComparator);

				int count1=0;
			for(int i=0;i<myArray.length;++i) {
				int Documentid=(Integer)((Map.Entry)myArray_idf[i]).getKey();
				double key=(Double)((Map.Entry)myArray_idf[i]).getValue();
				docs_tf_idf.add(Documentid);
				tfidfsim.add(key);
				count1++;
				if(count1>=10)
					break;
				
			}
			
			long IDF2= System.nanoTime();
			Long t2= System.nanoTime();
				}
		catch(IOException e){
			System.out.println("IO Error has occured: "+ e);
			return;

		}

	}
	
	public ArrayList<Integer> tfidfDocs()
	{
		return docs_tf_idf;
	}
	
	public ArrayList<Double> tfidfSimilarity()
	{
		return tfidfsim;
	}
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
	public static void main(String[] args)
	{
		VectorVwr CSE494Viewer = new VectorVwr();
		CSE494Viewer.showVector("asu");
		//System.out.println(" Total terms in corpus : " + CSE494Viewer.count);	
		System.out.println(CSE494Viewer.tfidfDocs());
	}


}




