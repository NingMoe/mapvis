package mapvis.common.datatype;


import java.util.*;

public class NodeUtils {

    public static int rebuildId(Node root, int nextid) {
        root.id = Integer.toString(nextid++);
        for (Node child : root.getChildren()) {
            nextid = rebuildId(child, nextid);
        }
        return nextid;
    }

    public static void populateLevel(Node root, int level){
        root.setVal("level", level);
        for (Node child : root.getChildren()) {
            populateLevel(child, level + 1);
        }
    }

    int populateSize(Node root){
       return 0;
    }

    public static class TreeStatistics{
        public int maxDepth;
        public int numOfLeaves;
        public int sumOfDepthsOfLeaves;
        public String maxDepthPathName;

        public TreeStatistics(final int maxDepth, final String maxDepthPathName){
            this.maxDepth = maxDepth;
            this.maxDepthPathName = maxDepthPathName;
            this.sumOfDepthsOfLeaves = 0;
            this.numOfLeaves = 0;
        }

        public TreeStatistics(final int maxDepth, final String maxDepthPathName, final int numOfLeaves, final int sumOfDepthsOfLeaves) {
            this.maxDepth = maxDepth;
            this.maxDepthPathName = maxDepthPathName;
            this.sumOfDepthsOfLeaves = sumOfDepthsOfLeaves;
            this.numOfLeaves = numOfLeaves;
        }

        static TreeStatistics createNew(final int maxDepth, final String maxDepthPathName, final int numOfLeaves, final int sumOfDepthsOfLeaves){
            return new TreeStatistics(maxDepth, maxDepthPathName, numOfLeaves, sumOfDepthsOfLeaves);
        }

        static TreeStatistics createNew(final int maxDepth, final String maxDepthPathName){
            return new TreeStatistics(maxDepth, maxDepthPathName);
        }

        public double getAverageDepth(){
            if(numOfLeaves == 0)
                return 0;
            return ((float) sumOfDepthsOfLeaves) / ((float) numOfLeaves);
        }

        @Override
        public String toString() {
            return "Statistics: Max Depth: " + maxDepth +  " Leaves: " + numOfLeaves + " sum: " + sumOfDepthsOfLeaves + " avarge Tree Depth " + getAverageDepth() + " Tree: " + maxDepthPathName;
        }
    }

    private static TreeStatistics getTreeStatisticsOfSubNode(final Node node, final int currDepth)
    {
        if(node.getSize() == 0 || node.getLabel().equals("*")){
            return TreeStatistics.createNew(currDepth, node.getLabel(), 0, 0);
        }else if(node.getChildren().size() == 0){
            return TreeStatistics.createNew(currDepth, node.getLabel(), 1, currDepth);
        }

        TreeStatistics maxTreeDepthStatistics = TreeStatistics.createNew(0, node.getLabel());
        for(Node child: node.getChildren()){
            // children with no size are dummy nodes e.g. folder with no files
            if(child.getSize() == 0){
                continue;
            }

            TreeStatistics childStatistics = getTreeStatisticsOfSubNode(child, currDepth + 1);

            maxTreeDepthStatistics.numOfLeaves += childStatistics.numOfLeaves;
            maxTreeDepthStatistics.sumOfDepthsOfLeaves += childStatistics.sumOfDepthsOfLeaves;
            if(childStatistics.maxDepth > maxTreeDepthStatistics.maxDepth){
                maxTreeDepthStatistics.maxDepthPathName = node.getLabel() + ("->" + childStatistics.maxDepthPathName);
                maxTreeDepthStatistics.maxDepth = childStatistics.maxDepth;
            }
        }
        return maxTreeDepthStatistics;
    }

    public static TreeStatistics getTreeDepthStatistics(final Node node)
    {
        return getTreeStatisticsOfSubNode(node, 0);
    }

    public static Node filterByDepth(final Node node, final int depth){
        if(depth == 0){
            Node cappedNode = new Node(node.getId(), node.getLabel());
            cappedNode.setSize(node.getSize());
            return cappedNode;
        }
        Node subTreeNode = new Node(node.getId(), node.getLabel());
//        List<Node> filteredChilds = new ArrayList<>();
        for(Node child: node.getChildren()){
            Node newChildNode = filterByDepth(child, depth - 1);
            subTreeNode.getChildren().add(newChildNode);
        }
        subTreeNode.setSize(node.getSize());
        return subTreeNode;
    }

    public static double filterBySize(Node root, double min, String sizeKey){
        if (sizeKey == null) sizeKey = "size";

        if (root.getChildren().isEmpty())
            return (double) root.getVal("size");

        double size = 0;

        List<Node> removed = new LinkedList<>();

        for (Node child : root.getChildren()) {
            double sizeChild = filterBySize(child, min, sizeKey);
            if (sizeChild < min)
                removed.add(child);
            else
                size += sizeChild;
        }

        root.getChildren().removeAll(removed);
        root.setVal(sizeKey, size);

        return size;
    }

    public static List<Node> getLeaves(Node node){
        if (node.getChildren().isEmpty())
            return Arrays.asList(node);

        ArrayList<Node> leaves = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getLeaves)
                .forEach(leaves::addAll);
        return leaves;

    }

    public static List<Node> getDecedents(Node node){
        ArrayList<Node> nodes = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getDecedents)
                .forEach(nodes::addAll);

        nodes.addAll(node.getChildren());

        return nodes;
    }

}
