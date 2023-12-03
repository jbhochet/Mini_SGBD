import java.util.ArrayList;
import java.util.List;

public class ConditionUtil {

    public static List<Condition> parseConditions(String conditions) {
        System.out.println(conditions);
        String[] conditionsArray = conditions.split(" AND ");
        List<Condition> res = new ArrayList<>();

        for(int i = 0; i<conditionsArray.length; i++) {
            res.add(new Condition(conditionsArray[i]));
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
