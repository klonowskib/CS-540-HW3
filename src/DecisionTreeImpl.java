import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * <p>
 * You must add code for the 1 member and 4 methods specified below.
 * <p>
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl {
    private DecTreeNode root;
    //ordered list of attributes
    private List<String> mTrainAttributes;
    //
    private ArrayList<ArrayList<Double>> mTrainDataSet;
    //Min number of instances per leaf.
    private int minLeafNumber = 10;
    private Double[][] bestSplitPointList;

    /**
     * Answers static questions about decision trees.
     */
    DecisionTreeImpl() {
        // no code necessary this is void purposefully
    }

    /**
     * Build a decision tree given a training set then prune it using a tuning set.
     * <p>
     * //@param train: the training set
     * //@param tune:  the tuning set
     */
    DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
        this.mTrainAttributes = trainAttributeNames;
        this.bestSplitPointList = new Double[mTrainAttributes.size()][1];
        this.mTrainDataSet = trainDataSet;
        this.minLeafNumber = minLeafNumber;
        this.root = buildTree(this.mTrainDataSet);

    }

    private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet) {
        // TODO: add code here
        double best_thresh = -1;
        double best_gain = -1;
        String best_attr = "";
        //Check if all classes match
        boolean classified = true;
        int last = -1;
        int ones = 0;
        int zeros = 0;
        for (ArrayList<Double> inst : dataSet) {
            int type = inst.get(inst.size()-1).intValue();
            if (type == last || last == -1) {
                last = inst.get(inst.size() - 1).intValue();
            } else
                classified = false;
            if (type == 1) ones++;
            else if(type == 0) zeros++;
        }
        System.out.println("Ones: " + ones + " zeros: " +zeros);
        if (classified) {
            return new DecTreeNode(last, null, -1);
        } else {
            //Sort by attribute
            ArrayList<DataBinder> databinds = new ArrayList<DataBinder>();
            for (ArrayList<Double> example : dataSet) {
                for (int i = 0; i < example.size(); i++) {
                    databinds.add(new DataBinder(i, example));
                }
            }
            ArrayList<ArrayList<Double>> sorted = new ArrayList<ArrayList<Double>>();
            for (int i = 0; i < mTrainAttributes.size(); i++) {
                Comparator<DataBinder> myComparator = new Comparator<DataBinder>() {
                    @Override
                    public int compare(DataBinder t1, DataBinder t2) {
                        Double t1_class = t1.getData().get(t1.getData().size() - 1);
                        Double t2_class = t2.getData().get(t2.getData().size() - 1);
                        if (t1.getArgItem() == t2.getArgItem())
                            return t1_class.compareTo(t2_class);
                        else
                            return t1.getArgItem().compareTo(t2.getArgItem());
                    }
                };
                Collections.sort(databinds, myComparator);
/*
                for(DataBinder bind : databinds) {
                    System.out.println(bind.getArgItem());
                }
                */
                last = -1;
                ArrayList<Double> pot_threshs = new ArrayList<Double>();
                double thresh;
                for (DataBinder instance : databinds) {
                    ArrayList<Double> current = instance.getData();
                    int current_class = current.get(current.size() - 1).intValue();
                    if (current_class != last) {
                        thresh = current.get(i);

                        pot_threshs.add(thresh);
                    }
                    last = current_class;
                }

                double gain = 0;
                int left_size = 0;
                int right_size = 0;
                double bestAttrSplit = 0;
                System.out.println("BOOM:" + pot_threshs.size());
                for (double curr_t : pot_threshs) {
                    for (ArrayList<Double> instance : dataSet) {
                        if (instance.get(i) <= curr_t)
                            left_size++;
                        else
                            right_size++;
                    }
                    gain = calculateEntropy(left_size, right_size, zeros, ones);
                    //System.out.println("gain: "+ gain);
                    if (gain >= best_gain) {
                        best_gain = gain;
                        best_attr = mTrainAttributes.get(i);
                        best_thresh = curr_t;
                    }
                    if (gain >= bestAttrSplit) {
                        bestAttrSplit = gain;
                    }
                }
                bestSplitPointList[i][0] = bestAttrSplit;

                /*
                for (DataBinder binder : databinds) {
                    sorted.add(sorted.size() - 1, binder.getData());
                }
                */

            }
        }
        return new DecTreeNode(-1, null, -1);
    }

    private Double calculateEntropy(int left_size, int right_size, int zeros, int ones) {


        int total_size = left_size + right_size;
        //System.out.println("Ones: " + ones + " zeros: " +zeros + " total " + total_size + " left " + left_size + " right " + right_size);

        double p_zero = ((double)zeros/(double) total_size);
        double p_one = (double)ones/(double)total_size;
        //System.out.println("P_zero: " + p_zero + " p_one: " + p_one);
        double entropy = (-p_zero)*(Math.log(p_zero)/Math.log(2)) + (-p_one/total_size)*(Math.log(p_one)/Math.log(2));
        return entropy;
    }

    public int classify(List<Double> instance) {
        DecTreeNode current = this.root;
        while (!current.isLeaf()) {
            int attr = instance.get(mTrainAttributes.indexOf(current.attribute)).intValue();
            if (attr <= current.threshold && current.left != null)
                current = current.left;
            else if (current.right != null)
                current = current.right;
        }
        return current.classLabel;
    }

    public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
        this.mTrainAttributes = trainAttributeNames;
        this.mTrainDataSet = dataSet;
        this.minLeafNumber = minLeafNumber;
        // TODO: add code here

        //TODO: modify this example print statement to work with your code to output attribute names and info gain. Note the %.6f output format.
        for (int i = 0; i < bestSplitPointList.length; i++) {
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
        if (node.left.isLeaf()) {
            System.out.println(": " + String.valueOf(node.left.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.left);
        }
        System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
        if (node.right.isLeaf()) {
            System.out.println(": " + String.valueOf(node.right.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.right);
        }
    }

    public Double printAccuracy(int numEqual, int numTotal) {
        Double accuracy = numEqual / (double) numTotal;
        System.out.println(accuracy);
        return accuracy;
    }

    /**
     * Private class to facilitate instance sorting by argument position since java doesn't like passing variables to comparators through
     * nested variable scopes.
     */
    private class DataBinder {

        public ArrayList<Double> mData;
        public int i;

        public DataBinder(int i, ArrayList<Double> mData) {
            this.mData = mData;
            this.i = i;
        }

        public Double getArgItem() {
            return mData.get(i);
        }

        public ArrayList<Double> getData() {
            return mData;
        }

    }

}
