import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionUtil {
    public static final Pattern COND_PATTERN = Pattern.compile("^(\\w+)(=|<|>|<=|>=|<>)([^=<>].*)$");
    public static final Pattern JOIN_COND_PATTERN = Pattern.compile("^\\w+\\.(\\w+)(=|<|>|<=|>=|<>)\\w+\\.(\\w+)$");

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

    private static boolean compareWithOperator(String operator, int compareResult) {
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

    public static boolean check(Record record, String condition) {
        Matcher matcher = COND_PATTERN.matcher(condition);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid condition: " + condition);
        int indexVal = record.getTabInfo().getColumnIndex(matcher.group(1));
        DataType type = record.getTabInfo().getColumns()[indexVal].getType();
        int compareResult = compare(type, record.getRecValues()[indexVal], matcher.group(3));
        return compareWithOperator(matcher.group(2), compareResult);
    }

    public static boolean checkAll(Record record, String conditions) {
        boolean res = true;
        for (String condition : conditions.split(" AND ")) {
            if (!check(record, condition)) {
                res = false;
                break;
            }
        }
        return res;
    }

    public static boolean checkJoinCondition(Record recordR, Record recordS, String condition) {
        Matcher matcher = JOIN_COND_PATTERN.matcher(condition);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid join condition: " + condition);
        int indexValR = recordR.getTabInfo().getColumnIndex(matcher.group(1));
        int indexValS = recordS.getTabInfo().getColumnIndex(matcher.group(3));
        DataType type = recordR.getTabInfo().getColumns()[indexValR].getType();
        int compareResult = compare(type, recordR.getRecValues()[indexValR], recordS.getRecValues()[indexValS]);
        return compareWithOperator(matcher.group(2), compareResult);
    }

    public static boolean checkAllJoinConditions(Record recordR, Record recordS, String conditions) {
        boolean res = true;
        for (String condition : conditions.split(" AND ")) {
            if (!checkJoinCondition(recordR, recordS, condition)) {
                res = false;
                break;
            }
        }
        return res;
    }
}
