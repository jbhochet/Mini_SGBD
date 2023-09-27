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
}