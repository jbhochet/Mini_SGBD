package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import main.Record;
import main.TableInfo;
import main.ColInfo;
import main.PageId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class RecordTest extends BaseBufferTest {
    @Test
    public void testReadAndWritePage() throws IOException {
        PageId pageId = diskManager.AllocPage();
        ByteBuffer originalBuffer = ByteBuffer.allocate(1024);
        originalBuffer.putInt(1);
        originalBuffer.flip();
        diskManager.WritePage(pageId, originalBuffer);
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        diskManager.ReadPage(pageId, readBuffer);
        readBuffer.flip();
        assertEquals(1, readBuffer.getInt());
    }

}
