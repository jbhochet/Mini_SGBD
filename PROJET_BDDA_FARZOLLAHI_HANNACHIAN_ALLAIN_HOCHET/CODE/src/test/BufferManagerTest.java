import java.io.IOException;
import java.util.Stack;

import org.junit.jupiter.api.Test;

public class BufferManagerTest extends BaseBufferTest {

    @Test
    public void testGetAndFreePage() throws IOException {
        Stack<PageId> stack = new Stack<>();
        PageId pageId;
        int i;
        for(i = 0 ; i<DBParams.FrameCount; i++) {
            pageId = diskManager.AllocPage();
            bufferManager.getPage(pageId);
            stack.add(pageId);
        }
        for(i = 0; i<DBParams.FrameCount; i++) {
            bufferManager.freePage(stack.pop(), i%2==0);
        }
    }

    @Test
    public void testFlushAll() throws IOException {
        PageId pageId;
        for(int i = 0 ; i<DBParams.FrameCount; i++) {
            pageId = diskManager.AllocPage();
            bufferManager.getPage(pageId);
        }
        bufferManager.flushAll();
    }

}