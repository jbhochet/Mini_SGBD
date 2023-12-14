import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCommand implements ICommand {
    public static final Pattern PATTERN = Pattern.compile("^SELECT \\* FROM ([\\w,]+)( WHERE (.+))?$");

    private String[] tablesName;
    private String conditions;

    public SelectCommand(String command) {
        Matcher matcher = PATTERN.matcher(command);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid command");

        tablesName = matcher.group(1).split(",");

        conditions = matcher.group(3);
    }

    private void displayRecValues(String[] values) {
        System.out.printf("(%s)%n", String.join(", ", values));
    }

    private String[] merge(String[] array1, String[] array2) {
        String[] res = new String[array1.length + array2.length];
        for (int i = 0; i < res.length; i++) {
            if (i < array1.length) {
                res[i] = array1[i];
            } else {
                int j = i % array1.length;
                res[i] = array2[j];
            }
        }
        return res;
    }

    @Override
    public void execute() throws IOException {
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        FileManager fileManager = FileManager.getInstance();
        int total = 0;

        if (tablesName.length == 1) {
            // a simple select
            TableInfo tableInfo = databaseInfo.getTableInfo(tablesName[0]);
            for (PageId page : fileManager.getDataPages(tableInfo)) {
                RecordIterator recordIterator = new RecordIterator(tableInfo, page);
                Record record;
                while ((record = recordIterator.getNextRecord()) != null) {
                    if (conditions == null || ConditionUtil.checkAll(record, conditions)) {
                        displayRecValues(record.getRecValues());
                        total++;
                    }
                }
                recordIterator.close();
            }

        } else {
            // We must handle the join!
            TableInfo tableInfoR = databaseInfo.getTableInfo(tablesName[0]);
            TableInfo tableInfoS = databaseInfo.getTableInfo(tablesName[1]);

            // for each page of R
            for (PageId pageR : fileManager.getDataPages(tableInfoR)) {
                RecordIterator recordIteratorR = new RecordIterator(tableInfoR, pageR);
                Record recordR;

                // for each page of S
                for (PageId pageS : fileManager.getDataPages(tableInfoS)) {
                    RecordIterator recordIteratorS = new RecordIterator(tableInfoS, pageS);
                    Record recordS;

                    while ((recordR = recordIteratorR.getNextRecord()) != null) {
                        while ((recordS = recordIteratorS.getNextRecord()) != null) {
                            // Check if the condition is ok
                            if (conditions == null
                                    || ConditionUtil.checkAllJoinConditions(recordR, recordS, conditions)) {
                                displayRecValues(merge(recordR.getRecValues(), recordS.getRecValues()));
                                total++;
                            }
                        }
                        recordIteratorS.reset();
                    }
                    recordIteratorS.close();
                    recordIteratorR.reset();
                }
                recordIteratorR.close();
            }
        }

        System.out.println("Total records=" + String.valueOf(total));
    }
}
