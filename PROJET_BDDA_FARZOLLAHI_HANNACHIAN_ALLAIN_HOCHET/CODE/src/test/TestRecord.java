import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class TestRecord {
    public static void main(String[] args) {
        DiskManager diskManager;
        BufferManager bufferManager;

        // Init DiskManager

        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        DBParams.FrameCount = 2;

        // Create a temp DB
        try {
            DBParams.DBPath = Files.createTempDirectory("DB_").toFile().getAbsolutePath();
        } catch (IOException err) {
            System.err.println("Can't create temp folder!");
            System.err.println(err);
            System.exit(1);
        }

        diskManager = DiskManager.getInstance();

        // Init the DB
        try {
            diskManager.init();
        } catch (IOException err) {
            System.err.println("Can't initialise db!");
            System.err.println(err);
            System.exit(1);
        }

        bufferManager = BufferManager.getInstance();

        try {
            PageId page = diskManager.AllocPage();

            ColInfo[] columns = new ColInfo[] {
                    new ColInfo("prenom", DataType.STRING, 5),
                    new ColInfo("nom", DataType.STRING, 5),
                    new ColInfo("age", DataType.INT)
            };

            TableInfo tabInfo = new TableInfo("user", columns, null);

            // databaseInfo.addTableInfo(tabInfo);
            ByteBuffer buffer = bufferManager.getPage(page);

            Record record1 = new Record(tabInfo);
            Record record2 = new Record(tabInfo);

            record1.setRecValues(new String[] { "Jb", "PAA", "10" });

            System.out.println(record1);

            int nbBytes1 = record1.writeToBuffer(buffer, 0);

            int nbBytes2 = record2.readFromBuffer(buffer, 0);

            System.out.println(record2);

            System.out.printf("Record 1: %d bytes write%nRecord 2: %d bytes read%n", nbBytes1, nbBytes2);

            bufferManager.freePage(page, true);
        } catch (IOException err) {
            System.err.println(err);
        }

    }
}
