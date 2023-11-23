import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^SELECT \\* FROM (\\w+)( WHERE (.+))?$");

    private String tableName;
    private List<SelectCondition> conditions;
    private TableInfo tableInfo; // Instance variable

    public SelectCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid command");
        tableName = matcher.group(1);
        tableInfo = DatabaseInfo.getInstance().getTableInfo(tableName); // Set tableInfo here
        conditions = parseConditions(matcher.group(3)); // Pass conditionsStr only
    }

    private List<SelectCondition> parseConditions(String conditionsStr) {
        if (conditionsStr == null || conditionsStr.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<SelectCondition> conditionList = new ArrayList<>();
        String[] conditionStrings = conditionsStr.split(" AND ");
        for (String conditionString : conditionStrings) {
            // Assume simple conditions: "columnName operator value"
            String[] parts = conditionString.split("\\s+");
            if (parts.length == 3) {
                int columnIndex = tableInfo.getColumnIndex(parts[0]);
                String operator = parts[1];
                String value = parts[2];
                conditionList.add(new SelectCondition(columnIndex, operator, value));
            }
        }

        return conditionList;
    }


    @Override
    public void execute() throws IOException {
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        TableInfo tableInfo = databaseInfo.getTableInfo(tableName);
        FileManager fileManager = FileManager.getInstance();

        if (conditions == null) {
            // Execute a simple SELECT without conditions
            List<PageId> dataPages = fileManager.getDataPages(tableInfo);
            for (PageId dataPageId : dataPages) {
                List<Record> records = fileManager.getRecordsInDataPage(tableInfo, dataPageId);
                // Process the retrieved records as needed
                processRecords(records);
            }
        } else {
            // Execute SELECT with conditions
            List<Record> filteredRecords = filterRecords(tableInfo);
            // Process the filtered records
            processRecords(filteredRecords);
        }
    }

    private List<Record> filterRecords(TableInfo tableInfo) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        List<PageId> dataPages = fileManager.getDataPages(tableInfo);
        List<Record> filteredRecords = new ArrayList<>();

        for (PageId dataPageId : dataPages) {
            List<Record> records = fileManager.getRecordsInDataPage(tableInfo, dataPageId);
            for (Record record : records) {
                if (checkConditions(record, tableInfo)) {
                    filteredRecords.add(record);
                }
            }
        }

        return filteredRecords;
    }

    private boolean checkConditions(Record record, TableInfo tableInfo) {
        if (conditions == null || conditions.isEmpty()) {
            return true; // No conditions specified, include all records
        }

        // Split conditions based on AND
        for (SelectCondition condition : conditions) {
            // Evaluate the condition for the current record
            if (!evaluateCondition(record, condition, tableInfo)) {
                return false; // Record doesn't satisfy at least one condition
            }
        }

        return true; // Record satisfies all conditions
    }

    private boolean evaluateCondition(Record record, SelectCondition condition, TableInfo tableInfo) {
        int columnIndex = condition.getColumnIndex();
        String operator = condition.getOperator();
        String value = condition.getValue();

        // Retrieve the actual value from the record based on the column index
        String recordValue = record.getRecValues()[columnIndex];

        // Implement logic to evaluate the condition
        switch (operator) {
            case "=":
                return recordValue.equals(value);
            case "<":
                // Implement other comparison operators as needed
                // ...
            default:
                // Handle unsupported operator
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }


    private void processRecords(List<Record> records) {
        // Implement logic to process or display the records
        for (Record record : records) {
            System.out.println(record.toString() + ".");
        }
    }
}
