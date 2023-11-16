import java.io.IOException;
import java.nio.file.Files;

public class TestDatabaseInfo {
    public static void main(String[] args) {
        // Create a temp DB
        try {
            DBParams.DBPath = Files.createTempDirectory("DB_").toFile().getAbsolutePath();
        } catch (IOException err) {
            System.err.println("Can't create temp folder!");
            System.err.println(err);
            System.exit(1);
        }

        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();

        try {
            databaseInfo.init();

            databaseInfo.finish();
        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
        }
    }
}
