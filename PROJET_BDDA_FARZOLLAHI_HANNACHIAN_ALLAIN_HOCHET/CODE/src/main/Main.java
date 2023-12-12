import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBParams.DBPath = (args.length > 0) ? args[0] : "../DB";
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        DBParams.FrameCount = 2;
        // DatabaseManager
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        try {
            databaseManager.init();
        } catch (IOException | ClassNotFoundException err) {
            System.err.println("Can't start the SGBD!");
            err.printStackTrace();
            System.exit(1);
        }
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String command;
        while (!exit) {
            System.out.print("Entrez une commande (ou EXIT pour quitter): ");
            command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("EXIT")) {
                exit = true;
            } else {
                try {
                    databaseManager.processCommand(command);
                } catch (IOException err) {
                    System.err.println("Internal error:");
                    err.printStackTrace();
                } catch(IllegalArgumentException err) {
                    System.out.println("Your command is bad!");
                    err.printStackTrace();
                }
            }
        } try {
            databaseManager.finish();
        } catch (IOException | ClassNotFoundException err) {
            System.err.println("Can't stop the SGBD!");
            err.printStackTrace();
            System.exit(1);
        }
        scanner.close();
    }
}
