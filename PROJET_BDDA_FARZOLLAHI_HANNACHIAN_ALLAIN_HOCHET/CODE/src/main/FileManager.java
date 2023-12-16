import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance;

    private FileManager() {
        // Empty constructor
    }

    public static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    // Allocates a page in which two pageids will be written, corresponding to lists of data pages
    public PageId createNewHeaderPage() throws IOException {
        PageId headerPageId = DiskManager.getInstance().AllocPage();
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer byteBuffer = bufferManager.getPage(headerPageId);
        // Fake page 1
        byteBuffer.position(0);
        byteBuffer.putInt(-1);
        byteBuffer.putInt(-1);
        // Fake page 2
        byteBuffer.putInt(-1);
        byteBuffer.putInt(-1);
        // Write data
        bufferManager.freePage(headerPageId, true);
        return headerPageId;
    }

    // Add an empty data page to the heap file related to the relation given by the tabInfo and return it's PageId 
    public PageId addDataPage(TableInfo tabInfo) throws IOException {
        PageId newDataPage = DiskManager.getInstance().AllocPage();
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer buffer = bufferManager.getPage(newDataPage);
        // add fake pageId
        buffer.position(0);
        buffer.putInt(-1);
        buffer.putInt(-1);
        // add slot directory and free space
        buffer.position(DBParams.SGBDPageSize - 8);
        buffer.putInt(0); // Nb case in directory
        buffer.putInt(8); // free space position
        // free this data page
        bufferManager.freePage(newDataPage, true);
        // Add this page in the list
        PageId current = tabInfo.getHeaderPageId();
        PageId temp;
        do {
            buffer = bufferManager.getPage(current);
            temp = current;
            current = new PageId(buffer.getInt(0), buffer.getInt(4));
            bufferManager.freePage(temp, false);
        } while (current.getFileIdx() != -1);
        buffer = bufferManager.getPage(temp);
        buffer.position(0);
        buffer.putInt(newDataPage.getFileIdx());
        buffer.putInt(newDataPage.getPageIdx());
        bufferManager.freePage(temp, true);
        return newDataPage;
    }

    private int getFreeSpace(ByteBuffer pageBuffer) {
        pageBuffer.position(DBParams.SGBDPageSize - 8);
        int m = pageBuffer.getInt();
        int posFreeSpace = pageBuffer.getInt();
        return DBParams.SGBDPageSize - posFreeSpace - 4 - 4 - m * 8 - 8; // -8 for the future slot dir entry
    }

    // Return the pageId of a page which have enough place to have the relation given by tabInfo
    private PageId getFreeDataPageId(TableInfo tabInfo, int sizeRecord) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        PageId current = tabInfo.getHeaderPageId();
        PageId temp;
        ByteBuffer buffer;
        PageId res = null;
        do {
            buffer = bufferManager.getPage(current);
            temp = current;
            if (!current.equals(tabInfo.getHeaderPageId()) && getFreeSpace(buffer) >= sizeRecord) {
                res = current;
                current = new PageId(-1, 0); // exit while
            } else {
                current = new PageId(buffer.getInt(0), buffer.getInt(4));
            }
            bufferManager.freePage(temp, false);
        } while (current.getFileIdx() != -1);
        return res;
    }

    // Write a record on a page and return it's recordId
    private RecordId writeRecordToDataPage(Record record, PageId pageId) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer buffer;
        buffer = bufferManager.getPage(pageId);
        // get free space position
        int startPosition = buffer.getInt(DBParams.SGBDPageSize - 4);
        // write record at the free space position
        int bytesWritten = record.writeToBuffer(buffer, startPosition);
        // update slot directory
        int m = buffer.getInt(DBParams.SGBDPageSize - 8);
        buffer.position(DBParams.SGBDPageSize - 4 - 4 - (m + 1) * 8);
        buffer.putInt(startPosition);
        buffer.putInt(bytesWritten);
        buffer.putInt(DBParams.SGBDPageSize - 8, m + 1);
        // update free space position
        buffer.putInt(DBParams.SGBDPageSize - 4, startPosition + bytesWritten);
        bufferManager.freePage(pageId, true);
        return new RecordId(pageId, m);
    }

    // Return the list of pageId data pages like they are in header page
    public List<PageId> getDataPages(TableInfo tabInfo) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        List<PageId> listDataPage = new ArrayList<>();
        ByteBuffer buffer;
        PageId pageId;
        PageId tempPageId;
        // Read non-filled pages
        buffer = bufferManager.getPage(tabInfo.getHeaderPageId());
        buffer.position(0);
        pageId = new PageId(buffer.getInt(), buffer.getInt());
        bufferManager.freePage(tabInfo.getHeaderPageId(), false);
        while (pageId.getFileIdx() != -1) {
            listDataPage.add(pageId);
            buffer = bufferManager.getPage(pageId);
            buffer.position(0);
            tempPageId = pageId; // backup to free
            pageId = new PageId(buffer.getInt(), buffer.getInt());
            bufferManager.freePage(tempPageId, false);
        }
        // Read filled pages
        buffer = bufferManager.getPage(tabInfo.getHeaderPageId());
        buffer.position(8);
        pageId = new PageId(buffer.getInt(), buffer.getInt());
        bufferManager.freePage(tabInfo.getHeaderPageId(), false);
        while (pageId.getFileIdx() != -1) {
            listDataPage.add(pageId);
            buffer = bufferManager.getPage(pageId);
            buffer.position(0);
            tempPageId = pageId; // backup to free
            pageId = new PageId(buffer.getInt(), buffer.getInt());
            bufferManager.freePage(tempPageId, false);
        }
        return listDataPage;
    }

    // Insertion of a record in a relation
    public RecordId InsertRecordIntoTable(Record record) throws IOException {
        // Get the TableInfo for the table associated with the record
        TableInfo tabInfo = record.getTabInfo();

        // Find a page with enough space for the new record
        PageId dataPageId = getFreeDataPageId(tabInfo, record.getSize());
        if (dataPageId == null) {
            dataPageId = addDataPage(tabInfo);
        }

        // Write the record to the identified data page
        return writeRecordToDataPage(record, dataPageId);
    }
}
