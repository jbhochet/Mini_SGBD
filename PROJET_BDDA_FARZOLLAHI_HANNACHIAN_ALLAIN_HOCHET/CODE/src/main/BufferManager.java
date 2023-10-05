package main;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferManager {
    private static BufferManager instance = null; // Une seule instance de la classe est créée
    private Frame[] bufferPool;

    public BufferManager() {
        bufferPool = new Frame[DBParams.FrameCount];
        for (int i = 0; i < DBParams.FrameCount; i++)
            bufferPool[i] = new Frame();
    }

    public static BufferManager getInstance() {
        if (instance == null)
            instance = new BufferManager();
        return instance;
    }

    public Frame FindFrame(PageId pageIdx) {
        for (Frame frame : bufferPool) {
            if (frame.getPageIdx() != null && frame.getPageIdx().equals(pageIdx)) // Si la frame contient la page qu'on recherche
                return frame;
        }
        return null; // Retourne null si la page n'est pas trouvée dans le pool
    }

    public Frame ReplaceLFU(PageId pageIdx) {
        Frame frameToReplace = null;
        for (Frame frame : bufferPool) {
            if (frame.getPageIdx() != null && frame.getPageIdx().equals(pageIdx)) // Si la frame contient la page qu'on recherche
                return frame;
            if (frameToReplace == null || frame.getAccessCount() < frameToReplace.getAccessCount())
                frameToReplace = frame;
        }
        return frameToReplace;
    }

    public ByteBuffer GetPage(PageId pageIdx) throws IOException {
        Frame frame = FindFrame(pageIdx);
        if (frame == null) // Page non trouvée dans le buffer pool ---> remplacement LFU
            frame = ReplaceLFU(pageIdx);
        if (frame.getDirty()) { // Si la frame est dirty, écrire la page sur le disque
            DiskManager.getInstance().WritePage(frame.getPageIdx(), frame.getBuffer());
            frame.setDirty(false); // Mettre à jour le flag dirty
        }
        DiskManager.getInstance().ReadPage(pageIdx, frame.getBuffer()); // Charger la page depuis le disque dans la frame
        frame.incrementPinCount(); // Mettre à jour le pin_count
        return frame.getBuffer();
    }

    public PageId DetermineNewPage() {
        if (bufferPool.length == 0)
            return null; // Gérer le cas où le tableau bufferPool est vide
        Frame lFUFrame = bufferPool[0]; // Initialiser la première frame comme la moins fréquemment utilisée
        for (int i = 1; i < bufferPool.length; i++) {
            Frame frame = bufferPool[i];
            if (frame.getAccessCount() < lFUFrame.getAccessCount())
                lFUFrame = frame;
        }
        return lFUFrame.getPageIdx();
    }

    public void FreePage(PageId pageIdx, int valDirty) throws IOException {
        Frame frame = FindFrame(pageIdx);
        if (frame != null) {
            frame.decrementPinCount(); // Décrémenter le pin_count
            if (valDirty == 1) // Mettre à jour le flag dirty
                frame.setDirty(true); // 1 indique que la page est marquée comme dirty
            else
                frame.setDirty(false); // Sinon la page est marquée comme clean
        }
        if (frame.getPinCount() == 0) {
            frame.incrementAccessCount(); // Incrémenter le compteur LFU
            Frame frameLFU = ReplaceLFU(pageIdx); // Chercher la frame LFU
            if (frameLFU != null) {
                PageId newPageIdx = DetermineNewPage(); // Charger la nouvelle page depuis le disque
                ByteBuffer newPageBuffer = ByteBuffer.allocate(DBParams.SGBDPageSize); // Créez un nouveau buffer pour la nouvelle page
                DiskManager.getInstance().ReadPage(newPageIdx, newPageBuffer); // Chargez la nouvelle page depuis le disque
                frameLFU.setPageIdx(newPageIdx); // Mettre à jour le PageId avec le nouveau
                frameLFU.setBuffer(newPageBuffer); // Mettre à jour le buffer avec le contenu de la nouvelle page
                frameLFU.setDirty(false); // La nouvelle page est clean
                frameLFU.resetAccessCount(); // Réinitialiser le compteur LFU
                if (frameLFU.getDirty()) // Ecrire la page retirée sur le disque si elle est marquée comme dirty
                    DiskManager.getInstance().WritePage(frameLFU.getPageIdx(), frameLFU.getBuffer()); // Écrire la page sur le disque
            }
        }
    }

    public void FlushAll() throws IOException {
    	for(Frame fr : bufferPool) {
    		if(fr.getDirty()){ 
    			PageId pageId = fr.getPageIdx();
    			ByteBuffer buffer = fr.getBuffer();
    			DiskManager.getInstance().WritePage(pageId,buffer); // l’écriture de toutes les pages dont le flag dirty = 1 (true) sur disque
    		}
    		fr.reset(); //la remise à 0 de tous les flags/informations et contenus des buffers (buffer pool « vide »)
    	}
    }

}
