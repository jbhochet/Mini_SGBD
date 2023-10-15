package main;

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

    public void serialize(byte[] byteArray, int offset) {
        if (offset + SIZE > byteArray.length)
            throw new IllegalArgumentException("Le tableau de bytes est trop petit pour stocker les données de PageId.");
        byteArray[offset] = (byte) ((FileIdx >> 24) & 0xFF); // Décalage de 24 bits vers la droite + masque pour obtenir le premier octet
        byteArray[offset + 1] = (byte) ((FileIdx >> 16) & 0xFF); // Décalage de 16 bits vers la droite + masque pour obtenir le deuxième octet
        byteArray[offset + 2] = (byte) ((FileIdx >> 8) & 0xFF); // Décalage de 8 bits vers la droite + masque pour obtenir le troisième octet
        byteArray[offset + 3] = (byte) (FileIdx & 0xFF); // Masque pour obtenir le dernier octet
        byteArray[offset + 4] = (byte) ((PageIdx >> 24) & 0xFF); // Décalage de 24 bits vers la droite + masque pour obtenir le premier octet
        byteArray[offset + 5] = (byte) ((PageIdx >> 16) & 0xFF); // Décalage de 16 bits vers la droite + masque pour obtenir le deuxième octet
        byteArray[offset + 6] = (byte) ((PageIdx >> 8) & 0xFF); // Décalage de 8 bits vers la droite + masque pour obtenir le troisième octet
        byteArray[offset + 7] = (byte) (PageIdx & 0xFF); // Masque pour obtenir le dernier octet
    }
}