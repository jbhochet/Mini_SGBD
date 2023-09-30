package main;

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
    public DiskManager() throws IOException {
        this.dataFiles = new File[DBParams.DMFileCount];
        this.deallocatedPages = new Stack<>();
        createDataFiles();

    }

    // Singleton : getInstance method
    public static DiskManager getInstance() throws IOException {
        if (instance == null) {

            instance = new DiskManager();
        }
        return instance;
    }

    private void createDataFiles() throws IOException {
        File tmpFile;
        for (int i = 0; i < DBParams.DMFileCount; i++) {
            tmpFile = new File(Paths.get(DBParams.DBPath, "F" + i + ".data").toString());
            tmpFile.createNewFile();
            dataFiles[i] = tmpFile;
        }
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
        if (!deallocatedPages.empty())
            return deallocatedPages.pop();
        int fileId = lightestFileId();
        RandomAccessFile raf = new RandomAccessFile(dataFiles[fileId], "rw");
        raf.seek(dataFiles[fileId].length());
        raf.write(ByteBuffer.allocate(DBParams.SGBDPageSize).array());
        raf.close();
        PageId page = new PageId(fileId, (int) dataFiles[fileId].length() / DBParams.SGBDPageSize);
        return page;
    }

    public void ReadPage(PageId pageId, ByteBuffer buffer) throws IOException {
        File file = dataFiles[pageId.getFileIdx()];
        if (!file.exists() || file.length() < pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "r");
        dataFile.seek(pageId.getPageIdx()); // modifier la position du pointeur
        int bytesRead = dataFile.getChannel().read(buffer); // Lire le contenu de la page dans le ByteBuffer
        if (bytesRead != -1) { // En Java, la valeur -1 est retournée pour indiquer la fin du fichier
            buffer.flip(); // Préparer le ByteBuffer pour la lecture
        }
        dataFile.close();
    }

    public void WritePage(PageId pageId, ByteBuffer buffer) throws IOException {
        File file = dataFiles[pageId.getFileIdx()];
        if (!file.exists() || file.length() < pageId.getPageIdx() * DBParams.SGBDPageSize) {
            throw new IllegalArgumentException("La page spécifiée n'existe pas.");
        }
        if (pageId.getPageIdx() < 0 || pageId.getPageIdx() >= file.length()) {
            throw new IllegalArgumentException("L'indice de page spécifié est hors limites.");
        }
        RandomAccessFile dataFile = new RandomAccessFile(file, "rw");
        dataFile.seek(pageId.getPageIdx()); // Positionner le pointeur à l'emplacement spécifié
        dataFile.getChannel().write(buffer); // Écrire le contenu du ByteBuffer dans le fichier
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

}
