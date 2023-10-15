package main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static FileManager instance = null;

    public FileManager() {};

    public static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    public PageId createNewHeaderPage() throws IOException {
        PageId headerPageId = DiskManager.getInstance().AllocPage();
        try {
            byte[] headerPageData = BufferManager.getInstance().getPage(headerPageId).array();
            int offset = 0;
            PageId emptyPageList = new PageId(-1, 0);
            emptyPageList.serialize(headerPageData, offset);
            offset += 8; // Taille d'un PageId
            emptyPageList.serialize(headerPageData, offset);
            BufferManager.getInstance().markPageDirty(headerPageId);
            BufferManager.getInstance().releasePage(headerPageId, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de la création de la Header Page.", e);
        }
        return headerPageId;
    }

    public PageId addDataPage(TableInfo tabInfo) throws IOException {
        PageId newPageId = DiskManager.getInstance().AllocPage();
        PageId firstDataPageId = tabInfo.getFirstDataPageId();
        ByteBuffer firstDataPageBuffer = BufferManager.getInstance().getPage(firstDataPageId);
        PageId lastDataPageId = firstDataPageId;
        while (true) {
            ByteBuffer lastDataPageBuffer = BufferManager.getInstance().getPage(lastDataPageId);
            // Si la page actuelle est pleine, passer à la suivante
            // PAS COMPLET
        }
        // PAS COMPLET
    }

    public PageId getFreeDataPageId(TableInfo tabinfo,int sizeRecord) {
    }

    public RecordId writeRecordToDataPage(Record record,PageId pageId) {
    }

    public List<Record> getRecordsInDataPage(TableInfo tabInfo, PageId pageId) throws IOException {
        ByteBuffer dataPageBuffer = BufferManager.getInstance().getPage(pageId);
        List<Record> records = new ArrayList<>();
        int originalPosition = dataPageBuffer.position();
        while (dataPageBuffer.hasRemaining()) {
            int startPosition = dataPageBuffer.position();
            Record record = new Record(tabInfo);
            int bytesRead = record.readFromBuffer(dataPageBuffer, startPosition);
            records.add(record);
        }
        dataPageBuffer.position(originalPosition);
        BufferManager.getInstance().releasePage(pageId, false);
        return records;
    }

    public List<PageId> getDataPages(TableInfo tabInfo) throws IOException {
        List<PageId> dataPageIds = new ArrayList<>();
        ByteBuffer headerPageBuffer = BufferManager.getInstance().getPage(tabInfo.getHeaderPageId());
        int dataPageCount = headerPageBuffer.getInt();
        for (int i = 0; i < dataPageCount; i++) {
            int fileIdx = headerPageBuffer.getInt();
            int pageIdx = headerPageBuffer.getInt();
            PageId dataPageId = new PageId(fileIdx, pageIdx);
            dataPageIds.add(dataPageId);
        }
        BufferManager.getInstance().releasePage(tabInfo.getHeaderPageId(), false);
        return dataPageIds;
    }

    public RecordId InsertRecordIntoTable(Record record) {
    }

    public List<Record> GetAllRecords(TableInfo tabInfo) throws IOException {
        List<Record> records = new ArrayList<>();
        List<PageId> dataPageIds = getDataPages(tabInfo);
        for (PageId dataPageId : dataPageIds) {
            List<Record> recordsInDataPage = getRecordsInDataPage(tabInfo, dataPageId);
            records.addAll(recordsInDataPage);
            BufferManager.getInstance().releasePage(dataPageId, false);
        }
        return records;
    }
}
