import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance;

    public FileManager() {};

    public static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    public PageId createNewHeaderPage() throws IOException {
        PageId headerPageId = DiskManager.getInstance().AllocPage();
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer byteBuffer = bufferManager.getPage(headerPageId);
        // Fake page 1
        byteBuffer.putInt(0, -1);
        byteBuffer.putInt(4, 0);
        // Fake page 2
        byteBuffer.putInt(8, -1);
        byteBuffer.putInt(12, 0);
        // Write data
        bufferManager.freePage(headerPageId, true);
        return headerPageId;
    }

    public PageId addDataPage(TableInfo tabInfo) throws IOException {
        PageId dataPage = DiskManager.getInstance().AllocPage();
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer dataPageBuffer = bufferManager.getPage(dataPage);
        // add fake pageId
        dataPageBuffer.putInt(0, -1);
        dataPageBuffer.putInt(1, 0);
        // add slot directory
        int index = DBParams.SGBDPageSize-4;
        dataPageBuffer.putInt(index, 8*1024); // free space position
        index-=4;
        dataPageBuffer.putInt(index, 0); // Nb case in directory
        // Add this page in the list
        PageId currentPage = tabInfo.getHeaderPageId();
        PageId tempPage;
        boolean foundLast = false;
        do {
            ByteBuffer buffer = bufferManager.getPage(currentPage);
            tempPage = currentPage;
            currentPage = new PageId(buffer.getInt(), buffer.getInt());
            if(currentPage.getFileIdx() == -1) {
                foundLast = true;
                buffer.putInt(0, dataPage.getFileIdx());
                buffer.putInt(1, dataPage.getPageIdx());
                bufferManager.freePage(tempPage, true);
            } else {
                bufferManager.freePage(tempPage, false);
            }
        } while(!foundLast);

        bufferManager.freePage(dataPage, true);
        
        return dataPage;
    }//CREATE TABLE R (C1:INT,C2:VARSTRING(3),C3:INT) INSERT INTO R VALUES (1,aab,2)

    private int getFreeSpace(ByteBuffer pageBuffer) {
        pageBuffer.position(DBParams.SGBDPageSize-(2*4*1024));
        int m = pageBuffer.getInt();
        int posFreeSpace = pageBuffer.getInt();
        return DBParams.SGBDPageSize - posFreeSpace - (8*1024)*(m+1);
    }

    public PageId getFreeDataPageId(TableInfo tabInfo, int sizeRecord) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        PageId res = null;
        PageId currentPage = tabInfo.getHeaderPageId();
        PageId tempPage;
        boolean isFirst = true;
        int freeSize = 0;
        boolean foundLast = false;
        do {
            ByteBuffer buffer = bufferManager.getPage(currentPage);
            tempPage = currentPage;
            currentPage = new PageId(buffer.getInt(), buffer.getInt());
            if(!isFirst) {
                freeSize = getFreeSpace(buffer);
                if(freeSize >= sizeRecord)
                    res = tempPage;
            }
            if(currentPage.getFileIdx() == -1) {
                foundLast = true;
            }
            bufferManager.freePage(tempPage, false);

        } while(!foundLast);
        
        return res;
    }

    public RecordId writeRecordToDataPage(Record record, PageId pageId) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer dataPageBuffer = bufferManager.getPage(pageId);
        int startPosition = dataPageBuffer.position();
        int bytesWritten = record.writeToBuffer(dataPageBuffer, startPosition);
        bufferManager.freePage(pageId, true);

        return new RecordId(pageId, startPosition / bytesWritten);
    }

    public List<Record> getRecordsInDataPage(TableInfo tabInfo, PageId pageId) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer dataPageBuffer = bufferManager.getPage(pageId);
        List<Record> records = new ArrayList<>();
        int originalPosition = dataPageBuffer.position();
        while (dataPageBuffer.hasRemaining()) {
            int startPosition = dataPageBuffer.position();
            Record record = new Record(tabInfo);
            int bytesRead = record.readFromBuffer(dataPageBuffer, startPosition);
            records.add(record);
        }
        dataPageBuffer.position(originalPosition);
        bufferManager.freePage(pageId, false);
        return records;
    }

    public List<PageId> getDataPages(TableInfo tabInfo) throws IOException {
        BufferManager bufferManager = BufferManager.getInstance();
        List<PageId> listDataPage = new ArrayList<>();

        ByteBuffer headerPageBuffer = bufferManager.getPage(tabInfo.getHeaderPageId());

        try {
            // Read empty pages
            int fileIdx = headerPageBuffer.getInt(0);
            int pageIdx = headerPageBuffer.getInt(4);
            PageId dataPageId = new PageId(fileIdx, pageIdx);
            listDataPage.add(dataPageId);
            bufferManager.freePage(dataPageId, false);

            while (pageIdx != 0 && pageIdx != -1) {
                ByteBuffer pageBuffer = bufferManager.getPage(dataPageId);
                fileIdx = pageBuffer.getInt();
                pageIdx = pageBuffer.getInt();
                dataPageId = new PageId(fileIdx, pageIdx);
                listDataPage.add(dataPageId);
                bufferManager.freePage(dataPageId, false);
            }

            // Read filled pages
            fileIdx = headerPageBuffer.getInt(8);
            pageIdx = headerPageBuffer.getInt(12);
            dataPageId = new PageId(fileIdx, pageIdx);
            listDataPage.add(dataPageId);
            bufferManager.freePage(dataPageId, false);

            while (pageIdx != 0 && pageIdx != -1) {
                ByteBuffer pageBuffer = bufferManager.getPage(dataPageId);
                fileIdx = pageBuffer.getInt();
                pageIdx = pageBuffer.getInt();
                dataPageId = new PageId(fileIdx, pageIdx);
                listDataPage.add(dataPageId);
                bufferManager.freePage(dataPageId, false);
            }
        } finally {
            bufferManager.freePage(tabInfo.getHeaderPageId(), false);
        }

        return listDataPage;
    }


    public RecordId InsertRecordIntoTable(Record record) throws IOException {
        // Step 1: Get the TableInfo for the table associated with the record
        TableInfo tabInfo = record.getTabInfo();

        // Step 2: Find a page with enough space for the new record
        PageId dataPageId = getFreeDataPageId(tabInfo, record.getSize());

        if (dataPageId == null) {
            // Handle the case where there is no page with enough space
            // You might need to create a new data page
            dataPageId = addDataPage(tabInfo);
        }

        // Step 3: Write the record to the identified data page
        RecordId rid = writeRecordToDataPage(record, dataPageId);

        // Step 4: Update any necessary metadata, e.g., in the header page
        BufferManager bufferManager = BufferManager.getInstance();
        ByteBuffer headerPageBuffer = bufferManager.getPage(tabInfo.getHeaderPageId());
        int dataPageCount = headerPageBuffer.getInt();
        headerPageBuffer.putInt(dataPageCount + 1); // Increment the data page count

        // Append the new data page information to the header page
        headerPageBuffer.putInt(dataPageId.getFileIdx());
        headerPageBuffer.putInt(dataPageId.getPageIdx());

        bufferManager.freePage(tabInfo.getHeaderPageId(), true);

        // Step 5: Return the RecordId of the inserted record
        return rid;
    }

}
