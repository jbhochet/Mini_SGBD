package main;

import java.nio.ByteBuffer;

public class Frame {
    private ByteBuffer buffer;
    private PageId pageIdx;
    private int pinCount; // Le compteur associé à une page en mémoire indiquant le nombre de références actives à un élément
    private boolean dirty; // Pour vérifier si un élément a été modifié depuis sa dernière lecture ou écriture
    private int accessCount; // Le nombre d'accès à une page spécifique associée à une frame
    public Frame() {
        buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
        pageIdx = null;
        pinCount = 0;
        dirty = false;
        accessCount = 0;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public PageId getPageIdx() {
        return pageIdx;
    }

    public void setPageIdx(PageId pageIdx) {
        this.pageIdx = pageIdx;
    }

    public void incrementPinCount() {
        pinCount++;
    }

    public void decrementPinCount() {
        pinCount--;
    }

    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getAccessCount() {
        return accessCount;
    }

}
