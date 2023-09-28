package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DiskManager {
    private static final int DEFAULT_PAGE_SIZE = 4096;

    private static DiskManager instance;
    private List<File> dataFiles;
    private List<PageId> deallocatedPages;

    // Constructor to initialize DBPath
    public DiskManager() {
        this.dataFiles = new ArrayList<>();
        this.deallocatedPages = new ArrayList<>();
        createDataFiles(DBParams.DMFileCount);
    }

    // Singleton : getInstance method
    public static DiskManager getInstance() {
        if (instance == null) {

            instance = new DiskManager();
        }
        return instance;
    }

       // Allouer une page
    public PageId AllocPage() {
        int i = 1;
        PageId allocatedPageId = null;
        
        // Vérifie s'il y a des pages désallouées disponibles.
        if (!deallocatedPages.isEmpty()) {
            // Si oui, allouer la première page désallouée.
            allocatedPageId = deallocatedPages.remove(0);
        } else {
        	String fileName = DBPath + File.separator + "F" + i + ".data";
        	i++;
            File file = new File(fileName);
            File target=file;
            try {
                file.createNewFile();
                dataFiles.add(file); // Add the created file to the list
                System.out.println("File created: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error creating file.");
            }
            // Parcoure les fichiers de données pour trouver un fichier approprié.
            for (File f : dataFiles) {
                if (f.length() % DBParams.pageSize == 0) {
                    if (target == null || f.length() < target.length()) {
                    	target = f;
                    }
                }
            }
            if (target == null || target.length() >= DBParams.DMFileCount * 4096) {
                System.err.println("Aucun fichier approprié trouvé pour l'allocation.");
                return null;
            }
            // Calculer l'indice de la page allouée dans le fichier cible.
            int pageIdx = (int) (target.length() / DBParams.pageSize);
             
            // Créer l'identifiant de la page allouée.
            allocatedPageId = new PageId(dataFiles.indexOf(target), pageIdx);
            DeallocPage(allocatedPageId);

            pageIdx++;
            //ajoute une nouvelle page vide au fichier.
            if (pageIdx * DBParams.pageSize >= target.length()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
                    randomAccessFile.seek(target.length());
                    randomAccessFile.write(new byte[DBParams.pageSize]); // Écrire des données vides pour créer une nouvelle page 4 KO
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors de l'ajout d'une nouvelle page au fichier.");
                    return null;
                }
            }
            
        }
        return allocatedPageId;
    }
    public void ReadPage (PageId pageId, ByteBuffer buffer) throws IOException {
        String filePath = DBParams.DBPath + File.separator + "F" + pageId.getFileIdx() + ".data";
        File file = new File(filePath);
        if (!file.exists() || file.length() < pageId.getPageIdx() + DEFAULT_PAGE_SIZE) {
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
        String filePath = DBParams.DBPath + File.separator + "F" + pageId.getFileIdx() + ".data";
        File file = new File(filePath);
        if (!file.exists() || file.length() < pageId.getPageIdx() + DEFAULT_PAGE_SIZE) {
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

    private void createDataFiles(int fileCount) {
        for (int i = 1; i <= fileCount; i++) {
            String fileName = DBParams.DBPath + File.separator + "F" + i + ".data";
            File file = new File(fileName);
            try {
                file.createNewFile();
                dataFiles.add(file); // Add the created file to the list
                System.out.println("File created: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error creating file.");
            }
        }
    }

    public void DeallocPage(PageId pageId) {
        deallocatedPages.add(pageId);
    }

  
    /**
     * @return the numbers of allocated pages
     */
    public int GetCurrentCountAllocPages() {
        int count;
        count=(DBParams.DMFileCount)*dataFiles.size();
        count=count-deallocatedPages.size();
        return count;
    }

}
