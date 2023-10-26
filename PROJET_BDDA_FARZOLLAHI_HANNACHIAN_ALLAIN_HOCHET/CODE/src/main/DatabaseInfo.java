package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInfo {
    public static final String SAVE_FILE = "DBInfo.save";
    private static final DatabaseInfo instance = new DatabaseInfo();
    private Map<String, TableInfo> tables;
    private int tableCounter;

    private DatabaseInfo() {
        this.tables = new HashMap<>();
        this.tableCounter = 0;
    }

    public static DatabaseInfo getInstance() {
        return instance;
    }

    private File getSaveFile() {
        String path = Paths.get(DBParams.DBPath, SAVE_FILE).toAbsolutePath().toString();
        return new File(path);
    }

    public void init() throws IOException, ClassNotFoundException{
        File file = getSaveFile();
        if(!file.exists()) return;
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Collection<?> objects = (Collection<?>) ois.readObject();
        for(Object obj: objects) {
            TableInfo table = (TableInfo) obj;
            this.tables.put(table.getTableName(), table);
        }
        ois.close();
    }

    public void finish() throws IOException {
        FileOutputStream fos = new FileOutputStream(getSaveFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tables.values());
        oos.close();
    }

    public void addTableInfo(TableInfo tableInfo) {
        tables.put(tableInfo.getTableName(), tableInfo);
        tableCounter++;
    }

    public TableInfo getTableInfo(String tableName) {
        return tables.get(tableName);
    }

    public int getTableCounter() {
        return tableCounter;
    }

    

}
