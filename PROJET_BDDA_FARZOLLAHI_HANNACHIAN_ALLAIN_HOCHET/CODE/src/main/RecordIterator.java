package main;

public class RecordIterator {
    PageId pageId;
    TableInfo tabinfo;

    public RecordIterator(TableInfo tabinfo,PageId pageId){
        this.tabinfo=tabinfo;
        this.pageId=pageId;
    }

    public Record GetNextRecord(){
        return ;//A faire
    }

    public void Close(){

    }

    public void Reset(){
        
    }
}
