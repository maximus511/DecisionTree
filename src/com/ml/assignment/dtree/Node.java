package com.ml.assignment.dtree;

/**
 * Node class for nodes of decision tree
 * @author Rahul
 *
 */
public class Node
{
	private String data= new String();
	private double gain = 0.0;
	private double entropy = 0.0;
	private Node leftChild = null;
	private Node rightChild = null;
	private int onesCount = 0;
	private int zerosCount = 0;
	private int level = 0;
	private boolean visited = false;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getOnesCount() {
		return onesCount;
	}

	public void setOnesCount(int onesCount) {
		this.onesCount = onesCount;
	}

	public int getZerosCount() {
		return zerosCount;
	}

	public void setZerosCount(int zerosCount) {
		this.zerosCount = zerosCount;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
