package main;

public class FileManager {
    private static final FileManager instance=new FileManager();

    public FileManager(){};

    public PageId createNewHeaderPage(){
        return ;//A faire
    }

    public PageId addDataPage(TableInfo tabInfo){
        return ;//A faire
    }

    public PageId getFreeDataPageId(TableInfo tabinfo,int sizeRecord){
        return ;//A faire
    }

    public RecordId writeRecordToDataPage(Record record,PageId pageId){
        return ;//A faire
    }

    public Record getRecordsInDataPage(TableInfo tabinfo,PageId pageId){
        return ;//A faire
    }

    public PageId getDataPages(TableInfo tabinfo){
        return ;//A faire
    }

    public RecordId InsertRecordIntoTable(Record record){
        return ;//A faire
    }

    public Record getAllRecords(TableInfo tabinfo){
        return ;//A faire
    }
}
