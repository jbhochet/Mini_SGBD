import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportCommand implements ICommand {

    private String relationName;
    private String fileName;

    public ImportCommand(String command) {
        parseCommand(command);
    }

    private void parseCommand(String command) {
        String[] tokens = command.split("\\s+");
        this.relationName = tokens[2];
        this.fileName = tokens[3];
    }

    @Override
    public void execute() throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\t");
                Record record = new Record(DatabaseInfo.getInstance().getTableInfo(relationName));

                /*
                for (String value : values) {
                    record.addRecordValue(value);
                }
                 */

                System.out.println("Inserting record into " + relationName + ": " + line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'importation : " + e.getMessage());
        }
    }
}

