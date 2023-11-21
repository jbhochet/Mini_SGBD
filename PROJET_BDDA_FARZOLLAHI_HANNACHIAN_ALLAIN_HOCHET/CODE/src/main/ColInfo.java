import java.io.Serializable;

public class ColInfo implements Serializable {
    private String name;
    private DataType type;
    private int t;

    public ColInfo(String name, DataType type) {
        this(name, type, 0);
    }

    public ColInfo(String name, DataType type, int t) {
        this.name = name;
        this.type = type;
        this.t = t;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public int getT() {
        return t;
    }
}