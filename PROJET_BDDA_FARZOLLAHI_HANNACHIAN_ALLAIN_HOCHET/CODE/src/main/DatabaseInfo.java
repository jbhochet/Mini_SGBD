import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInfo {
    public static final String SAVE_FILE = "DBInfo.save";
    private static DatabaseInfo instance;
    private Map<String, TableInfo> tables;
    private DatabaseInfo() {
        this.tables = new HashMap<>();
    }

    public static DatabaseInfo getInstance() {
        if (instance == null) {
            instance = new DatabaseInfo();
        }
        return instance;
    }

    private File getSaveFile() {
        String path = Paths.get(DBParams.DBPath, SAVE_FILE).toAbsolutePath().toString();
        return new File(path);
    }

    // Load the information of DBinfo.save in the instance of DatabaseInfo
    public void init() throws IOException, ClassNotFoundException {
        File file = getSaveFile();
        if (!file.exists()) {
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object[] objects = (Object[]) ois.readObject();
            TableInfo temp;
            for (Object obj : objects) {
                if (obj instanceof TableInfo) {
                    temp = (TableInfo) obj;
                    tables.put(temp.getName(), temp);
                } else {
                    throw new ClassNotFoundException("save file does not contains TableInfo objects!");
                }
            }
        }
    }

    // Save the information in a file named DBinfo.save
    public void finish() throws IOException {
        FileOutputStream fos = new FileOutputStream(getSaveFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tables.values().toArray());
        oos.close();
    }

    public void reset() {
        tables.clear();
    }

    // Add an instance of TableInfo in the map
    public void addTableInfo(TableInfo tableInfo) {
        tables.put(tableInfo.getName(), tableInfo);
    }

    // Take the name of a relation and return the associated instance of TableInfo
    public TableInfo getTableInfo(String tableName) {
        return tables.get(tableName);
    }
}
