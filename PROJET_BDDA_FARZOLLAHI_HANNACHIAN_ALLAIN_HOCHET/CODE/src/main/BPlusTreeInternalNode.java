import java.util.ArrayList;
import java.util.List;
public class BPlusTreeInternalNode extends BPlusTreeNode {

    private List<String> keys;
    private List<BPlusTreeNode> children;

    public BPlusTreeInternalNode(int order) {
        super(order);
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    // Insert a key and its corresponding child into the internal node
    public void insert(String key, BPlusTreeNode child) {
        int i = 0;
        // Find the position to insert the key
        while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        // Insert the key and its corresponding child
        keys.add(i, key);
        children.add(i + 1, child);

        // Check if the internal node has exceeded its capacity
        if (keys.size() > 2 * order) {
            // Split the internal node and return the new node
            split();
        }
    }

    // Search for a key in the internal node
    @Override
    public List<Record> search(String key) {
        int i = 0;
        // Find the child node to traverse based on the key
        while (i < keys.size() && key.compareTo(keys.get(i)) >= 0) {
            i++;
        }
        // Return the child node to traverse
        return children.get(i).search(key);
    }

 // Private method to split the internal node
    private BPlusTreeNode split() {
        int middle = keys.size() / 2;

        // Create a new internal node
        BPlusTreeInternalNode newNode = new BPlusTreeInternalNode(order);

        // Move the keys and children to the new internal node
        newNode.keys.addAll(keys.subList(middle + 1, keys.size()));
        newNode.children.addAll(children.subList(middle + 1, children.size()));

        // Update the current internal node
        keys.subList(middle, keys.size()).clear();
        children.subList(middle + 1, children.size()).clear();

        // Notify the parent about the split
        notifyParent(keys.get(middle), newNode);

        // Return the new internal node
        return newNode;
    }

    // Notify the parent about the split
    @Override
    public void notifyParent(String key, BPlusTreeNode newNode) {
        int i = 0;
        // Find the position to insert the key in the parent
        while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        // Insert the key and the new node into the parent
        keys.add(i, key);
        children.add(i + 1, newNode);

        // Check if the parent has exceeded its capacity
        if (keys.size() > 2 * order) {
            // Split the parent node
            splitParent();
        }
    }

    // Private method to split the parent node
    private void splitParent() {
        int middle = keys.size() / 2;

        // Create a new internal node for the parent
        BPlusTreeInternalNode newParent = new BPlusTreeInternalNode(order);

        // Move the keys and children to the new parent node
        newParent.keys.addAll(keys.subList(middle + 1, keys.size()));
        newParent.children.addAll(children.subList(middle + 1, children.size()));

        // Update the current internal node
        keys.subList(middle, keys.size()).clear();
        children.subList(middle + 1, children.size()).clear();

        // Notify the grandparent about the split
        notifyParent(keys.get(middle), newParent);
    }

    @Override
    public BPlusTreeNode insert(BPlusTreeKey key) {
        int i = 0;
        // Find the child node to traverse based on the key
        while (i < keys.size() && key.getKey().compareTo(keys.get(i)) >= 0) {
            i++;
        }
        // Insert the key into the appropriate child node
        BPlusTreeNode child = children.get(i).insert(key);
        // Check if the child node has split and returned a new node
        if (child != null) {
            // Insert the key and the new child into the internal node
            keys.add(i, key.getKey());
            children.add(i + 1, child);
            
            // Check if the internal node has exceeded its capacity
            if (keys.size() > 2 * order) {
                // Split the internal node and return the new node
                return split();
            }
        }
        return null;
    }
}
