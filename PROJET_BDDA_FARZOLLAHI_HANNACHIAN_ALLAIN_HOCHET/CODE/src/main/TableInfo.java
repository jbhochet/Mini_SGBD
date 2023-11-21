import java.io.Serializable;

public class TableInfo implements Serializable {
    private String name;
    private ColInfo[] columns;
    private PageId headerPageId;

    public TableInfo(String name, ColInfo[] columns, PageId headerPageId) {
        this.name = name;
        this.columns = columns;
        this.headerPageId = headerPageId;
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

}