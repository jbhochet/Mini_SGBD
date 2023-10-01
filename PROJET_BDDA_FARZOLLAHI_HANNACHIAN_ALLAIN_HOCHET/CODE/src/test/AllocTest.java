package test;

import org.junit.jupiter.api.Test;

import main.PageId;
import main.DBParams;
import main.DiskManager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class AllocTest {
    private DiskManager diskManager;

    @Test
    public void testAllocPage() throws IOException {
        String dbPath = "../DB";
        DBParams.DBPath = dbPath;
        DBParams.DMFileCount = 1000;
        DBParams.SGBDPageSize = 4096;
        diskManager = DiskManager.getInstance();

        int numberOfPagesToAllocate = 1000;

        for (int i = 0; i < numberOfPagesToAllocate; i++) {
            PageId allocatedPageId = diskManager.AllocPage();

            assertNotNull(allocatedPageId);
            assertEquals(0, allocatedPageId.getPageIdx());
	assertEquals(i, allocatedPageId.getFileIdx());
        }
    }
}
