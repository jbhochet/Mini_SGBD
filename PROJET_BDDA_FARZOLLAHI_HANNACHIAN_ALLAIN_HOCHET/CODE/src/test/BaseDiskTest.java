import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;

@Ignore
public class BaseDiskTest {
    public static DiskManager diskManager;

    @BeforeAll
    public static void setup() throws IOException {
        if (diskManager != null)
            return;
        File tmpDir = Files.createTempDirectory("DB_").toFile();
        DBParams.DBPath = tmpDir.getAbsolutePath();
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        diskManager = DiskManager.getInstance();
        // Delete temp DB at shutdown, thanks to https://stackoverflow.com/a/5824066
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (File file : Objects.requireNonNull(tmpDir.listFiles()))
                    file.delete();
                tmpDir.delete();
            }
        }));
    }

}
