import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DiskManager diskManager;

        DBParams.DBPath = (args.length > 0) ? args[0] : "../DB";
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        DBParams.FrameCount = 2;

        diskManager = DiskManager.getInstance();

        try {
            diskManager.init();
        } catch (IOException e) {
            System.out.println("Can't initialise database!");
            return;
        }

        // Allocation du page
        PageId allocatedPageId;
        try {
            allocatedPageId = diskManager.AllocPage();
        } catch (IOException e) {
            System.out.println("Can't alloc a new page!");
            return;
        }

        System.out.println("Allocated Page: File Index = " + allocatedPageId.getFileIdx() +
                ", Page Index = " + allocatedPageId.getPageIdx());

        // Creation de ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
        buffer.putInt(42); // Ecrire an integer (42) dans le Page
        buffer.flip();

        try { // Ecriture
            diskManager.WritePage(allocatedPageId, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lecture
        ByteBuffer readBuffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
        try {
            diskManager.ReadPage(allocatedPageId, readBuffer);
            int data = readBuffer.getInt(); // Example: Reading an integer from the page
            System.out.println("Read data from Page: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int allocatedPagesCount = diskManager.GetCurrentCountAllocPages();
        System.out.println("Current Count of Allocated Pages: " + allocatedPagesCount);

        // DB Info
        DatabaseInfo dbinfo = DatabaseInfo.getInstance();

        try {
            dbinfo.init();

            

            dbinfo.finish();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        // DatabaseManager
        DatabaseManager databaseManager =  DatabaseManager.getInstance();
        //databaseManager.init();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("Entrez une commande (ou EXIT pour quitter)");
            String command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("EXIT")) {
                //databaseManager.finish();
                exit = true;
            } else 
                databaseManager.processCommand(command);
        }
        scanner.close();
    }
}
