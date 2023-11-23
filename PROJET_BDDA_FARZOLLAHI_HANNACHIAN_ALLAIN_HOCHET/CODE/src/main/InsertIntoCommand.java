import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertIntoCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("INSERT INTO (\\w+) VALUES \\((.+)\\)");

    private String tableName;
    private String[] values;

    public InsertIntoCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid command");
        tableName = matcher.group(1);
        values = matcher.group(2).split(",");
    }

    @Override
    public void execute() throws IOException {
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        TableInfo tableInfo = databaseInfo.getTableInfo(tableName);
        Record record = new Record(tableInfo);
        record.setRecValues(values);
        FileManager fileManager = FileManager.getInstance();
        fileManager.InsertRecordIntoTable(record);
    }

}