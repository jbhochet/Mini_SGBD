public class BPlusTreeKey {
    private String key;
    private Record record;

    public BPlusTreeKey(String key, Record record) {
        this.key = key;
        this.record = record;
    }

    public String getKey() {
        return key;
    }

    public Record getRecord() {
        return record;
    }
}
