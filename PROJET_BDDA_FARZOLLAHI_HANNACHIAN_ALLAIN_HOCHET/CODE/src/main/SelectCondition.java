public class SelectCondition {
    private int columnIndex;
    private String operator;
    private String value;

    public SelectCondition(int columnIndex, String operator, String value) {
        this.columnIndex = columnIndex;
        this.operator = operator;
        this.value = value;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
}
