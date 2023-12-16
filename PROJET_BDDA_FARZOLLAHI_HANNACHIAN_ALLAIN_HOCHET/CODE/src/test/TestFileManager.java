import java.io.IOException;
import java.nio.file.Files;

public class TestFileManager {
    public static void main(String[] args) throws IOException {
        DiskManager diskManager;
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

        // Create a table info and a record

        ColInfo[] columns = {
                new ColInfo("Column1", DataType.INT),
                new ColInfo("Column2", DataType.STRING),
        };

        // Create header page
        PageId headerPageId = fileManager.createNewHeaderPage();
        System.out.println("Header page created: " + headerPageId);
        assert headerPageId != null : "Header page not created";

        TableInfo tableInfo = new TableInfo("TEST", columns, headerPageId);

        DatabaseInfo.getInstance().addTableInfo(tableInfo);

        Record record = new Record(tableInfo);

        record.setRecValues(new String[] { "10", "test" });

        RecordId rid = fileManager.InsertRecordIntoTable(record);

        System.out.println(rid.toString());
    }
}
