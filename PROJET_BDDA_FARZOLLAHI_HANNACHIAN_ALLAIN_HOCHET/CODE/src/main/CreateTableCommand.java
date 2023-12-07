import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTableCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("CREATE TABLE (\\w+) \\((.+)\\)");
    public static final Pattern COLUMN_PATTERN = Pattern.compile("^(\\w+):(\\w+)(\\((\\d+)\\))?$");
    private String tableName;
    // private int numColumns;
    private ColInfo[] columns;

    public CreateTableCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }
        // get the name of the table
        tableName = matcher.group(1);
        // now we must parse the column infos!
        String[] columnInfos = matcher.group(2).split(",");
        columns = new ColInfo[columnInfos.length];
        for (int i = 0; i < columns.length; i += 1) {
            columns[i] = parseColumn(columnInfos[i]);
        }
    }

    public ColInfo parseColumn(String column) {
        Matcher matcher = COLUMN_PATTERN.matcher(column);
        DataType type;
        int size = 0;
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Your command doesn't match the CREATE TABLE syntax!");
        }
        switch (matcher.group(2)) {
            case "STRING":
                type = DataType.STRING;
                size = Integer.parseInt(matcher.group(4));
                break;
            case "VARSTRING":
                type = DataType.VARSTRING;
                size = Integer.parseInt(matcher.group(4));
                break;
            case "INT":
                type = DataType.INT;
                break;
            case "FLOAT":
                type = DataType.FLOAT;
                break;
            default:
                throw new IllegalArgumentException("Unknown type!");
        }
        return new ColInfo(matcher.group(1), type, size);
    }

    public void execute() throws IOException {
        FileManager fileManager = FileManager.getInstance();
        PageId headerPage = fileManager.createNewHeaderPage();
        TableInfo tableInfo = new TableInfo(tableName, columns, headerPage);
        DatabaseInfo.getInstance().addTableInfo(tableInfo);
    }
}
