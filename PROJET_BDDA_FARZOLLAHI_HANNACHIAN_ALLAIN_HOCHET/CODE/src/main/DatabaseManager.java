import java.io.IOException;

public class DatabaseManager {
    private static DatabaseManager instance;

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }

    public void init() throws IOException, ClassNotFoundException {

    }

    public void finish() throws IOException  {

    }

    public void ProcessCommand (String command) {
        
    }

}