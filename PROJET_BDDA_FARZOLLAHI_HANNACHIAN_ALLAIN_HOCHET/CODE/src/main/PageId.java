package main;

public class PageId {
    private int FileIdx;
    private int PageIdx;
    private int accessCount;

    
    public PageId(int fileIdx, int pageIdx) {
        this.FileIdx = fileIdx;
        this.PageIdx = pageIdx;
    }

    public int getFileIdx() {
        return FileIdx;
    }

    public int getPageIdx() {
        return PageIdx;
    }
    
    public int getAccessCount() {
        return accessCount;
    }

    public void incrementAccessCount() {
        accessCount++;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PageId))
            return false;
        PageId page = (PageId) obj;
        if (this == page)
            return true;
        if ((FileIdx == page.getFileIdx()) && (PageIdx == page.getPageIdx()))
            return true;
        return false;
    }
}