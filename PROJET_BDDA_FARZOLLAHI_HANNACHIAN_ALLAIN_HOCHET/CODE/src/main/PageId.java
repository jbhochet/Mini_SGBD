package main;

public class PageId {
    private int FileIdx;
    private int PageIdx;

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