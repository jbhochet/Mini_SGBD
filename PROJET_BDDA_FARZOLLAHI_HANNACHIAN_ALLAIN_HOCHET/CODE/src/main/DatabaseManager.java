import java.io.IOException;

public class DatabaseManager {
    private static DatabaseManager instance;

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }

    public void init() throws IOException, ClassNotFoundException {
        DatabaseInfo.getInstance().init();
        DiskManager.getInstance().init();
    }

    public void finish() throws IOException, ClassNotFoundException {
        DatabaseInfo.getInstance().finish();
    }

    public void processCommand(String command) throws IOException {
        ICommand cmd = null;

        if(command.startsWith("CREATE TABLE")) {
            cmd = new CreateTableCommand(command);
        } else if(command.startsWith("INSERT")) {

        } else if(command.startsWith("SELECT")) {

        } else {
            throw new IllegalArgumentException("Unknown command!");
        }

        cmd.execute();
    }

}