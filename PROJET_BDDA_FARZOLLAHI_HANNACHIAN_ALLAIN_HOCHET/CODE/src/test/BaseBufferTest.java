package test;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;

import main.BufferManager;
import main.DBParams;

@Ignore
public class BaseBufferTest extends BaseDiskTest {
    public static BufferManager bufferManager;
    
    @BeforeAll
    public static void setup() throws IOException {
        BaseDiskTest.setup();
        DBParams.FrameCount = 2;
        bufferManager = new BufferManager();
    }

}
