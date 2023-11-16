import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

    public void processCommand(String command) {
        switch (command.toUpperCase()) {
            case "CREATE_TABLE":
                //createTable();
                break;
            case "INSERT":
                //insertData();
                break;
            case "SELECT":
                //selectData();
                break;
            default:
                // Commande inconnue, gestion de l'erreur ou traitement par défaut
                System.out.println("Commande inconnue : " + command);
                break;
        }
    }

}