import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^SELECT \\* FROM (\\w+)( WHERE (.+))?$");

    private String tableName;
    private List<SelectCondition> conditions;
    private TableInfo tableInfo;

    public SelectCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid command");
        tableName = matcher.group(1);
        tableInfo = DatabaseInfo.getInstance().getTableInfo(tableName);
        conditions = parseConditions(matcher.group(3));
    }

    private List<SelectCondition> parseConditions(String conditionsStr) {
        if (conditionsStr == null || conditionsStr.trim().isEmpty()) {
            return null; // Return null if no conditions are specified
        }

        List<SelectCondition> conditionList = new ArrayList<>();
        String[] conditionStrings = conditionsStr.split(" AND ");
        for (String conditionString : conditionStrings) {
            String[] parts = conditionString.split("(=|<|>|<=|>=|<>)");
            String operator = conditionString.replaceAll("[^=<>]+", "");
            if (parts.length == 2) {
                int columnIndex = tableInfo.getColumnIndex(parts[0].trim());
                String value = parts[1].trim();
                conditionList.add(new SelectCondition(columnIndex, operator, value));
            }
        }

        return conditionList;
    }

     @Override
    public void execute() throws IOException {
        List<Record> records;
        tableInfo = DatabaseInfo.getInstance().getTableInfo(tableName);

     //   PageId headerPage = tableInfo.getHeaderPageId();
        if (conditions == null || conditions.isEmpty()) {
            // No conditions specified, select all records
            records = Record.getAllRecords(tableInfo);
        } else {
            // Execute SELECT with conditions
            records = Record.getFilteredRecords(tableInfo, conditions);
        }

        // Display records
        displayRecords(records);
    }

    private void displayRecords(List<Record> records) {
        for (Record record : records) {
            String[] values = record.getRecValues();
            for (int i = 0; i < values.length; i++) {
                System.out.print(values[i]);
                if (i < values.length - 1) {
                    System.out.print("; ");
                }
            }
            System.out.println(".");
        }

        System.out.println("Total records=" + records.size());
    }
}
