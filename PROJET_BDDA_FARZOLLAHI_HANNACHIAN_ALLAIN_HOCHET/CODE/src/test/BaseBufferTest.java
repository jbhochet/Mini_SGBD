package test;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;

import main.BufferManager;

@Ignore
public class BaseBufferTest extends BaseDiskTest {
    public static BufferManager bufferManager;
    
    @BeforeAll
    public static void setup() throws IOException {
        BaseDiskTest.setup();
        bufferManager = new BufferManager();
    }

}
