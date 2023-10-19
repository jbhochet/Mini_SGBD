package main;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordIterator {
    private PageId pageIdx;
    private TableInfo tabinfo;
    private ByteBuffer dataPageBuffer;
    private int slotIdx;
    private Record currentRecord; //POURQUOI ?

    public RecordIterator(TableInfo tabinfo, PageId pageIdx) throws IOException {
        this.tabinfo = tabinfo;
        this.pageIdx = pageIdx;
        this.slotIdx = 0;
        this.currentRecord = null;
        this.dataPageBuffer = BufferManager.getInstance().getPage(pageIdx);
    }

     public Record GetNextRecord() {
        int slotsCount = getSlotsCount();

        if (slotIdx >= slotsCount) {
            return null; // No more records on the page
        }

        int startPosition = getStartPosition(slotIdx);
        Record record = new Record(tabinfo);
        int bytesRead = record.readFromBuffer(dataPageBuffer, startPosition);

        slotIdx++;

        if (bytesRead == 0) {
            return GetNextRecord(); // Skip deleted records
        }

        return record;
    }

    private int getSlotsCount() {
        return dataPageBuffer.getInt(0);
    }

    private int getStartPosition(int slotIdx) {
        // Each slot is 8 bytes (start position + record size)
        int offset = 4 + slotIdx * 8;
        return dataPageBuffer.getInt(offset);
    }

    public void Close() throws IOException {
        BufferManager.getInstance().freePage(pageIdx, false);
    }

    public void Reset() {
        this.slotIdx = 0;
        this.currentRecord = null;
    }
}

/*
public Record GetNextRecord(){
    if (dataPageBuffer.remaining() <= 0) {
        return null; // No more records on the page
    }

    int startPosition = dataPageBuffer.position();
    Record record = new Record(tabinfo);
    int bytesRead = record.readFromBuffer(dataPageBuffer, startPosition);

    if (bytesRead > 0) {
        // Move to the next slot index
        slotIdx++;
        return record;
    } else {
        return null; // Unable to read a complete record
    }
}
*/
