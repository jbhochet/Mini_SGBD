package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class DiskManager {
    private static final String DB_DIRECTORY = ".." + File.separator + ".." + File.separator + ".." + File.separator + "DB";
    private static final int DEFAULT_PAGE_SIZE = 4096; // La taille de page par défaut

    private DiskManager() { // Constructeur privé pour empêcher l'instanciation directe
        initializeDBDirectory(); // Initialiser le répertoire de la base de données
    }

    private void initializeDBDirectory() { // Créer un répertoire de base de données (DB) si ce répertoire n'existe pas déjà
        File dbDirectory = new File(DB_DIRECTORY);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdir();
        }
    }

    public void readPage (PageId pageId, ButeBuffer buffer) throws IOException {
        String filePath = DB_DIRECTORY + File.separator + "F" + pageId.getFileIdx() + ".data";
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
}