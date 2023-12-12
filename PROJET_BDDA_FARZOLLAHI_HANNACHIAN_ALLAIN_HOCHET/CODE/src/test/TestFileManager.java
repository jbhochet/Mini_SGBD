import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class TestFileManager {
    public static void main(String[] args) throws IOException {
        DiskManager diskManager;
        BufferManager bufferManager = BufferManager.getInstance();
        FileManager fileManager;

        // Initialize DiskManager
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        DBParams.FrameCount = 2;

        // Create a temporary DB
        try {
            DBParams.DBPath = Files.createTempDirectory("DB_").toFile().getAbsolutePath();
        } catch (IOException err) {
            System.err.println("Can't create temp folder!");
            System.err.println(err);
            System.exit(1);
        }

        diskManager = DiskManager.getInstance();

        // Initialize the DB
        try {
            diskManager.init();
        } catch (IOException err) {
            System.err.println("Can't initialize db!");
            System.err.println(err);
            System.exit(1);
        }

        // Initialize FileManager
        fileManager = FileManager.getInstance();

        // Create a sample TableInfo for testing
        ColInfo[] columns = {
                new ColInfo("Column1", DataType.INT),
                new ColInfo("Column2", DataType.STRING),
                // ... add other columns as needed
        };

        PageId headerPageId = new PageId(1, 0);

        TableInfo tableInfo = new TableInfo("TestTable", columns, headerPageId);

        List<PageId> dataPages = null; // Declare dataPages outside the try block

        try {
            // Test createNewHeaderPage
            headerPageId = fileManager.createNewHeaderPage();
            System.out.println("Header page created: " + headerPageId);
            assert headerPageId != null : "Header page not created";

            // Test addDataPage
            PageId dataPageId = fileManager.addDataPage(tableInfo);
            System.out.println("Data page created: " + dataPageId);
            assert dataPageId != null : "Data page not created";

            // Test getFreeDataPageId
            PageId freeDataPageId = fileManager.getFreeDataPageId(tableInfo, 100); // Assuming 100 bytes record size
            System.out.println("Free data page retrieved: " + freeDataPageId);
            assert freeDataPageId != null : "No free data page found";

            // Test writeRecordToDataPage
            Record record = new Record(tableInfo);
            fileManager.writeRecordToDataPage(record, dataPageId);
            System.out.println("Record written to data page: " + record);

            // Read the content of the data page after writing the record
            ByteBuffer dataPageContent = bufferManager.getPage(dataPageId);
            if (dataPageContent != null) {
                System.out.println("Data page content after writing record: " + dataPageContent);
            } else {
                System.out.println("Error: Unable to retrieve data page content.");
            }

            // Test getRecordsInDataPage
            List<Record> recordsInDataPage = fileManager.getRecordsInDataPage(tableInfo, dataPageId);
            System.out.println("Records in data page: " + recordsInDataPage);
            assert recordsInDataPage != null : "Unable to retrieve records from data page";

            // Test getDataPages
            dataPages = fileManager.getDataPages(tableInfo);
            System.out.println("Data pages retrieved: " + dataPages);
            assert dataPages != null : "Unable to retrieve data pages from header page";

            bufferManager = BufferManager.getInstance();
            for (PageId dataPage : dataPages) {
                ByteBuffer dataPageBuffer = bufferManager.getPage(dataPage);

                // Example of reading from the ByteBuffer
                byte[] data = new byte[dataPageBuffer.remaining()];
                dataPageBuffer.get(data);
                String content = new String(data, StandardCharsets.UTF_8);
                System.out.println("Data page content: " + content);

                // Example of writing to the ByteBuffer
                String newData = "New data to write";
                byte[] newDataBytes = newData.getBytes(StandardCharsets.UTF_8);
                dataPageBuffer.clear(); // Clears the buffer
                dataPageBuffer.put(newDataBytes);

                bufferManager.freePage(dataPage, true); // Indicates that the page is modified
            }

            for (PageId dataPage : dataPages) {
                bufferManager.freePage(dataPage, false);
            }

            // Test InsertRecordIntoTable
            Record newRecord = new Record(tableInfo);
            RecordId insertedRecordId = fileManager.InsertRecordIntoTable(newRecord);
            System.out.println("Record inserted into the table with ID: " + insertedRecordId);
            assert insertedRecordId != null : "Record not inserted into the table";

            // Additional tests for getRecordsInDataPage, getDataPages, and InsertRecordIntoTable

            System.out.println("All tests passed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Some tests failed!");
        } finally {
            assert dataPages != null;
            for (PageId dataPage : dataPages) {
                bufferManager.freePage(dataPage, false);
            }
        }
    }
}
