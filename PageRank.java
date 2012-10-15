package edu.asu.cse494;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

public class PageRank {
	
	public static void main(String[] args)
	{
		PageRank p = new PageRank();
		p.page();
		//p.example();
	}
	
	public void page()
	{
		LinkAnalysis link =new LinkAnalysis();
		Hashtable<Integer,LinkedList> hash = new Hashtable<Integer,LinkedList>();
		Hashtable<Integer,Double> pagerank = new Hashtable<Integer, Double>();
		//Hashtable<Integer,Double> page = new Hashtable<Integer,Double>();
		 int numDocs = 25053;
		 float dampingfactor=(float) 0.4;
		// double mno= 1.0/25053 ;
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
			
			//page.put(i, mno);
						//System.out.println(hash.get(i));
			
			//System.out.println(pagerank[i]);
			
		}
		//System.out.println(link.getCitations(25052).length);
		
		/**************Calculating page rank of all the nodes | removing sink nodes*****************************/
		 for(int mn=0;mn<100;mn++)
			{
				for(int i=0;i<numDocs;i++)
				{
					if(link.getLinks(i).length!=0)
					{
						int[] forwardlinks = link.getLinks(i);
						int linkslength = forwardlinks.length;
						for(int k=0;k<linkslength;k++)
						{
							double temp = pagerank.get(i)/linkslength;
							temp+=pagerank.get(k);
							pagerank.put(k, temp);
						}
						
					}
					else
					{
						double a = pagerank.get(i)+(pagerank.get(i)/numDocs);
						pagerank.put(i, a);
						
					}
					//System.out.println(pagerank.get(i));
				}
			}
		//System.out.println(pagerank);
		
		
		
		
		
		/*
		for(int i=0;i<numDocs;i++)
		{
			int[] forwardlinks = link.getLinks(i);
			int linkLength=forwardlinks.length;
			if(linkLength!=0)
			{
				//if(i==9410)
				//	System.out.println("linkLength="+linkLength);		
				for(int k=0;k<linkLength;k++)
				{
					int nodes = forwardlinks[k];
					if(i==9410)
					{
						System.out.println("cal="+pagerank[nodes]/forwardlinks.length+" : "+ pagerank[nodes]+" : "+forwardlinks.length);		
						double an =(pagerank[nodes])+ (pagerank[nodes]/forwardlinks.length);
						//System.out.println(an);
					}
					pagerank[nodes]+=(double)pagerank[nodes]/forwardlinks.length;
					//System.out.println(no.length);
									
				}
				
			}
			else
			{
				pagerank[i]+=(double)pagerank[i]/numDocs;
				if(i==9090)
					
					System.out.println("else PageRank="+pagerank[9047]);		
					
			}
			//System.out.println(pagerank[i]);
			//System.out.println(pagerank[9047]);
		}
		//System.out.println(link.getLinks(9410).length);
		 
		 
		*/
		
		/********adding damping factor for the pageranks*******************/
		double temp =1.0;
		double dam = (double)(1.0-dampingfactor)/numDocs;
		int z=0;
		//System.out.println(dam);
		for(int i=0;i<numDocs;i++)
		{
			double tem = dampingfactor*pagerank.get(i)+dam;
			pagerank.put(i, tem);
			//pagerank[i] = dampingfactor*pagerank[i] + dam;
			//System.out.println(pagerank[9047]);
			double m = pagerank.get(i);
			if(m>temp)
			{
				temp = pagerank.get(i);
				System.out.println("for i="+i+"  val="+temp);
				z=i;
				
			}
			//System.out.println(pagerank.get(i));
		}
		System.out.println(temp);
		/************Normalize pagerank values*******************/
	/*	for(int i=0;i<numDocs;i++)
		{
			pagerank[i]=pagerank[i]/temp;
			//System.out.println(pagerank[i]);
		}
		*/
	}
	
	public void example(){
		Hashtable<Integer,Double> pagerank = new Hashtable<Integer, Double>();
		pagerank.put(1, 15.5);
		pagerank.put(1, 10.2);
		
		System.out.println(pagerank);
		
	}

	
}
