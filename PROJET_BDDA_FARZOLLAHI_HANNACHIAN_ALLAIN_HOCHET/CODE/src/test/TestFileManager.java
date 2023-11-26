import java.io.IOException;
import java.nio.file.Files;

public class TestFileManager {
    public static void main(String[] args) {
        DiskManager diskManager;
        BufferManager bufferManager;
        FileManager fileManager;

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

        fileManager = FileManager.getInstance();

        /*
        try {
            

        } catch(IOException err) {
            System.err.println(err.getMessage());
            err.printStackTrace();
        }
        */
    }
}
