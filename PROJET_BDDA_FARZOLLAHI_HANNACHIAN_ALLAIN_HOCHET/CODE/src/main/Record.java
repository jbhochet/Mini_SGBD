import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Record {
    private TableInfo tabInfo;
    private String[] recValues;

    public Record(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
        this.recValues = new String[tabInfo.getNumberOfColumns()];
    }

    public TableInfo getTabInfo() {
        return tabInfo;
    }

    public String[] getRecValues() {
        return recValues;
    }

    public void setRecValues(String[] recValues) {
        this.recValues = recValues;
    }

    public int writeToBuffer(ByteBuffer buffer, int pos) {
        buffer.position(pos);

        for (int i = 0; i < recValues.length; i++) {
            String value = recValues[i];
            DataType colType = tabInfo.getColumns()[i].getType();

            switch (colType) {
                case INT:
                    int intValue = Integer.parseInt(value);
                    buffer.putInt(intValue);
                    break;
                case FLOAT:
                    float floatValue = Float.parseFloat(value);
                    buffer.putFloat(floatValue);
                    break;
                case STRING:
                    writeFixedString(buffer, value, tabInfo.getColumns()[i].getT());
                    break;
                case VARSTRING:
                    writeVariableString(buffer, value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        return buffer.position() - pos;
    }

    private void writeFixedString(ByteBuffer buffer, String value, int maxLength) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.put(Arrays.copyOf(stringBytes, maxLength));
    }

    private void writeVariableString(ByteBuffer buffer, String value) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(stringBytes.length);
        buffer.put(stringBytes);
    }

    public int readFromBuffer(ByteBuffer buffer, int pos) {
        buffer.position(pos);

        for (int i = 0; i < tabInfo.getNumberOfColumns(); i++) {
            DataType colType = tabInfo.getColumns()[i].getType();

            switch (colType) {
                case INT:
                    int intValue = buffer.getInt();
                    recValues[i] = String.valueOf(intValue);
                    break;
                case FLOAT:
                    float floatValue = buffer.getFloat();
                    recValues[i] = String.valueOf(floatValue);
                    break;
                case STRING:
                    recValues[i] = readFixedString(buffer, tabInfo.getColumns()[i].getT());
                    break;
                case VARSTRING:
                    recValues[i] = readVariableString(buffer);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        return buffer.position() - pos;
    }

    private String readFixedString(ByteBuffer buffer, int maxLength) {
        byte[] stringBytes = new byte[maxLength];
        buffer.get(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8).trim();
    }

    private String readVariableString(ByteBuffer buffer) {
        int stringLength = buffer.getInt();
        byte[] stringBytes = new byte[stringLength];
        buffer.get(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

    public int getSize() {
        int size = 0;

        for (int i = 0; i < recValues.length; i++) {
            String value = recValues[i];
            DataType colType = tabInfo.getColumns()[i].getType();

            switch (colType) {
                case INT:
                    size += Integer.BYTES;
                    break;
                case FLOAT:
                    size += Float.BYTES;
                    break;
                case STRING:
                    size += tabInfo.getColumns()[i].getT();
                    break;
                case VARSTRING:
                    size += Integer.BYTES + value.getBytes(StandardCharsets.UTF_8).length;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Record: (");
        for (String val : recValues) {
            sb.append(val).append(", ");
        }
        sb.append(")\n");
        return sb.toString();
    }

    // New method to get all records
    public static List<Record> getAllRecords(TableInfo tabInfo) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        List<PageId> dataPages = fileManager.getDataPages(tabInfo);
        List<Record> records = new ArrayList<>();

        for (PageId pageId : dataPages) {
            records.addAll(fileManager.getRecordsInDataPage(tabInfo, pageId));
        }

        return records;
    }

    // New method to get filtered records
    public static List<Record> getFilteredRecords(TableInfo tabInfo, List<SelectCondition> conditions) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        List<PageId> dataPages = fileManager.getDataPages(tabInfo);
        List<Record> filteredRecords = new ArrayList<>();

        for (PageId pageId : dataPages) {
            List<Record> records = fileManager.getRecordsInDataPage(tabInfo, pageId);
            for (Record record : records) {
                if (record.matchesConditions(conditions)) {
                    filteredRecords.add(record);
                }
            }
        }

        return filteredRecords;
    }

    // New method to check if a record matches the given conditions
    private boolean matchesConditions(List<SelectCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true; // No conditions, all records match
        }

        for (SelectCondition condition : conditions) {
            int columnIndex = condition.getColumnIndex();
            String operator = condition.getOperator();
            String value = condition.getValue();

            // Retrieve the actual value from the record
            String recordValue = recValues[columnIndex];

            // Perform comparison based on the condition
            if (!compareValues(recordValue, operator, value)) {
                return false; // Record does not match this condition
            }
        }

        return true; // All conditions are satisfied, record matches
    }

    // Helper method to compare values based on the specified operator
    private boolean compareValues(String value1, String operator, String value2) {
        switch (operator) {
            case "=":
                return value1.equals(value2);
            case "<":
                // Implement less than comparison logic
                return value1.compareTo(value2) < 0;
            case ">":
                // Implement greater than comparison logic
                return value1.compareTo(value2) > 0;
            case "<=":
                // Implement less than or equal to comparison logic
                return value1.compareTo(value2) <= 0;
            case ">=":
                // Implement greater than or equal to comparison logic
                return value1.compareTo(value2) >= 0;
            case "<>":
                // Implement not equal to comparison logic
                return !value1.equals(value2);
            default:
                // Handle unsupported operators
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }
}
