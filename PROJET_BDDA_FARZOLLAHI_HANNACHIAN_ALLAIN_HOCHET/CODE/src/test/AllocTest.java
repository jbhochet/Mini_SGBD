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
        DBParams.DMFileCount = 1;
        DBParams.SGBDPageSize = 4096;
        diskManager = DiskManager.getInstance();

        int numberOfPagesToAllocate = 100;

        for (int i = 1; i < numberOfPagesToAllocate; i++) {
            PageId allocatedPageId = diskManager.AllocPage();

            assertNotNull(allocatedPageId);
            assertEquals(i, allocatedPageId.getPageIdx());
        }
    }
}
