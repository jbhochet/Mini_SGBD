import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {
    public static Pattern PATTERN = Pattern.compile("^(\\w+)(=|<|>|<=|>=|<>)(.+)$");
    private String column;
    private String operator;
    private String value;

    public Condition(String condition) {
        Matcher matcher = PATTERN.matcher(condition);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid condition: " + condition);
        }
        column = matcher.group(1);
        operator = matcher.group(2);
        value = matcher.group(3);
    }

    private static int compare(DataType type, String val1, String val2) {
        int res;
        if (type == DataType.INT) {
            Integer a = Integer.parseInt(val1);
            Integer b = Integer.parseInt(val2);
            res = a.compareTo(b);
        } else if (type == DataType.FLOAT) {
            Float a = Float.parseFloat(val1);
            Float b = Float.parseFloat(val2);
            res = a.compareTo(b);
        } else if (type == DataType.STRING || type == DataType.VARSTRING) {
            res = val1.compareTo(val2);
        } else {
            throw new IllegalArgumentException("Unknow type: " + type);
        }
        return res;
    }

    public boolean check(Record record) {
        TableInfo tableInfo = record.getTabInfo();
        int index = tableInfo.getColumnIndex(column);
        String recValue = record.getRecValues()[index];
        int compareResult = compare(tableInfo.getColumns()[index].getType(), recValue, this.value);
        return switch (operator) {
            case "=" -> compareResult == 0;
            case "<" -> compareResult < 0;
            case ">" -> compareResult > 0;
            case "<=" -> compareResult <= 0;
            case ">=" -> compareResult >= 0;
            case "<>" -> compareResult != 0;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}
