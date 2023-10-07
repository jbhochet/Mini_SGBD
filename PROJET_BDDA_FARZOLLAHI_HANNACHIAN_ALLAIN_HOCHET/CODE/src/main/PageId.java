package main;

public class PageId {
    private int AccessCount;
    private int FileIdx;
    private int PageIdx;

    public PageId(int fileIdx, int pageIdx) {
        this.FileIdx = fileIdx;
        this.PageIdx = pageIdx;
        this.AccessCount = 0; // Initialize access count to 0
    }

    public int getFileIdx() {
        return FileIdx;
    }

    public int getPageIdx() {
        return PageIdx;
    }

    public int getAccessCount() {
        return AccessCount;
    }

    public void incrementAccessCount() {
        AccessCount++;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PageId other = (PageId) obj;
        System.out.println("Comparing " + this + " with " + other);
        return FileIdx == other.FileIdx && PageIdx == other.PageIdx;
    }

    @Override
    public String toString() {
        return "PageId(FileIdx=" + FileIdx + ", PageIdx=" + PageIdx + ", AccessCount=" + AccessCount + ")";
    }
}