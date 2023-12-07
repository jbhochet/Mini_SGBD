import java.io.Serializable;

public class PageId implements Serializable {
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
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PageId other = (PageId) obj;
        return FileIdx == other.getFileIdx() && PageIdx == other.getPageIdx();
    }

    @Override
    public String toString() {
        return String.format("PageId(FileIdx=%d, PageIdx=%d)", this.FileIdx, this.PageIdx);
    }
}
