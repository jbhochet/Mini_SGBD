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

    // Méthode pour écrire les valeurs du Record dans le buffer
    public int writeToBuffer(ByteBuffer buff, int pos) {
        

    }

    // Méthode pour lire les valeurs du Record depuis le buffer
    public void readFromBuffer(ByteBuffer buff, int pos) {
        
    }
}
