package main;
import java.util.List;

public class TableInfo {
    private String tableName;
    private int numberOfColumns;
    private List<ColInfo> columns;

    public TableInfo(String tableName, List<ColInfo> columns) {
        this.tableName = tableName;
        this.numberOfColumns = columns.size();
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public List<ColInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColInfo> columns) {
        this.columns = columns;
    }
}