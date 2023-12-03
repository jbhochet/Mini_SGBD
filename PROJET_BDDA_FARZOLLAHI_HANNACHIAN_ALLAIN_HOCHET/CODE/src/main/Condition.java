import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {
    public static Pattern PATTERN = Pattern.compile("^(\\w+)(=|<|>|<=|>=|<>)(.+)$");

    private String column;
    private String operator;
    private String value;

    public Condition(String condition) {
        Matcher matcher = PATTERN.matcher(condition);

        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid condition: " + condition);

        column = matcher.group(1);
        operator = matcher.group(2);
        value = matcher.group(3);
    }

    public boolean check(Record record) {
        TableInfo tableInfo = record.getTabInfo();
        int index = tableInfo.getColumnIndex(column);
        String recValue = record.getRecValues()[index];
        boolean res = false;

        switch (operator) {
            case "=":
                res = recValue.equals(value);
                break;
            case "<":
                res =Double.parseDouble(recValue)<Double.parseDouble(value);
                break;
            case ">":
                res = Double.parseDouble(recValue)>Double.parseDouble(value);
                break;
            case "<=":
                res = Double.parseDouble(recValue)<=Double.parseDouble(value);
                break;
            case ">=":
                res = Double.parseDouble(recValue)>=Double.parseDouble(value);
                break;
            case "<>":
                res = !recValue.equals(value);
                break;
        }

        return res;
    }
}
