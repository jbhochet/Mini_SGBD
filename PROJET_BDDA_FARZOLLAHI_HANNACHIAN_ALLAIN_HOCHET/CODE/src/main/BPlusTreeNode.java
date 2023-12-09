import java.util.List;

public abstract class BPlusTreeNode {

    protected int order;
    protected BPlusTreeNode parent;

    public BPlusTreeNode(int order) {
        this.order = order;
        this.parent = null;
    }

    // Abstract method to search for a key in the node
    public abstract List<Record> search(String key);

    // Abstract method to insert a key into the node
    public abstract BPlusTreeNode insert(BPlusTreeKey key);

    // Abstract method to notify the parent about the split
    public abstract void notifyParent(String key, BPlusTreeNode newNode);

    // Method to set the parent of the node
    public void setParent(BPlusTreeNode parent) {
        this.parent = parent;
    }
}
