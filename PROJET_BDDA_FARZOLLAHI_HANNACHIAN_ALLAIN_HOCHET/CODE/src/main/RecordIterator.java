import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordIterator {
    private PageId pageId;
    private TableInfo tabinfo;
    private ByteBuffer buffer;
    private int cursor;
    private int nbRecord;

    public RecordIterator(TableInfo tabinfo, PageId pageId) throws IOException {
        this.tabinfo = tabinfo;
        this.pageId = pageId;
        this.buffer = BufferManager.getInstance().getPage(pageId);
        nbRecord = buffer.getInt(DBParams.SGBDPageSize - 8);
        reset();
    }

    // Browse the record of one page
    public Record getNextRecord() {
        if (cursor == nbRecord) {
            return null;
        }
        Record record = new Record(tabinfo);
        record.readFromBuffer(buffer, buffer.position());
        cursor++;
        return record;
    }

    public void close() throws IOException {
        BufferManager.getInstance().freePage(pageId, false);
    }

    public void reset() {
        cursor = 0;
        buffer.position(8); // after the page id
    }
}
