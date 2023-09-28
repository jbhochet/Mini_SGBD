package main;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class DiskManager {
    private static DiskManager instance;
    private List<File> dataFiles;
    private int pageSize;
    private List<PageId> deallocatedPages; 

    // Constructor to initialize DBPath
    public DiskManager() {
        this.dataFiles = new ArrayList<>();
        this.pageSize = DBParams.pageSize;
        this.deallocatedPages = new ArrayList<>();
        //createDataFiles(DBParams.DMFileCount); 
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
        PageId allocatedPageId = null;
        
        // Vérifie s'il y a des pages désallouées disponibles.
        if (!deallocatedPages.isEmpty()) {
            // Si oui, allouer la première page désallouée.
            allocatedPageId = deallocatedPages.remove(0);
        } else {
            File target = null;
            // Parcoure les fichiers de données pour trouver un fichier approprié.
            for (File file : dataFiles) {
                if (file.length() % pageSize == 0) {
                    if (target == null || file.length() < target.length()) {
                    	target = file;
                    }
                }
            }
            if (target == null || target.length() >= Integer.MAX_VALUE - pageSize) {
                System.err.println("Aucun fichier approprié trouvé pour l'allocation.");
                return null;
            }
            // Calculer l'indice de la page allouée dans le fichier cible.
            int pageIdx = (int) (target.length() / pageSize);
            
            // Créer l'identifiant de la page allouée.
            allocatedPageId = new PageId(dataFiles.indexOf(target), pageIdx);

            pageIdx++;
            //ajoute une nouvelle page vide au fichier.
            if (pageIdx * pageSize >= target.length()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
                    randomAccessFile.seek(target.length());
                    randomAccessFile.write(new byte[pageSize]); // Écrire des données vides pour créer une nouvelle page 4 KO
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



    /*
    public void ReadPage(PageId pageId, byte[] buff) {
        String fileName = DBPath + File.separator + "F" + pageId.getFileIdx() + ".data";
        int offset = pageId.getPageIdx() * pageSize;

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
            randomAccessFile.seek(offset);

            int bytesRead = randomAccessFile.read(buff);
            randomAccessFile.close();

            if (bytesRead == -1) {
                System.err.println("End of file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading data.");
        }
    }*/
    
    /*
    //public void ReadPage(PageId pageId, byte[] buff)
    
   
     */
    /*
    private void createDataFiles(int fileCount) {
        for (int i = 1; i <= fileCount; i++) {
            String fileName = DBPath + File.separator + "F" + i + ".data";
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
    */
    
    //public void DeallocPage(PageId pageId) {}
    
    //public int GetCurrenctCountAllocPages(){}

}
