package main;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Record {
    private TableInfo tabInfo;
    private List<String> recValues;

    public Record(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
        this.recValues = new ArrayList<>();
    }

    public TableInfo getTabInfo() {
        return tabInfo;
    }

    public void setTabInfo(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
    }

    public List<String> getRecValues() {
        return recValues;
    }

    public void setRecValues(List<String> recValues) {
        this.recValues = recValues;
    }
public int writeToBuffer(ByteBuffer buffer, int pos) {
        int originalPos = buffer.position();
        buffer.position(pos);

        for (int i = 0; i < recValues.size(); i++) {
            String value = recValues.get(i);
            String colType = tabInfo.getColumns().get(i).getColType();

            switch (colType) {
                case "INT":
                    int intValue = Integer.parseInt(value);
                    buffer.putInt(intValue);
                    break;
                case "FLOAT":
                    float floatValue = Float.parseFloat(value);
                    buffer.putFloat(floatValue);
                    break;
                case "STRING":
                    writeFixedString(buffer, value, tabInfo.getColumns().get(i).getT());
                    break;
                case "VARSTRING":
                    writeVariableString(buffer, value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        buffer.position(originalPos);
        return buffer.position() - pos;
    }

    private void writeFixedString(ByteBuffer buffer, String value, int maxLength) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.put(Arrays.copyOf(stringBytes, maxLength));
    }

    private void writeVariableString(ByteBuffer buffer, String value) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(stringBytes.length);
        buffer.put(stringBytes);
    }

    public int readFromBuffer(ByteBuffer buffer, int pos) {
        int originalPos = buffer.position();
        buffer.position(pos);

        recValues.clear();

        for (int i = 0; i < tabInfo.getNumberOfColumns(); i++) {
            String colType = tabInfo.getColumns().get(i).getColType();

            switch (colType) {
                case "INT":
                    int intValue = buffer.getInt();
                    recValues.add(String.valueOf(intValue));
                    break;
                case "FLOAT":
                    float floatValue = buffer.getFloat();
                    recValues.add(String.valueOf(floatValue));
                    break;
                case "STRING":
                    recValues.add(readFixedString(buffer, tabInfo.getColumns().get(i).getT()));
                    break;
                case "VARSTRING":
                    recValues.add(readVariableString(buffer));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported column type: " + colType);
            }
        }

        buffer.position(originalPos);
        return buffer.position() - pos;
    }

    private String readFixedString(ByteBuffer buffer, int maxLength) {
        byte[] stringBytes = new byte[maxLength];
        buffer.get(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8).trim();
    }

    private String readVariableString(ByteBuffer buffer) {
        int stringLength = buffer.getInt();
        byte[] stringBytes = new byte[stringLength];
        buffer.get(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

        

}

