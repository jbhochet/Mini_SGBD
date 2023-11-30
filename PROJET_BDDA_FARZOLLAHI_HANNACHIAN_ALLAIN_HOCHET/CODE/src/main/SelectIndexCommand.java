import java.util.List;

public class SelectIndexCommand implements ICommand {
    private String relationName;
    private String columnName;
    private String value;

    public SelectIndexCommand(String command) {
        // A completer
        parseCommand(command);
    }

    private void parseCommand(String command) {
        // A completer
    }

    @Override
    public void execute() {
        // A completer
    }

    private void getIndexForColumn(TableInfo tableInfo, String columnName) {
        // A completer
        // Exemple factice :
        // return IndexManager.getInstance().getIndexForColumn(tableInfo, columnName);
    }

    private void displayRecords(List<Record> records, TableInfo tableInfo) {

    }
}
