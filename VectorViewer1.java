package edu.asu.cse494;
import com.lucene.index.*;
import java.io.*;


public class VectorViewer1 {
	int count=0;
	//display the vector
	public void  showVector()
	{
		// lists the vector
		try{
			IndexReader reader = IndexReader.open("result3index");
			System.out.println(" Number of Docs in Index :" + reader.numDocs());
			
			// use the TermEnum object to iterate through all the terms in the index
			TermEnum termenum = reader.terms();
			System.out.println("Printing the Terms and the Frequency \n");
			while(termenum.next())
			{
				count++;
				Term termval = termenum.term();
				//System.out.println("The Term :" + termval.text() + " Frequency :"+termenum.docFreq()+ " Term value:"+termval);
				
				
				   //Add following here to retrieve the <docNo,Freq> pair for each term
				   TermDocs termdocs = reader.termDocs(termval);
				   termdocs.next();
				   System.out.println("document number:"+termdocs.doc()+" | "+"frequency:"+termdocs.freq());
			/*
				   //to retrieve the <docNo,Freq,<pos1,......posn>> call
				   TermPositions termpositions = termval.termPositions(termval)
				*/
			
			}
			System.out.println(" Total terms : " + count);
		
		}
		catch(IOException e){
		    System.out.println("IO Error has occured: "+ e);
		    return;
		}
	}


	public static void main(String[] args)
	{
		VectorViewer1 CSE494Viewer = new VectorViewer1();
		CSE494Viewer.showVector();
		System.out.println(" Total terms : " + CSE494Viewer.count);
	}
}
