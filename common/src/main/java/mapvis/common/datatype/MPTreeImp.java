package mapvis.common.datatype;

import java.util.*;
import java.util.stream.Collectors;

// TODO: not thread-safe
public class MPTreeImp<T> implements Tree2<T> {

    static class MPTreeNode<T> {
        public int left;
        public int right;
        public int depth;
        public int weight;
        public T element;

        public MPTreeNode<T> parent;
        public List<MPTreeNode<T>> children = new ArrayList<>();
    }
    boolean dirty = false;
    void checkDirty(){
        if (dirty) {
            populate(1, 0, root);
            refreshLeafCache();
            dirty = false;
        }
    }
    void markDirty(){
        dirty = true;
    }

    public MPTreeNode<T> root;
    // object to node mapping
    private Map<T, MPTreeNode<T>> o2n = new LinkedHashMap<>();

    @Override
    public Set<T> getChildren(T obj){
        checkDirty();
        return o2n.get(obj).children.stream().map(n -> n.element)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    @Override
    public T getParent(T node){
        checkDirty();
        return o2n.get(node).parent.element;
    }
    @Override
    public Set<T> getNodes(){
        checkDirty();
        return Collections.unmodifiableSet(o2n.keySet());
    }
    @Override
    public T getRoot() {
        checkDirty();
        if (root == null)
            return null;
        return root.element;
    }
    @Override
    public int getDepth(T elem){
        checkDirty();
        MPTreeNode<T> node = o2n.get(elem);
        return node.depth;
    }
    @Override
    public int getWeight(T elem){
        checkDirty();
        MPTreeNode<T> node = o2n.get(elem);
        return node.weight;
    }
    @Override
    public Set<T> getLeaves(){
        checkDirty();
        return Collections.unmodifiableSet(leaves);
    }
    // lowest common ancestor
    @Override
    public T getLCA(T o1, T o2){
        checkDirty();
        MPTreeNode<T> n1 = o2n.get(o1);
        MPTreeNode<T> n2 = o2n.get(o2);
        MPTreeNode<T> lca = getLCA(n1, n2);
        if (lca != null)
            return lca.element;
        return null;
    }
    @Override
    public List<T> getPathToNode(T elem){
        checkDirty();
        ArrayList<T> list = new ArrayList<>();
        MPTreeNode<T> node = o2n.get(elem);
        list.add(elem);

        while (node.parent != null){
            node = node.parent;
            list.add(node.element);
        }

        Collections.reverse(list);
        return list;
    }
    @Override
    public boolean isAncestorOf(T ancestor, T decedent){
        checkDirty();
        MPTreeNode<T> na = o2n.get(ancestor);
        MPTreeNode<T> nd = o2n.get(decedent);
        return na != null && nd != null &&
                na.left < nd.left && na.right > nd.right;
    }
    @Override
    public boolean isSibling(T o1, T o2){
        checkDirty();
        MPTreeNode<T> n1 = o2n.get(o1);
        MPTreeNode<T> n2 = o2n.get(o2);
        return n1.parent == n2.parent;
    }

    public void setRoot(T obj){
        // TODO: what if the root is already in the tree
        if (o2n.containsKey(obj))
            throw new RuntimeException("the child already exists");
        if (root == null) {
            this.root = new MPTreeNode<>();
            this.root.element = obj;
        }
        else
            throw new RuntimeException("can't change the root");
        o2n.put(obj, root);
        markDirty();
    }

    public void addChild(T parent, T child, int weight){
        if (o2n.containsKey(child))
            throw new RuntimeException("the child already exists");
        if (!o2n.containsKey(parent))
            throw new RuntimeException("the parent doesn't exist");

        MPTreeNode<T> nParent = o2n.get(parent);

        MPTreeNode<T> nChild = new MPTreeNode<>();
        nChild.element = child;
        nChild.weight  = weight;
        nParent.children.add(nChild);
        nChild.parent = nParent;

        o2n.put(child, nChild);
        markDirty();
    }

    // recalculate the left and right value of all nodes.
    /// @return: right
    void populate(int left, int depth, MPTreeNode<T> node){
        node.left = left;
        node.depth = depth;
        left ++;

        if (node.children.size() == 0){
            node.right = left;
            return;
        }

        node.weight = 0;

        for (MPTreeNode<T> child : node.children) {
            populate(left, depth + 1, child);
            node.weight += child.weight;
            left = child.right + 1;
        }
        node.right = left;
    }
    //public  T root;
    Set<T> leaves = new HashSet<>();

    void refreshLeafCache(){
        leaves.clear();
        refreshLeafCache(root);
    }

    void refreshLeafCache(MPTreeNode<T> node){
        if (node.children.size() == 0)
            leaves.add(node.element);
        else {
            node.children.forEach(this::refreshLeafCache);
        }
    }

    MPTreeNode<T> getLCA(MPTreeNode<T> n1, MPTreeNode<T> n2) {
        if (n1 == null)
            return null;
        if (n1.left < n2.left && n1.right > n2.right) {
            return n1;
        }
        return getLCA(n1.parent, n2);
    }


    public static MPTreeImp<Node> from(Node root) {
        MPTreeImp<Node> tree = new MPTreeImp<>();
        tree.setRoot(root);
        recAddChild(tree,root);
        tree.getRoot();
        return tree;
    }

    static void recAddChild(MPTreeImp<Node> tree, Node node){
        for (Node child : node.getChildren()) {
            tree.addChild(node, child, (int)(double)node.getVal("size"));
            recAddChild(tree, child);
        }
    }

}
