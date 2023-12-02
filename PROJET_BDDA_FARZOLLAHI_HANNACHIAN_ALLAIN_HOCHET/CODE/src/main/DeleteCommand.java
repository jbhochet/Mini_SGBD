import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteCommand implements ICommand {

    public static final Pattern PATTERN = Pattern.compile("^DELETE \\* FROM (\\w+)( WHERE (.+))?$");
    private String tableName;
    private List<Condition> conditions;

    public DeleteCommand(String command) {
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
        TableInfo tableInfo = databaseInfo.getTableInfo(tableName);
        FileManager fileManager = FileManager.getInstance();

        RecordIterator recordIterator;
        Record record;
        int nb = 0;

        for(PageId page: fileManager.getDataPages(tableInfo)) {
            recordIterator = new RecordIterator(tableInfo, page);
            while((record = recordIterator.getNextRecord()) != null) {
                if(conditions == null || ConditionUtil.checkAll(record, conditions)) {
                    //TODO : methode pour supprimer le record (hard delete) surement via fileManager
                    nb++;
                }
            }
            recordIterator.close();
        }
    }
}
