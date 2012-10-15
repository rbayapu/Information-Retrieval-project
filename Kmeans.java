package edu.asu.cse494;

import java.util.Random;

public class Kmeans {
	
	/*******Function to perform kmeans operation************/
	public void kmeansAlgorithm(String query)
	{
		VectorVwr vector = new VectorVwr();
		vector.showVector(query);
		System.out.println(vector.tfidfDocs());
		//find 3 numbers randomly here
		
		//Loop for calculating kmeans
		
		
	}

	public static void main(String[] args)
	{
		Kmeans kmean = new Kmeans();
		kmean.kmeansAlgorithm("asu");
		
	}
}
