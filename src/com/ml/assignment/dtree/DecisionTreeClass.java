package com.ml.assignment.dtree;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.opencsv.CSVReader;

/**
 * Class to create and test decision tree.
 * @author Rahul
 *
 */
class DecisionTreeClass {

	public List<String[]> trainingDataList;
	public List<String[]> testDataList;
	public List<String[]> validationDataList;
	private static List<String> attributeList = new ArrayList<String>();
	private static Node rootNode = null;
	public int[] decisionTree;
	public int level=0;
	int classOneCount = 0;
	int classZeroCount = 0;
	double entropy = 0;
	double onesEntropy = 0;
	double zerosEntropy = 0;
	Random random = new Random();

	public DecisionTreeClass()
	{
		trainingDataList = new ArrayList<String[]>();;
		testDataList = new ArrayList<String[]>();
		validationDataList = new ArrayList<String[]>();
		decisionTree = new int[10000000];
	}

	/**
	 * Function to read input datasets
	 * @param inputSet
	 * @param csvFile
	 */
	public void readInputFile(List<String[]> inputSet, String csvFile)
	{
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String [] tokens = reader.readNext();
			int i = 0;
			inputSet.add(tokens);
			while ( i < tokens.length )
			{
				attributeList.add(i,tokens[i]);
				i++;
			}
			while ((tokens = reader.readNext()) != null) {
				inputSet.add(tokens);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(reader != null)
				{
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function to traverse the tree comparing class values
	 * @param node
	 * @param dataSet
	 * @return
	 */
	public String traverseDTree(Node node, String[] dataSet)
	{
		String output = "";
		if(node.getData().equals("0") || node.getData().equals("1")) 
		{
			return node.getData();
		}
		int col = -10;
		for(int currColumn = 0; currColumn<dataSet.length; currColumn++)
		{
			if(trainingDataList.get(0)[currColumn].equals(node.getData())) 
			{
				col = currColumn;
				break;
			}
		}
		if(dataSet[col].equals("0"))
		{
			output = traverseDTree(node.getLeftChild(), dataSet);
		} 
		else 
		{
			output = traverseDTree(node.getRightChild(), dataSet);
		}
		return output;
	}

	/**
	 * Function to test tree of Test Dataset
	 * @param tree
	 * @param data
	 * @return
	 */
	public double runDTreeOnTestData(Node tree, List<String[]> data)
	{
		int positiveOutput = 0;
		int negativeOutput = 0;
		int length = data.get(0).length;
		for(int index = 1; index < data.size(); index++) {
			String[] currentRow = data.get(index);
			String output = traverseDTree(tree, currentRow);
			if(output.equals(currentRow[length-1])) 
			{
				positiveOutput++;
			}
			else 
			{
				negativeOutput++;
			}
		}
		return ((double)positiveOutput/(positiveOutput+negativeOutput))*100;
	}

	/**
	 * Function to print tree
	 * @param root
	 */

	public void printTree(Node root) 
	{

		String blank = "";
		for(int i = 0; i < root.getLevel(); i++) 
		{
			blank = blank + "|";
		}
		if(root.getLeftChild().getData().equals("0") || root.getLeftChild().getData().equals("1")) 
		{
			System.out.println(blank + root.getData()+" = 0 : "+root.getLeftChild().getData());
		} 
		else 
		{
			System.out.println(blank + root.getData()+" = 0 :");
			printTree(root.getLeftChild());
		}
		if(root.getRightChild().getData().equals("0") || root.getRightChild().getData().equals("1"))
		{
			System.out.println(blank + root.getData()+" = 1 : "+root.getRightChild().getData());
		}
		else
		{
			System.out.println(blank + root.getData()+" = 1 :");
			printTree(root.getRightChild());
		}

	}

	/**
	 * Function to calculate entropy for the root of the subtree
	 * @param dataSet
	 * @param rootNode
	 * @param currentAttributes
	 * @param index
	 */
	public void calculateRootEntropy(List<String[]> dataSet, Node rootNode,List<String> currentAttributes, int index)
	{

		entropy = 0;
		classZeroCount=0;
		classOneCount=0;
		for(int a = 1; a < dataSet.size(); a++) {
			if(dataSet.get(a)[dataSet.get(a).length-1].equals("0"))
			{
				classZeroCount++;
			}
			else
			{
				classOneCount++;
			}
		}
		if(classZeroCount != 0 && classOneCount != 0) {
			entropy = -((double)classOneCount/(double)(classOneCount+classZeroCount)) * (Math.log10((double)classOneCount/(double)(classOneCount+classZeroCount)) / Math.log10(2)) 
					-((double)classZeroCount/(double)(classOneCount+classZeroCount)) * (Math.log10((double)classZeroCount/(double)(classOneCount+classZeroCount)) / Math.log10(2));
		}

		rootNode.setEntropy(entropy);
		rootNode.setData("root");
		rootNode.setOnesCount(classOneCount);
		rootNode.setZerosCount(classZeroCount);

	}

	/**
	 * Function to calculate gain ratio for each attribute
	 * @param dataSet
	 * @param currentColumn
	 * @return
	 */
	public double calculateGainRatio(List<String[]> dataSet, int currentColumn)
	{
		int onesCount = 0;
		int zeroesCount = 0;
		int zeroCountForAttribute = 0;
		int oneCountForAttribute = 0;
		onesEntropy = 0; 
		zerosEntropy = 0;
		for(int i = 1; i <dataSet.size(); i++) {
			String value = dataSet.get(i)[dataSet.get(0).length-1];
			if(dataSet.get(i)[currentColumn].equals("0"))
			{
				zeroCountForAttribute++;
				if(value.equals("1")) 
				{
					onesCount++;
				} else if(value.equals("0")) 
				{
					zeroesCount++;
				}
			}
		}
		double onesProbability = (double)onesCount / (double)(zeroCountForAttribute);
		double zerosProbability = (double)zeroesCount / (double)(zeroCountForAttribute);
		if(onesProbability > 0 && zerosProbability > 0) {
			zerosEntropy = -(onesProbability * (Math.log10(onesProbability) / Math.log10(2))) -(zerosProbability * (Math.log10(zerosProbability) / Math.log10(2)));
		}
		onesCount = 0; 
		zeroesCount = 0; 
		oneCountForAttribute = 0;
		for(int i = 1; i <dataSet.size(); i++) {
			String value = dataSet.get(i)[dataSet.get(0).length-1];
			if(dataSet.get(i)[currentColumn].equals("1")) {
				oneCountForAttribute++;
				if(value.equals("1")) {
					onesCount++;
				} else if(value.equals("0")) {
					zeroesCount++;
				}
			}
		}
		onesProbability = (double)onesCount / (double)(oneCountForAttribute);
		zerosProbability = (double)zeroesCount / (double)(oneCountForAttribute);
		if(onesProbability > 0 && zerosProbability > 0) {
			onesEntropy = -((onesProbability * (Math.log10(onesProbability) / Math.log10(2))) +(zerosProbability * (Math.log10(zerosProbability) / Math.log10(2))));
		}


		double totalCountForAttribute = (double)(zeroCountForAttribute+oneCountForAttribute);
		double splitCountForZero = ((double)zeroCountForAttribute/totalCountForAttribute);
		double splitCountForOne = (double)oneCountForAttribute/totalCountForAttribute;
		double gain = entropy -( splitCountForZero*zerosEntropy + splitCountForOne*onesEntropy);

		double splitInfo = -((splitCountForZero * (Math.log10(splitCountForZero) / Math.log10(2))) +(splitCountForOne * (Math.log10(splitCountForOne) / Math.log10(2))));

		double gainRatio = gain/splitInfo;
		return gainRatio;
	}

	/**
	 * Function to calculate entropy
	 * @param dataSet
	 * @param rootNode
	 * @param currentAttributes
	 * @param level
	 * @param index
	 */
	public void calculateEntropy(List<String[]> dataSet, Node rootNode,List<String> currentAttributes, int level, int index)
	{

		calculateRootEntropy(dataSet,rootNode,currentAttributes, index);
		if(entropy == 0) {
			if(classZeroCount==0)
			{
				rootNode.setData("1");
			}
			else {
				rootNode.setData("0");
			}
			return;
		}

		if(currentAttributes.size() == 1) {
			if(classZeroCount<classOneCount)
			{
				rootNode.setData("1");
			}
			else {
				rootNode.setData("0");
			}
			return;
		}
		List<String[]> leftBranch = new ArrayList<String[]>();
		List<String[]> rightBranch = new ArrayList<String[]>();

		double maximumGainRatio = -10;
		int columnChosen = -1;
		for(int currColumn = 0; currColumn < (dataSet.get(0).length - 1); currColumn++) 
		{
			if(currentAttributes.contains(dataSet.get(0)[currColumn])) 
			{
				double gainRatio = calculateGainRatio(dataSet, currColumn);

				if(gainRatio > maximumGainRatio) {
					maximumGainRatio = gainRatio;
					columnChosen = currColumn;
				}
			}	

		}
		leftBranch.add(dataSet.get(0));
		rightBranch.add(dataSet.get(0));

		for(int i=1;i<dataSet.size()-1;i++){

			if(dataSet.get(i)[columnChosen].equals("0")){
				leftBranch.add(dataSet.get(i));
			}
			if(dataSet.get(i)[columnChosen].equals("1")){
				rightBranch.add(dataSet.get(i));
			}
		}

		rootNode.setData(dataSet.get(0)[columnChosen]);

		int treeIndexForLeft  = 2 * index;
		int treeIndexForRight = (2 * index) + 1;
		decisionTree[index]=columnChosen;
		level++;
		createBranch(rootNode, currentAttributes, level, leftBranch,
				treeIndexForLeft, true);

		createBranch(rootNode, currentAttributes, level, rightBranch,
				treeIndexForRight, false);

		level--;			
	}


	/**
	 * Create Child branches for the nodes.
	 * isLeft parameter will decided whether it is left child or right child
	 * @param rootNode
	 * @param currentAttributes
	 * @param level
	 * @param branch
	 * @param treeIndex
	 */
	private void createBranch(Node rootNode,
			List<String> currentAttributes, int level,
			List<String[]> branch, int treeIndex, Boolean isLeft) {
		List<String> attributes= new ArrayList<String>(currentAttributes); 


		Node node =  new Node();
		if(isLeft)
		{
			rootNode.setLeftChild(node);
			node.setEntropy(zerosEntropy);
		} else 
		{
			rootNode.setRightChild(node);
			node.setEntropy(onesEntropy);
		}
		node.setVisited(true);
		node.setLevel(level);

		if(branch.size() == 1) {
			if(classZeroCount>classOneCount)
			{
				node.setData("0");
			} else 
			{
				node.setData("1");
			}
		} else {
			attributes.remove(attributes.indexOf(rootNode.getData()));
			calculateEntropy(branch,node,attributes,level,treeIndex);
		}
	}
	
	//Prune section

	/*	public Node cloneTree(Node src) {
		Node clone = new Node();
		clone.setData(src.getData());
		clone.setGain(src.getGain());
		clone.setEntropy(src.getEntropy());
		clone.setLevel(src.getLevel());
		clone.setOnesCount(src.getOnesCount());
		clone.setZerosCount(src.getZerosCount());
		if(src.getLeftChild() != null) {
			clone.setLeftChild(cloneTree(src.getLeftChild()));
		}

		if(src.getRightChild() != null) {
			clone.setRightChild(cloneTree(src.getRightChild()));
		}

		return clone;
	}

	public int getNonLeafCount(Node node) {
		int count =0;
		if(node.getLeftChild()!=null)
		{
			count = count + getNonLeafCount(node.getLeftChild());
		}

		if(node.getRightChild()!=null)
		{
			count = count + getNonLeafCount(node.getRightChild());	
		}

		if(!(node.getData().equals("0")||node.getData().equals("1")))
		{
			count= count+1;
		}

		return count;
	}


	public Node postPrune(int L, int K){

		Node dBest = cloneTree(rootNode);
		double bestAccuracy = runDTreeOnTestData(dBest, validationDataList);
		double currAccuracy;
		for(int i=1;i<L+1;i++) {

			Node clonedTree = cloneTree(rootNode);
			int M= random.nextInt((K - 1) + 1) + 1;
			int N = getNonLeafCount(clonedTree);
			System.out.println("Non leaf node count: "+N);

			for(int j=1;j<M+1;j++){
				int P = random.nextInt((N - 1) + 1) + 1;
				pruneSubTree(clonedTree, P);
			}
			currAccuracy= runDTreeOnTestData(clonedTree, validationDataList);
			if(currAccuracy>bestAccuracy){
				dBest=clonedTree;
				bestAccuracy=currAccuracy;
			}
		}
		return dBest;
	}

	private int pruneSubTree(Node pruneNode, int pValue) {

		if(pValue==1) 
		{
			pValue--;
			if(pruneNode.getZerosCount()>pruneNode.getOnesCount())
			{
				pruneNode.setData("0");
			}
			else 
			{
				pruneNode.setData("1");
			}
			pruneNode.setLeftChild(null);
			pruneNode.setRightChild(null);
		}
		else 
		{
			pValue--;
			if(pValue>=1)
			{
				if(pruneNode.getLeftChild() != null &&
						!(pruneNode.getLeftChild().getData().equals("0") || pruneNode.getLeftChild().getData().equals("1"))) 
				{
					pValue = pruneSubTree(pruneNode.getLeftChild(), pValue);
				}
			}

			if(pruneNode.getRightChild() != null && !(pruneNode.getRightChild().getData().equals("0") || pruneNode.getRightChild().getData().equals("1"))) 
			{
				pValue = pruneSubTree(pruneNode.getRightChild(), pValue);
			}
		}
		return pValue;

	}*/

	/**
	 * Main function
	 * @param args
	 */
	public static void main(String args[]){
		if(args.length<2)
		{
			System.out.println("Invalid number of arguments!");
			return;
		}
		DecisionTreeClass decisionTree =new DecisionTreeClass();
		decisionTree.readInputFile(decisionTree.trainingDataList, args[0]+".csv");
		//decisionTree.readInputFile(decisionTree.validationDataList, args[2]+".csv");
		rootNode = new Node();
		decisionTree.calculateEntropy(decisionTree.trainingDataList,rootNode,attributeList, 0, 1);
		decisionTree.printTree(rootNode);
		decisionTree.readInputFile(decisionTree.testDataList, args[1]+".csv");
		double accuracy = decisionTree.runDTreeOnTestData(rootNode, decisionTree.testDataList);
		System.out.println("\n\nAccuracy: "+accuracy);
	}

}