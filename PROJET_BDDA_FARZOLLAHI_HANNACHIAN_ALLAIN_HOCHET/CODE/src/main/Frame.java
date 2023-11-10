import java.nio.ByteBuffer;

public class Frame {
    private ByteBuffer buffer;
    private PageId pageId;
    private int pinCount;
    private boolean dirty;

    public Frame() {
        this.buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
        this.pageId = null;
        this.pinCount = 0;
        this.dirty = false;
    }

    public ByteBuffer getBuffer() {
        return buffer;
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

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public void replacePage(PageId p) {
        reset();
        pageId = p;
    }

    public void reset() {
        buffer.clear();
        pageId = null;
        pinCount = 0;
        dirty = false;
    }

}