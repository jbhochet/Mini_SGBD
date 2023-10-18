package main;

import java.nio.ByteBuffer;

public class PageId {
    private int FileIdx;
    private int PageIdx;
    private static final int SIZE = 8; // Taille en octets de PageId (2 entiers de 4 octets)

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
        return String.format("PageId(FileIdx=%d, PageIdx=%d)", this.FileIdx, this.PageIdx);
    }
}