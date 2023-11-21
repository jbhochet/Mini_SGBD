import java.io.File;
import java.io.IOException;

public class ResetDBCommand implements ICommand {

    private void clearDB() {
        File dbpath = new File(DBParams.DBPath);
        for (File file : dbpath.listFiles()) {
            if (file.getName().endsWith(".data") || file.getName().endsWith(".save")) {
                // TODO: handle false?
                file.delete();
            }
        }
    }

    @Override
    public void execute() throws IOException {
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        BufferManager bufferManager = BufferManager.getInstance();
        DiskManager diskManager = DiskManager.getInstance();

        // reset all falgs
        diskManager.reset();
        bufferManager.reset();
        databaseInfo.reset();

        // clear all files in dbpath
        clearDB();

        // create all files in dbpath again!
        diskManager.init();

        System.out.println("DB cleaned up! you can write useless stuff again ;)");
    }

}