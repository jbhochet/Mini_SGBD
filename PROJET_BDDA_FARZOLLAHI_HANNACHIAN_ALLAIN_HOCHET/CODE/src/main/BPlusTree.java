import java.util.List;

public class BPlusTree {
    private BPlusTreeNode root;
    private int order; // Order of the tree

    // Constructor for BPlusTree
    public BPlusTree(int order) {
        this.root = new BPlusTreeLeafNode(order);
        this.order = order;
    }

    // Method to create B+Tree index from a list of records and a specific column index
    public void createIndex(List<Record> records, int columnIndex) {
        for (Record record : records) {
            // Extract the key from the specified column index
            String key = record.getRecValues()[columnIndex];
            // Insert a new key into the B+Tree
            insert(new BPlusTreeKey(key, record));
        }
    }

    // Method to search for records in the B+Tree based on a given value
    public List<Record> search(String value) {
        return root.search(value);
    }

    // Method to insert a new key into the B+Tree
    public void insert(BPlusTreeKey key) {
        // Try to insert the key into the root
        BPlusTreeNode newRoot = root.insert(key);

        // If the root has split during insertion, update the root
        if (newRoot != null) {
            root = newRoot;
        }
    }

    // Method to delete records with a specific value from the B+Tree
    public void delete(String value) {
        // TODO: Implement deletion in the B+Tree
        // Tired as fuck !
    }
}
