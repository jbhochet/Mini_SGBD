package main;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
            case "VARSTRING":
                buffer.put(value.getBytes(StandardCharsets.UTF_8));
                break;
            default:
                throw new IllegalArgumentException("Type de colonne non pris en charge : " + colType);
        }
    }

        buffer.position(originalPos);
        return buffer.position() - pos;
    }
    
    public void readFromBuffer(ByteBuffer buffer, int pos) {
        int originalPos = buffer.position();
        buffer.position(pos);

        recValues.clear(); // Vide la liste avant de la remplir avec les nouvelles valeurs

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
                case "VARSTRING":
                    byte[] stringBytes = new byte[buffer.getInt()];
                    buffer.get(stringBytes);
                    recValues.add(new String(stringBytes, StandardCharsets.UTF_8));
                    break;
                default:
                    throw new IllegalArgumentException("Type de colonne non pris en charge : " + colType);
            }
        }

        buffer.position(originalPos);
    }
        

}

/*   // Méthode pour lire les valeurs du Record depuis le buffer
    public void readFromBuffer(ByteBuffer buff, int pos) {
        
    }
}*/
