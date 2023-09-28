package main;


public class Main {
    public static void main(String[] args) {
        
        String dbPath = (args.length > 0) ? args[0] : "../DB";
        DBParams.DBPath = dbPath;
        DBParams.DMFileCount=4;
        DBParams.SGBDPageSize=4096;

        
        DiskManager diskManager = DiskManager.getInstance();
        
        // Allocation du page
        PageId allocatedPageId = diskManager.AllocPage();
        if (allocatedPageId != null) {
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
        }
    }
}
