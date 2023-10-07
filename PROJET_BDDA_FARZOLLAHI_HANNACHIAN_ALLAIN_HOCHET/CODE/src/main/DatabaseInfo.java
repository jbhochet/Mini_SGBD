package main;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInfo {
    private static final DatabaseInfo instance = new DatabaseInfo();
    private Map<String, TableInfo> tables;
    private int tableCounter;

    private DatabaseInfo() {
        this.tables = new HashMap<>();
        this.tableCounter = 0;
    }

    public static DatabaseInfo getInstance() {
        return instance;
    }

    public void init() {
    }

    public void finish() {
    }

    public void addTableInfo(TableInfo tableInfo) {
        tables.put(tableInfo.getTableName(), tableInfo);
        tableCounter++;
    }

    public TableInfo getTableInfo(String tableName) {
        return tables.get(tableName);
    }

}
