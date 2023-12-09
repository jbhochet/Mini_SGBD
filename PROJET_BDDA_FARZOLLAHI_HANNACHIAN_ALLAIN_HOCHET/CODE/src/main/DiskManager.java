import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Stack;

public class DiskManager {
    private static DiskManager instance;
    private File[] dataFiles;
    private Stack<PageId> deallocatedPages;

    // Constructor to initialize DBPath
    public DiskManager() {
        this.dataFiles = new File[DBParams.DMFileCount];
        this.deallocatedPages = new Stack<>();
    }

    // Singleton : getInstance method
    public static DiskManager getInstance() {
        if (instance == null) {
            instance = new DiskManager();
        }
        return instance;
    }

    public void init() throws IOException {
        File tmpFile;
        for (int i = 0; i < DBParams.DMFileCount; i++) {
            tmpFile = new File(Paths.get(DBParams.DBPath, "F" + i + ".data").toString());
            tmpFile.createNewFile();
            dataFiles[i] = tmpFile;
        }
    }

    public void reset() {
        deallocatedPages.clear();
    }

    private int lightestFileId() {
        int res = 0;
        for (int i = 1; i < dataFiles.length; i++) {
            if (dataFiles[i].length() < dataFiles[res].length()) {
                res = i;
            }
        }
        return res;
    }

    // Page allocation
    public PageId AllocPage() throws IOException {
        if (!deallocatedPages.empty()) {
            return deallocatedPages.pop();
        }
        int fileId = lightestFileId();
        RandomAccessFile raf = new RandomAccessFile(dataFiles[fileId], "rw");
        raf.seek(dataFiles[fileId].length());
        raf.write(ByteBuffer.allocate(DBParams.SGBDPageSize).array());
        raf.close();
        return new PageId(fileId, (int) dataFiles[fileId].length() / DBParams.SGBDPageSize - 1);
    }

    //Read an allocate page
    public void ReadPage(PageId pageId, ByteBuffer buffer) throws IOException {
        if (pageId.getFileIdx() < 0 || pageId.getFileIdx() > dataFiles.length) {
            throw new IllegalArgumentException("the specified file does not exist!");
        }
        File file = dataFiles[pageId.getFileIdx()];
        if (pageId.getPageIdx() < 0 || file.length() < (long) pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "r");
        dataFile.seek((long) pageId.getPageIdx() * DBParams.SGBDPageSize); // change pointer position
        int bytesRead = dataFile.getChannel().read(buffer); // Read the content of the page in the ByteBuffer
        if (bytesRead != -1) { // In Java the value -1 is return to indicate the end of the file
            buffer.flip(); // Prepare the ByteBuffer for reading
        }
        dataFile.close();
    }

    // Write what is in the buffer at the position of the pageId
    public void WritePage(PageId pageId, ByteBuffer buffer) throws IOException {
        if (pageId.getFileIdx() < 0 || pageId.getFileIdx() > dataFiles.length) {
            throw new IllegalArgumentException("The specified file does not exist!");
        }
        File file = dataFiles[pageId.getFileIdx()];
        if (pageId.getPageIdx() < 0 || file.length() < (long) pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
        dataFile.seek((long) pageId.getPageIdx() * DBParams.SGBDPageSize); // Position pointer at specified location
        dataFile.write(buffer.array()); // Write the content of the ByteBuffer in the file
        dataFile.close();
    }

    // Desallocate page and put it in the list of avaible pages
    public void DeallocPage(PageId pageId) {
        deallocatedPages.add(pageId);
    }

    // Return the number of currently allocated pages in the DiskManger
    public int GetCurrentCountAllocPages() {
        int pageCount = 0;
        for (File file : dataFiles)
            pageCount += (int) file.length() / DBParams.SGBDPageSize;
        return pageCount - deallocatedPages.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("===============").append('\n');
        sb.append("DiskManager\n");
        sb.append("File: ").append(dataFiles.length).append('\n');
        sb.append("Allocated pages: ").append(GetCurrentCountAllocPages()).append('\n');
        sb.append("Deallocated pages: ").append(deallocatedPages.size()).append('\n');
        sb.append("===============");
        return sb.toString();
    }
}
