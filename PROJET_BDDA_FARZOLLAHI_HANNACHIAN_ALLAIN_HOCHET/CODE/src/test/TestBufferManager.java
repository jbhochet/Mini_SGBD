import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class TestBufferManager {
    public static void main(String[] args) throws IOException {
        DiskManager diskManager;
        BufferManager bufferManager;
        PageId page1, page2, page3;
        ByteBuffer buffer;

        /*
         * Init DB
         */

        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4;
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

        /*
         * Test BufferManager
         */

        try {
            diskManager.init();
        } catch (IOException err) {
            System.err.println("Can't initialise db!");
            System.err.println(err);
            System.exit(1);
        }

        bufferManager = BufferManager.getInstance();

        // Alloc 3 pages
        page1 = diskManager.AllocPage();
        page2 = diskManager.AllocPage();
        page3 = diskManager.AllocPage();

        System.out.printf("Page 1 = %s%n", page1);
        System.out.printf("Page 2 = %s%n", page2);
        System.out.printf("Page 3 = %s%n", page3);

        // Test get and free

        System.out.println("Write data in page 1.");
        System.out.println(bufferManager);
        buffer = bufferManager.getPage(page1);
        System.out.println(bufferManager);
        buffer.putInt(0, 10);
        bufferManager.freePage(page1, true);
        System.out.println(bufferManager);

        System.out.println("Write data in page 2.");
        buffer = bufferManager.getPage(page2);
        buffer.putInt(0, 20);
        bufferManager.freePage(page2, true);

        System.out.println("Read data in page 1.");
        System.out.println(bufferManager);
        buffer = bufferManager.getPage(page1);
        System.out.println(bufferManager);
        System.out.println(buffer.getInt(0));

        System.out.println("Read data in page 2.");
        buffer = bufferManager.getPage(page2);
        System.out.println(buffer.getInt(0));

        System.out.println("Free page 1 and 2.");
        System.out.println(bufferManager);
        bufferManager.freePage(page1, false);
        bufferManager.freePage(page2, false);
        System.out.println(bufferManager);

        // Test lfu

        System.out.println("Get and free page 1.");
        System.out.println(bufferManager);
        bufferManager.getPage(page1);
        bufferManager.freePage(page1, false);
        System.out.println(bufferManager);

        System.out.println("Get and free page 3.");
        System.out.println(bufferManager);
        bufferManager.getPage(page3);
        bufferManager.freePage(page3, false);
        System.out.println(bufferManager);

        bufferManager.flushAll();
    }
}
