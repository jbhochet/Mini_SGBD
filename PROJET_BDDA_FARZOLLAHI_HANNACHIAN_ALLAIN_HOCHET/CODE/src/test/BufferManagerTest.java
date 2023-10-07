package test;

import java.io.IOException;

import main.PageId;
import main.BufferManager;
import main.DBParams;
import main.DiskManager;
import main.Frame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class BufferManagerTest extends BaseBufferTest {
    private DiskManager diskManager;

    private BufferManager bufferManager;

    @BeforeEach
    public void setUp() {
        try {
        	
            // Initialize the DiskManager
        	 String dbPath = "../DB";
             DBParams.DBPath = dbPath;
             DBParams.DMFileCount = 4;
             DBParams.SGBDPageSize = 4096;
             diskManager = DiskManager.getInstance();
            
             // Initialize the BufferManager
            bufferManager = BufferManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllocPage() {
        try {
            PageId allocatedPage = diskManager.AllocPage();
            assertNotNull(allocatedPage);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while allocating a page.");
        }
    }

    @Test
    public void testFindFrame() {
        try {
            PageId allocatedPage = diskManager.AllocPage();
            Frame frame = bufferManager.FindFrame(allocatedPage);
            assertNotNull(frame);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while finding a frame.");
        }
    }

    @Test
    public void testReplaceLFU() {
        try {
            PageId pageToReplace = new PageId(0, 1);
            Frame replacedFrame = bufferManager.ReplaceLFU(pageToReplace);
            assertNotNull(replacedFrame);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while replacing LFU.");
        }
    }

    @Test
    public void testFreePage() {
        try {
            PageId allocatedPage = diskManager.AllocPage();
            bufferManager.freePage(allocatedPage, true);
            // Add assertions as needed to check the state after freeing a page
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while freeing a page.");
        }
    }

    @Test
    public void testFlushAll() {
        try {
            // Add assertions as needed to check the state after flushing all dirty frames
            bufferManager.FlushAll();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while flushing all frames.");
        }
    }
    
    @Test
    public void testFindFrameAndAccessCount1() {
        try {
            // Allocate a page
            PageId allocatedPage = diskManager.AllocPage();

            bufferManager.FindFrame(allocatedPage);

            // Increment the access count
            allocatedPage.incrementAccessCount();

            // Verify that the access count is incremented
            assertEquals(1, allocatedPage.getAccessCount());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while testing access count.");
        }
        
    }
    @Test
    public void testFindFrameAndAccessCount() {
        try {
            // Allocate a page
            PageId allocatedPage = diskManager.AllocPage();

            bufferManager.FindFrame(allocatedPage);

            // Increment the access count
            allocatedPage.incrementAccessCount();

            // Verify that the access count is incremented
            assertEquals(1, allocatedPage.getAccessCount());

            bufferManager.FindFrame(allocatedPage);
            allocatedPage.incrementAccessCount();
            allocatedPage.incrementAccessCount();

            // Verify that the access count is updated after multiple accesses
            assertEquals(3, allocatedPage.getAccessCount());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception thrown while testing access count.");
        }
    }



}