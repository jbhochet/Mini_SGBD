import java.io.IOException;
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
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        FileManager fileManager = FileManager.getInstance();
        TableInfo tableInfo = databaseInfo.getTableInfo(tableName);
        RecordIterator recordIterator;
        Record record;
        int nb = 0;

        for(PageId page: fileManager.getDataPages(tableInfo)) {
            recordIterator = new RecordIterator(tableInfo, page);
            while((record = recordIterator.getNextRecord()) != null) {
                if(conditions == null || ConditionUtil.checkAll(record, conditions)) {
                    System.out.println(record);
                    nb++;
                }
            }
            recordIterator.close();
        }

        System.out.println("Total records=" + String.valueOf(nb));
    }
}
