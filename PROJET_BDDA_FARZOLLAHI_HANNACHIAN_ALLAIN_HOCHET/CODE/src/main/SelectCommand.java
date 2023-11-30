import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^SELECT \\* FROM (\\w+)( WHERE (.+))?$");

    private String tableName;
    private List<Condition> conditions;

    public SelectCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid command");
        tableName = matcher.group(1);
        conditions = null;
        if (matcher.group(3) != null) {
            conditions = ConditionUtil.parseConditions(matcher.group(3));
        }
    }

    @Override
    public void execute() throws IOException {
        List<Record> records;
        TableInfo tableInfo = DatabaseInfo.getInstance().getTableInfo(tableName);

        if (conditions == null) {
            // No conditions specified, select all records
            records = Record.getAllRecords(tableInfo);
        } else {
            records = new ArrayList<>();
            for (Record record : Record.getAllRecords(tableInfo)) {
                if (ConditionUtil.checkAll(record, conditions))
                    records.add(record);
            }
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
