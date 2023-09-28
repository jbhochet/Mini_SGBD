package main;

public class Main {
    
    public static void main(String[] args) {
        DBParams.DBPath = (args.length == 0) ? "../DB" : args[0];
        DBParams.SGBDPageSize = 4096;
        DBParams.DMFileCount = 5;

        DiskManager diskManager = DiskManager.getInstance();

        for(int i = 0; i<10000; i++) {
            diskManager.AllocPage();
        }
        System.out.println(diskManager.GetCurrentCountAllocPages());
    }
}