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

    // Allouer une page
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

    public void ReadPage(PageId pageId, ByteBuffer buffer) throws IOException {
        if (pageId.getFileIdx() < 0 || pageId.getFileIdx() > dataFiles.length) {
            throw new IllegalArgumentException("the specified file does not exist!");
        }
        File file = dataFiles[pageId.getFileIdx()];
        if (pageId.getPageIdx() < 0 || file.length() < (long) pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "r");
        dataFile.seek((long) pageId.getPageIdx() * DBParams.SGBDPageSize); // modifier la position du pointeur
        int bytesRead = dataFile.getChannel().read(buffer); // Lire le contenu de la page dans le ByteBuffer
        if (bytesRead != -1) { // En Java, la valeur -1 est retournée pour indiquer la fin du fichier
            buffer.flip(); // Préparer le ByteBuffer pour la lecture
        }
        dataFile.close();
    }

    public void WritePage(PageId pageId, ByteBuffer buffer) throws IOException {
        if (pageId.getFileIdx() < 0 || pageId.getFileIdx() > dataFiles.length) {
            throw new IllegalArgumentException("The specified file does not exist!");
        }
        File file = dataFiles[pageId.getFileIdx()];
        if (pageId.getPageIdx() < 0 || file.length() < (long) pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
        dataFile.seek((long) pageId.getPageIdx() * DBParams.SGBDPageSize); // Positionner le pointeur à l'emplacement spécifié
        dataFile.write(buffer.array()); // Écrire le contenu du ByteBuffer dans le fichier
        dataFile.close();
    }

    public void DeallocPage(PageId pageId) {
        deallocatedPages.add(pageId);
    }

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
