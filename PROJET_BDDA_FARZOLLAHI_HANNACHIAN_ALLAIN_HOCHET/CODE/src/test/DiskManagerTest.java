package test;

import org.junit.jupiter.api.Test;

import main.PageId;
import main.DBParams;
import main.DiskManager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;


public class DiskManagerTest {
    DiskManager diskManager;
    
    @Test
    public void testAllocPage() throws IOException {
        String dbPath =  "../DB";
        DBParams.DBPath = dbPath;
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        diskManager = DiskManager.getInstance();

		PageId allocatedPageId = diskManager.AllocPage();

		assertNotNull(allocatedPageId);
		assertEquals(0, allocatedPageId.getPageIdx());
    }
    
    @Test
    public void testReadWritePage() {
        try {
        
        	String dbPath = "../DB";
            DBParams.DBPath = dbPath;
            DBParams.DMFileCount = 4;
            DBParams.SGBDPageSize = 4096;
            diskManager = DiskManager.getInstance();

            PageId allocatedPageId = diskManager.AllocPage();

            ByteBuffer writeBuffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
            writeBuffer.putInt(42); 
            writeBuffer.flip();

            diskManager.WritePage(allocatedPageId, writeBuffer);

            ByteBuffer readBuffer = ByteBuffer.allocate(DBParams.SGBDPageSize);

            diskManager.ReadPage(allocatedPageId, readBuffer);

            int data = readBuffer.getInt();
            assertEquals(42, data);
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testDeallocPage() throws IOException {
    	 String dbPath =  "../DB";
         DBParams.DBPath = dbPath;
         DBParams.DMFileCount = 4;
         DBParams.SGBDPageSize = 4096;
         diskManager = DiskManager.getInstance();

         PageId allocatedPageId = diskManager.AllocPage();
        
         diskManager.DeallocPage(allocatedPageId);

         int allocatedPagesCount = diskManager.GetCurrentCountAllocPages();
         assertEquals(2, allocatedPagesCount);
        
    }
    
}

