import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class TestDiskManager {
    public static void main(String[] args) {
        DiskManager diskManager;
        PageId page;
        ByteBuffer buffer;


        /*
         * Init DB
         */

        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4;

        // Create a temp DB
        try {
            DBParams.DBPath = Files.createTempDirectory("DB_").toFile().getAbsolutePath();
        } catch(IOException err) {
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

        /*
         * Test DiskManager methods
         */

        buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);

        try {
            final int value = 1000;

            System.out.println(diskManager);
            page = diskManager.AllocPage();
            System.out.println(diskManager);

            diskManager.ReadPage(page, buffer);

            System.out.printf("Write %d in page.%n", value);
            buffer.putInt(0, value);
            diskManager.WritePage(page, buffer);

            buffer.clear();

            diskManager.ReadPage(page, buffer);
            System.out.printf("Read %d from page.%n", buffer.getInt(0));

            diskManager.DeallocPage(page);
            System.out.println(diskManager);
        } catch (IOException err) {
            System.err.println("Can't access page!");
            System.err.println(err);
        }
    }
}
