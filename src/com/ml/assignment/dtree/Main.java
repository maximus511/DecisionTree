package com.ml.assignment.dtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	static ArrayList<String> words = new ArrayList<String>();
	static int numberOfDocs = 0;
	static int numberOfZeros = 0;
	static int numberOfOnes = 0;
	static double priorZero = 0.0;
	static double priorOne = 0.0;
	static HashMap<String, Double> probabilityZero = new HashMap<String, Double>();
	static HashMap<String, Double> probabilityOne = new HashMap<String, Double>();
	static ArrayList<String> docClass = new ArrayList<String>();
	static ArrayList<Integer> wordsPerDoc = new ArrayList<Integer>();
	static int wordsWithZero = 0;
	static int wordsWithOne= 0;
	static HashMap<String, Double> conditionalProbZero = new HashMap<String, Double>();
	static HashMap<String, Double> conditionalProbOne = new HashMap<String, Double>();
	static HashMap<String, Double> testConditionalProbZero = new HashMap<String, Double>();
	static ArrayList<String> testClasses = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException{
		double startTime = System.currentTimeMillis();
		trainMultinomialNB();
		double endTime = System.currentTimeMillis();
		testMultinomialNB();
		checkAccuracy();
		System.out.println(endTime-startTime);
	}

	public static void trainMultinomialNB() throws IOException, InterruptedException{

		BufferedReader br = new BufferedReader(new FileReader("hw2_data/word_indices.txt"));
		String input = "";
		while((input=br.readLine())!=null){
			words.add(input);
		}
		br.close();
		br = new BufferedReader(new FileReader("hw2_data/train_labels.txt"));
		while((input=br.readLine())!=null){
			numberOfDocs++;
			if(input.equals("0"))
				numberOfZeros++;
			if(input.equals("1"))
				numberOfOnes++;
		}
		priorZero = (double)numberOfZeros/(double)numberOfDocs;
		priorOne = (double)numberOfOnes/(double)numberOfDocs;
		getDocClass();
		getWords();
		br.close();
	}

	public static void getDocClass() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("hw2_data/train_labels.txt"));
		String input = "";
		while((input=br.readLine())!=null){
			docClass.add(input);
		}
		br.close();
	}

	public static void getWords() throws IOException, InterruptedException{
		BufferedReader br = new BufferedReader(new FileReader("hw2_data/train.csv"));
		String input = "";
		int num = 0, i = 0;
		HashMap<Integer,ArrayList<String>> trainingData = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> row;
		while((input=br.readLine())!=null){
			row = new ArrayList<String>();
			String[] line = input.split(",");
			for(String str : line){
				row.add(str);
				num += Integer.parseInt(str);
			}
			trainingData.put(i, row);
			wordsPerDoc.add(num);
			num = 0;
			i++;
		}
		for(i = 0; i<wordsPerDoc.size(); i++){
			if(docClass.get(i).equals("0")){
				wordsWithZero += wordsPerDoc.get(i);
			}
			else{
				wordsWithOne += wordsPerDoc.get(i);
			}
		}
		calculateConditionalProbability(trainingData);
		br.close();
	}

	public static void calculateConditionalProbability(HashMap<Integer,ArrayList<String>> trainingData) throws IOException, InterruptedException{
		int vocabSize = words.size();
		int denominatorZero = wordsWithZero+vocabSize;
		int denominatorOne = wordsWithOne+vocabSize;
		double cp = 0;
		Integer i = 0, j = 0;
		while(i<vocabSize){
			int zeroVar = 0;
			int oneVar = 0;
			while(j<numberOfDocs){
				ArrayList<String> line = trainingData.get(j);
				if(docClass.get(j).equals("0")){
					zeroVar += Integer.parseInt(line.get(i));
				}
				else{
					oneVar += Integer.parseInt(line.get(i));
				}
				j++;
			}
			cp = (double)(zeroVar+1)/(double)(denominatorZero);
			conditionalProbZero.put(i.toString(), cp);
			cp = (double)(oneVar+1)/(double)(denominatorOne);
			conditionalProbOne.put(i.toString(), cp);
			j = 0;
			i++;
		}
	}

	public static void testMultinomialNB() throws IOException, InterruptedException{
		double zeroProb = Math.log(priorZero);
		double oneProb = Math.log(priorOne);
		BufferedReader br = new BufferedReader(new FileReader("hw2_data/test.csv"));
		String input = "";
		while((input=br.readLine())!=null){
			String[] line = input.split(",");
			zeroProb = Math.log(priorZero);
			oneProb = Math.log(priorOne);
			Integer i = 0;
			for(String j : line){
				zeroProb += Integer.parseInt(j)*Math.log(conditionalProbZero.get(i.toString()));
				i++;
			}
			i = 0;
			for(String j : line){
				oneProb += Integer.parseInt(j)*Math.log(conditionalProbOne.get(i.toString()));
				i++;
			}
			if(zeroProb > oneProb)
				testClasses.add("0");
			else
				testClasses.add("1");
		}
		br.close();
	}

	public static void checkAccuracy() throws IOException{
		Integer correct = 0;
		int i = 0;
		BufferedReader br = new BufferedReader(new FileReader("hw2_data/test_labels.txt"));
		String input = "";
		while((input=br.readLine())!=null){
			if(input.equals(testClasses.get(i))){
				correct++;
			}
			i++;
		}
		//System.out.println(correct);
		System.out.println((double)correct/(double)i);
		br.close();
	}
}