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

    public void FreePage(PageId pageIdx, int valDirty) {
        Frame frame = FindFrame(pageIdx);
        if (frame != null) {
            frame.decrementPinCount(); // Décrémenter le pin_count
            if (valDirty == 1) // Mettre à jour le flag dirty
                frame.setDirty(true); // 1 indique que la page est marquée comme dirty
            else
                frame.setDirty(false); // Sinon la page est marquée comme clean
        }
    }

}
