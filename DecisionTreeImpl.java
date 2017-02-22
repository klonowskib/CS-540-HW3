import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl{
	private DecTreeNode root;
	//ordered list of attributes
	private List<String> mTrainAttributes; 
	//
	private ArrayList<ArrayList<Double>> mTrainDataSet;
	//Min number of instances per leaf.
	private int minLeafNumber = 10;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = trainDataSet;
		this.minLeafNumber = minLeafNumber;
		this.root = buildTree(this.mTrainDataSet);
	}
	
	private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet){
		// TODO: add code here
	}
	
	public int classify(List<Double> instance) {
		// TODO: add code here

	}
	
	public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = dataSet;
		this.minLeafNumber = minLeafNumber;
		// TODO: add code here
		
		
		//TODO: modify this example print statement to work with your code to output attribute names and info gain. Note the %.6f output format.
		for(int i = 0; i<bestSplitPointList.length; i++){
			System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList[i][0]));
		}	
	}

	
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {
		printTreeNode("", this.root);
	}

	/**
	 * Recursively prints the tree structure, left subtree first, then right subtree.
	 */
	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + node.attribute;
			
		System.out.print(printStr + " <= " + String.format("%.6f", node.threshold));
		if(node.left.isLeaf()){
			System.out.println(": " + String.valueOf(node.left.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
		if(node.right.isLeaf()){
			System.out.println(": " + String.valueOf(node.right.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
		
		
	}
	
	public double printAccuracy(int numEqual, int numTotal){
		double accuracy = numEqual/(double)numTotal;
		System.out.println(accuracy);
		return accuracy;
	}

	/**
	 * Private class to facilitate instance sorting by argument position since java doesn't like passing variables to comparators through
	 * nested variable scopes.
	 * */
	private class DataBinder{
		
		public ArrayList<Double> mData;
		public int i;
		public DataBinder(int i, ArrayList<Double> mData){
			this.mData = mData;
			this.i = i;
		}
		public double getArgItem(){
			return mData.get(i);
		}
		public ArrayList<Double> getData(){
			return mData;
		}
		
	}

}
