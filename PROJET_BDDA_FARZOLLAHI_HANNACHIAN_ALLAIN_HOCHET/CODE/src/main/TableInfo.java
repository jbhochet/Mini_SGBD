import java.io.Serializable;

public class TableInfo implements Serializable {
    private String name;
    private ColInfo[] columns;
    private PageId headerPageId;
    private boolean variable;

    public TableInfo(String name, ColInfo[] columns, PageId headerPageId) {
        this.name = name;
        this.columns = columns;
        this.headerPageId = headerPageId;
        // check if this table contains varstring
        this.variable = false;
        for (ColInfo colInfo : columns) {
            if (colInfo.getType() == DataType.VARSTRING) {
                this.variable = true;
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getNumberOfColumns() {
        return columns.length;
    }

    public ColInfo[] getColumns() {
        return columns;
    }

    public PageId getHeaderPageId() {
        return headerPageId;
    }
	
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getName().equals(columnName)) {
                return i;
            }
        }
        // Handle the case where the column name is not found
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

    public boolean isVariable() {
        return variable;
    }
}
