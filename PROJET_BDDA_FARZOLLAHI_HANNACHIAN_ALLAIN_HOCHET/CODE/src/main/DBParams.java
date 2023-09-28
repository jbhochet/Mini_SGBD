package main;
public class DBParams {
    private static String DBPath;
    private static int SGBDPageSize;
    private static int DMFileCount;

    public static String getDBPath(){
        return DBPath;
    }

    public static void setDBPath(String path){
        DBPath=path;
    }

    public static int getSGBDPageSize(){
        return SGBDPageSize;
    }

    public static void setSGBDPageSize(int size){
        SGBDPageSize=size;
    }

    public static int getDMFileCount(){
        return DMFileCount;
    }

    public static void setDMFileCount(int count){
        DMFileCount=count;
    }

}