import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTableCommand {
    private String tableName;
    private int numColumns;
    private List<String> columnNames;
    private List<String> columnTypes;

    public CreateTableCommand(String commandString) {
        //parseCommand(command);
    }

    public void parseCommand(String command) {
        String[] tokens = command.split("\\s+"); // Séparation par espaces
        this.tableName = tokens[2];
        // Parsing des colonnes
        String[] columns = command.split("\\(|\\)")[1].split(",\\s*");
        this.numColumns = columns.length;
        this.columnNames = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
        for (String col : columns) {
            String[] colTokens = col.split("\\s+");
            this.columnNames.add(colTokens[0]);
            this.columnTypes.add(colTokens[1]);
        }
    }

    public void execute() throws Exception {
        try {
            FileManager fileManager = FileManager.getInstance();
            PageId pageId = fileManager.createNewHeaderPage();
            /*
            TableInfo tableInfo = new TableInfo(tableName, columns, pageId);
            DatabaseInfo.getInstance().addTableInfo(tableInfo);
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la table : " + e.getMessage());
        }
    }
}