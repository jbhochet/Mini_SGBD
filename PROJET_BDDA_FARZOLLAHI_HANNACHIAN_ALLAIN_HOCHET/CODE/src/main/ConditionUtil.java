import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ConditionUtil {

    public static List<Condition> parseConditions(String conditions) {
        System.out.println(conditions);
        StringTokenizer st = new StringTokenizer(conditions, " AND ");
        List<Condition> res = new ArrayList<>();

        while (st.hasMoreTokens()) {
            res.add(new Condition(st.nextToken()));
        }

        return res;
    }

    public static boolean checkAll(Record record, List<Condition> conditions) {
        boolean res = true;

        for(Condition condition: conditions) {
            if(!condition.check(record)) {
                res = false;
                break;
            }
        }

        return res;
    }

}
