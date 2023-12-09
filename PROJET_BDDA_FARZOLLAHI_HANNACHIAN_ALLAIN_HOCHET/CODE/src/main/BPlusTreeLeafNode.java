import java.util.ArrayList;
import java.util.List;

public class BPlusTreeLeafNode extends BPlusTreeNode {

    private List<BPlusTreeKey> keys;

    public BPlusTreeLeafNode(int order) {
        super(order);
        this.keys = new ArrayList<>();
    }

    // Search for records in the leaf node based on a given key
    @Override
    public List<Record> search(String key) {
        List<Record> result = new ArrayList<>();
        for (BPlusTreeKey treeKey : keys) {
            if (treeKey.getKey().equals(key)) {
                result.add(treeKey.getRecord());
            }
        }
        return result;
    }

    // Insert a new key into the leaf node
    @Override
    public BPlusTreeNode insert(BPlusTreeKey key) {
        int i = 0;
        while (i < keys.size() && key.getKey().compareTo(keys.get(i).getKey()) > 0) {
            i++;
        }
        keys.add(i, key);

        // Check if the leaf node has exceeded its capacity
        if (keys.size() > 2 * order) {
            // Split the leaf node and return the new node
            return split();
        }

        return null;
    }

    // Private method to split the leaf node
    private BPlusTreeNode split() {
        int middle = keys.size() / 2;

        // Create a new leaf node
        BPlusTreeLeafNode newNode = new BPlusTreeLeafNode(order);

        // Move the keys to the new leaf node
        newNode.keys.addAll(keys.subList(middle, keys.size()));
        keys.subList(middle, keys.size()).clear();

        // Return the new leaf node
        return newNode;
    }

    
    // Notify the parent about the split
    @Override
    public void notifyParent(String key, BPlusTreeNode newNode) {
        // Tired
    }
}
