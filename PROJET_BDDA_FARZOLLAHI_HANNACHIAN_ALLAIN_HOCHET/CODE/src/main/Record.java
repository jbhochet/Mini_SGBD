import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Record {
    private TableInfo tabInfo;
    private String[] recValues;

    public Record(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
        this.recValues = new String[tabInfo.getNumberOfColumns()];
    }

    public TableInfo getTabInfo() {
        return tabInfo;
    }

    public String[] getRecValues() {
        return recValues;
    }

    public void setRecValues(String[] recValues) {
        this.recValues = recValues;
    }

    private void putString(ByteBuffer buffer, String value, int size) {
        for (int i = 0; i < size; i++) {
            if (i < value.length()) {
                buffer.putChar(value.charAt(i));
            } else {
                buffer.putChar(' ');
            }
        }
    }

    public int writeToBuffer(ByteBuffer buffer, int pos) {
        int tempPosition;

        // if the tableinfo is variable, we must write data after the offset directory
        if (tabInfo.isVariable()) {
            buffer.position(pos + (recValues.length * 2 * 4));
        } else {
            buffer.position(pos);
        }

        for (int i = 0; i < recValues.length; i++) {
            String value = recValues[i];
            ColInfo col = tabInfo.getColumns()[i];
            // write the start position
            if (tabInfo.isVariable()) {
                tempPosition = buffer.position();
                buffer.putInt(pos + i * 4, tempPosition);
                buffer.position(tempPosition);
            }
            // write the value
            switch (col.getType()) {
                case INT:
                    int intValue = Integer.parseInt(value);
                    buffer.putInt(intValue);
                    break;
                case FLOAT:
                    float floatValue = Float.parseFloat(value);
                    buffer.putFloat(floatValue);
                    break;
                case STRING:
                    putString(buffer, value, col.getT());
                    break;
                case VARSTRING:
                    putString(buffer, value, value.length());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + col.getType());
            }
            // write the end position
            if (tabInfo.isVariable()) {
                tempPosition = buffer.position();
                buffer.putInt(pos + (i + 1) * 4, tempPosition);
                buffer.position(tempPosition);
            }
        }

        return buffer.position() - pos;
    }

    private String getString(ByteBuffer buffer, int size) {
        StringBuffer sb = new StringBuffer(size);
        for(int i = 0; i<size; i++)
            sb.append(buffer.getChar());
        return sb.toString();
    }

    public int readFromBuffer(ByteBuffer buffer, int pos) {
        // if the tableinfo is variable, we must read data after the offset directory
        if (tabInfo.isVariable()) {
            buffer.position(pos + (recValues.length * 2 * 4));
        } else {
            buffer.position(pos);
        }

        for (int i = 0; i < tabInfo.getNumberOfColumns(); i++) {
            ColInfo col = tabInfo.getColumns()[i];

            switch (col.getType()) {
                case INT:
                    int intValue = buffer.getInt();
                    recValues[i] = String.valueOf(intValue);
                    break;
                case FLOAT:
                    float floatValue = buffer.getFloat();
                    recValues[i] = String.valueOf(floatValue);
                    break;
                case STRING:
                    recValues[i] = getString(buffer, col.getT());
                    break;
                case VARSTRING:
                    int tempPosition = buffer.position();
                    int endVarStringPos = buffer.getInt(pos + (i + 1) * 4);
                    int size = (endVarStringPos-tempPosition)/2;
                    buffer.position(tempPosition);
                    recValues[i] = getString(buffer, size);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + col.getType());
            }
        }

        return buffer.position() - pos;
    }

    public int getSize() {
        int size = 0;

        if (tabInfo.isVariable())
            size += recValues.length * 4 * 2;

        for (int i = 0; i < recValues.length; i++) {
            String value = recValues[i];
            DataType colType = tabInfo.getColumns()[i].getType();

            switch (colType) {
                case INT:
                    size += Integer.BYTES;
                    break;
                case FLOAT:
                    size += Float.BYTES;
                    break;
                case STRING:
                    size += Character.BYTES * tabInfo.getColumns()[i].getT();
                    break;
                case VARSTRING:
                    size += Character.BYTES * value.length();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Record: (");
        for (String val : recValues) {
            sb.append(val).append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
