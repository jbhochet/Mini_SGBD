import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^IMPORT INTO (\\w+) (\\w+.csv)$");
    public static final String DELIM = ",";
    private String relation;
    private File file;

    public ImportCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("This command does not match IMPORT pattern!");
        }
        this.relation = matcher.group(1);
        this.file = new File(matcher.group(2));
    }

    @Override
    public void execute() throws IOException {
        TableInfo tableInfo = DatabaseInfo.getInstance().getTableInfo(this.relation);
        FileManager fileManager = FileManager.getInstance();
        Record record;
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            while ((line = reader.readLine()) != null) {
                record = new Record(tableInfo);
                record.setRecValues(line.split(DELIM));
                fileManager.InsertRecordIntoTable(record);
            }
        }
    }
}
