package main;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordIterator {
    private PageId pageIdx;
    private TableInfo tabinfo;
    private ByteBuffer dataPageBuffer;
    private int slotIdx;
    private Record currentRecord;

    public RecordIterator(TableInfo tabinfo, PageId pageIdx) throws IOException {
        this.tabinfo = tabinfo;
        this.pageIdx = pageIdx;
        this.slotIdx = 0;
        this.currentRecord = null;
        this.dataPageBuffer = BufferManager.getInstance().getPage(pageIdx);
    }

    public Record GetNextRecord() {

    }

    public void Close() {
        BufferManager.getInstance().releasePage(pageIdx, false);
    }

    public void Reset() {
        this.slotIdx = 0;
        this.currentRecord = null;
    }
}
