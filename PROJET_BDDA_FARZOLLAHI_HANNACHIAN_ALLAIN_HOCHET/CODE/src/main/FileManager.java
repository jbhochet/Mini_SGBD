import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance;

    private FileManager() {
    }

    public static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    // TODO: make this method private
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

    private PageId addDataPage(TableInfo tabInfo) throws IOException {
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
            System.out.println(current);
        } while (current.getFileIdx() != -1);
        System.out.println(current);

        System.out.printf("%s ---> %s%n", temp, newDataPage);
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

    private PageId getFreeDataPageId(TableInfo tabInfo, int sizeRecord) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        PageId current = tabInfo.getHeaderPageId();
        PageId temp;
        ByteBuffer buffer;
        PageId res = null;
        System.out.println(current);

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
            System.out.println(current);
        } while (current.getFileIdx() != -1);

        return res;
    }

    private RecordId writeRecordToDataPage(Record record, PageId pageId) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer buffer;

        buffer = bufferManager.getPage(pageId);

        // get free space position
        int startPosition = buffer.getInt(DBParams.SGBDPageSize - 4);
        System.out.printf("Start pos of %s = %s%n", pageId, startPosition);

        // write record at the free space position
        System.out.printf("Writed at %d%n", startPosition);
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

    // TODO: make this method private
    public List<Record> getRecordsInDataPage(TableInfo tabInfo, PageId pageId) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer buffer = bufferManager.getPage(pageId);
        Record record;
        List<Record> records = new ArrayList<>();
        int m = buffer.getInt(DBParams.SGBDPageSize - 8);
        buffer.position(8); // after the next page id

        for (int i = 0; i < m; i++) {
            record = new Record(tabInfo);
            record.readFromBuffer(buffer, buffer.position());
            records.add(record);
        }

        return records;
    }

    // TODO: make this method private
    public List<PageId> getDataPages(TableInfo tabInfo) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        List<PageId> listDataPage = new ArrayList<>();
        ByteBuffer buffer;
        PageId pageId;
        PageId tempPageId;

        // Read empty pages
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

    public RecordId InsertRecordIntoTable(Record record) throws IOException {
        // Step 1: Get the TableInfo for the table associated with the record
        TableInfo tabInfo = record.getTabInfo();

        // Step 2: Find a page with enough space for the new record
        PageId dataPageId = getFreeDataPageId(tabInfo, record.getSize());
        if (dataPageId == null) {
            dataPageId = addDataPage(tabInfo);
        }
        System.out.println(dataPageId);

        if (dataPageId == null) {
            // Handle the case where there is no page with enough space
            // You might need to create a new data page
            dataPageId = addDataPage(tabInfo);
        }

        // Step 3: Write the record to the identified data page
        RecordId rid = writeRecordToDataPage(record, dataPageId);

        return rid;
    }


    /*public void DeleteRecordFromTable(Record record) throws IOException {
        // Step 1: Get the TableInfo for the table associated with the record
        TableInfo tabInfo = record.getTabInfo();

        // Step 3: Write the record to the identified data page
        RecordId rid = writeRecordToDataPage(record, );

    }*/
}
