import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^DELETE \\* FROM (\\w+)( WHERE (.+))?$");
    private String tableName;
    private String conditions;

    public DeleteCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid command");
        }
        tableName = matcher.group(1);
        conditions = matcher.group(3);
    }

    @Override
    public void execute() throws IOException {
        DiskManager diskManager = DiskManager.getInstance();
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        TableInfo tableInfo = databaseInfo.getTableInfo(tableName);
        FileManager fileManager = FileManager.getInstance();
        List<Record> listRecords = new ArrayList<>();
        ByteBuffer buffer;
        RecordIterator recordIterator;
        Record record;
        int nb = 0;
        for (PageId page : fileManager.getDataPages(tableInfo)) {
            recordIterator = new RecordIterator(tableInfo, page);
            while ((record = recordIterator.getNextRecord()) != null) {
                if (conditions != null && !ConditionUtil.checkAll(record, conditions)) {
                    listRecords.add(record);
                } else {
                    nb++;
                }
            }
            recordIterator.close();
            diskManager.DeallocPage(page);
        }
        System.out.println("Total deleted records=" + nb);
        buffer = BufferManager.getInstance().getPage(tableInfo.getHeaderPageId());
        buffer.position(0);
        buffer.putInt(-1);
        buffer.putInt(-1);
        BufferManager.getInstance().freePage(tableInfo.getHeaderPageId(), true);
        for (Record records : listRecords) {
            fileManager.InsertRecordIntoTable(records);
        }
    }
}
