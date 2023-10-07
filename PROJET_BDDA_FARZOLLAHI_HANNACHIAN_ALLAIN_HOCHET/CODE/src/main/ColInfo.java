package main;

class ColInfo {
    private String colName;
    private String colType;

    public ColInfo(String colName, String colType) {
        this.colName = colName;
        this.colType = colType;
    }


    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }
}