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
    private String bestAttribute;

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
        this.bestSplitPointList = new Double[mTrainAttributes.size()][3];
        this.mTrainDataSet = trainDataSet;
        this.minLeafNumber = minLeafNumber;
        this.root = buildTree(this.mTrainDataSet);

    }

    private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet) {
        // TODO: add code here
        DecTreeNode node = new DecTreeNode(1, "", 0);
        if (dataSet.isEmpty()) return node;
        else if (dataSet.size() <= minLeafNumber || mTrainAttributes.isEmpty()) {
            node.classLabel = majority(dataSet);
            return node;
        }

        int match = 1;
        int last_class = dataSet.get(0).get(dataSet.get(0).size() - 1).intValue();
        int ones = 0;
        int zeros = 0;
        for (ArrayList<Double> curr : dataSet) {
            int current_class = curr.get(curr.size() - 1).intValue();
            if (current_class != last_class) match = 0;
            if (current_class == 0) zeros++;
            if (current_class == 1) ones++;
        }
        if (match == 1) {
            node.classLabel = match;
            return node;
        }
        String bestAttribute = getBestAttribute(dataSet);

        double best_thresh = -1;
        double best_gain = -1;

        //Sort by attribute
        ArrayList<DataBinder> databinds = new ArrayList<>();
        for (ArrayList<Double> example : dataSet) {
            for (int i = 0; i < example.size(); i++) {
                databinds.add(new DataBinder(i, example));
            }
        }
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


            int last = databinds.get(0).getData().get(databinds.get(0).getData().size()-1).intValue();
            double lastVal = databinds.get(0).getArgItem();
            ArrayList<Double> thresholds = getThresholds(databinds);

            double bestAttrSplit = 0;

            for (double current : thresholds) {
                int left_size = 0;
                int right_size = 0;
                int l_one = 0;
                int r_one = 0;
                for (ArrayList<Double> instance : dataSet) {
                    if (instance.get(i) <= current) {
                        left_size++;
                        if (instance.get(instance.size() - 1) == 1) {
                            l_one++;
                        }
                    } else {
                        right_size++;
                        if (instance.get(instance.size() - 1) == 1) {
                            r_one++;
                        }
                    }
                }
                double gain = calculateEntropy(left_size, right_size, zeros, ones, l_one, r_one);
                if (gain >= best_gain) {
                    best_gain = gain;
                    bestAttribute = mTrainAttributes.get(i);
                    best_thresh = current;
                }
                if (gain >= bestAttrSplit) {
                    bestAttrSplit = gain;
                }

            }
            bestSplitPointList[i][0] = bestAttrSplit;
            bestSplitPointList[i][1] = best_thresh;
        }

        node.threshold = best_thresh;
        node.attribute = bestAttribute;

        ArrayList<ArrayList<Double>> left_data = new ArrayList<>();
        ArrayList<ArrayList<Double>> right_data = new ArrayList<>();
        for (ArrayList<Double> instance : dataSet) {
            if (instance.get(mTrainAttributes.indexOf(bestAttribute)) <= best_thresh) {
                left_data.add(instance);
            } else
                right_data.add(instance);
        }

        // System.out.println("recurse");
        System.out.println("left data size: " + left_data.size() + " gain: " + best_gain + " attribute: " + node.attribute);
        node.left = buildTree(left_data);
        System.out.println("right data size: " + right_data.size());
        node.right = buildTree(right_data);
     //   System.out.println("method exit");
        return node;
    }

    private String getBestAttribute(ArrayList<ArrayList<Double>> data) {
        String attribute = "";
        return attribute;
    }

    private ArrayList<Double> getThresholds (ArrayList<DataBinder> data) {
        ArrayList<Double> thresholds = new ArrayList<>();
        int prevClass = data.get(0).getData().get( data.get(0).getData().size()-1).intValue();
        double prevVal =  data.get(0).getArgItem();
        for(DataBinder current : data ) {
            int currentClass = current.getData().get(current.getData().size()-1).intValue();
            if(currentClass == prevClass) {
                thresholds.add((current.getArgItem()+prevVal)/2);
            }
            prevVal = current.getArgItem();
            prevClass = currentClass;
        }
        return thresholds;
    }

    private Double calculateEntropy(int left_size, int right_size, int zeros, int ones, int l_one, int r_one) {

        int total_size = left_size + right_size;

        double p_zero = ((double) zeros / (double) total_size);
        double p_one = (double) ones / (double) total_size;
        //prob instances is on left side
        double p_left = ((double) left_size / (double) total_size);
        //prob instances is on right side
        double p_right = ((double) right_size / (double) total_size);
        //prob instances is on left side and class zero
        double p_lz = (double) (left_size - l_one) / (double) left_size;
        //prob instances is on right side and class zero
        double p_rz = ((double) right_size - (double) r_one) / (double) right_size;
        //prob instances is on left side and class one
        double p_lo = (double) (l_one) / (double) left_size;
        //prob instances is on right side and class one
        double p_ro = (double) (r_one) / (double) right_size;

        double entropy = (-p_zero * (Math.log(p_zero) / Math.log(2))) + (-p_one * (Math.log(p_one) / Math.log(2)));
        double sce_l = 0;

        if (p_lo != 0 && p_lz != 0) {
            sce_l = -(p_lz) * (Math.log(p_lz) / Math.log(2)) + -(p_lo) * (Math.log(p_lo) / Math.log(2));
        }
        else if(p_lo != 0) {
            sce_l = -(p_lo) * (Math.log(p_lo) / Math.log(2));
        }
        else if(p_lz != 0) {
            sce_l = -(p_lz) * (Math.log(p_lz) / Math.log(2));
        }
        else
            return -1.0;


        double sce_r = 0;
        if(p_ro == 0 || p_rz == 0) return 0.0;
        if (p_ro != 0 && p_rz != 0) {
            sce_r = -(p_rz) * (Math.log(p_rz) / Math.log(2)) + -(p_ro) * (Math.log(p_ro) / Math.log(2));
        }
        else if(p_ro != 0) {
            sce_r = -(p_ro) * (Math.log(p_ro) / Math.log(2));
        }
        else if(p_rz != 0) {
            sce_r = -(p_rz) * (Math.log(p_rz) / Math.log(2));
        }
        else return -2.0;



        double c_ent = (p_left) * sce_l + (p_right) * sce_r;
        /*
        entropy = (-p_zero)*(Math.log(p_zero)/Math.log(2)) + (-p_one)*(Math.log(p_one)/Math.log(2));
        //probability class=0 given attribute is less than threshold
        double cond_zero = (left_size/(double)total_size)*((double)(left_size-l_one)/left_size);
        //System.out.println("Cond_Zero:" + cond_zero);
        double cond_one = (left_size/(double)total_size)*((double)(l_one)/left_size);
        double condit_entropy =
                (-cond_zero*(Math.log(cond_zero)/Math.log(2)))+ (-cond_one*(Math.log(cond_one)/Math.log(2)));
        condit_entropy -= entropy;
        // (left_size/total_size)*condit_entropy + (right_size/total_size)*condit_entropy;
        entropy = ent - c_ent;
        //System.out.println(entropy);
        */
        entropy = entropy - c_ent;
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

    private int majority(ArrayList<ArrayList<Double>> data) {
        int ones = 0;
        int zeros = 0;
        int majority = 1;

        for (ArrayList<Double> instance : data) {
            if (instance.get(instance.size() - 1) == 0) zeros++;
            else ones++;
        }
        if (zeros > ones) majority = zeros;
        return majority;
    }

    public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
        this.mTrainAttributes = trainAttributeNames;
        this.mTrainDataSet = dataSet;
        this.minLeafNumber = minLeafNumber;
        // TODO: add code here

        //TODO: modify this example print statement to work with your code to output attribute names and info gain. Note the %.6f output format.
        for (int i = 0; i < bestSplitPointList.length; i++) {
            System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList[i][0]) + " thresh: " + bestSplitPointList[i][1] + " attribute " + bestAttribute);
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
