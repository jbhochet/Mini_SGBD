package main;

import java.nio.ByteBuffer;

public class Frame {
    private ByteBuffer buffer;
    private PageId pageId;
    private int pinCount; // Le compteur associé à une page en mémoire indiquant le nombre de références actives à un élément
    private boolean dirty; // Pour vérifier si un élément a été modifié depuis sa dernière lecture ou écriture
    public Frame() {
        buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
        pageId = null;
        pinCount = 0;
        dirty = false;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public PageId getPageId() {
        return pageId;
    }


    public int getPinCount() {
        return pinCount;
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

    public void setDirty( ) {
        this.dirty = true;
    }

    public void replacePage(PageId p) {
    	reset();
    	pageId = p;
    }
 

    public void reset() {
        pageId = null;
        pinCount = 0;
        dirty = false;
    }

}
