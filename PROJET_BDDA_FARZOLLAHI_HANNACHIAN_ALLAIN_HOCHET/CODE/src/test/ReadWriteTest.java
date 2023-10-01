package test;

import org.junit.jupiter.api.Test;

import main.PageId;
import main.DBParams;
import main.DiskManager;


import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class ReadWriteTest {
    DiskManager diskManager;

    @Test
    public void testLectureEcriturePage() throws IOException {
        setupDiskManager();

        testAvecTamponVide();
        testAvecValeursMaximales();
        testAvecValeursMinimales();
        testEcritureEtLecturePhrase();
    }

    private void setupDiskManager() throws IOException {
        String cheminDB = "../DB";
        DBParams.DBPath = cheminDB;
        DBParams.DMFileCount = 4;
        DBParams.SGBDPageSize = 4096;
        diskManager = DiskManager.getInstance();
    }

    private void testAvecTamponVide() {
        try {
            PageId pageIdTamponVide = diskManager.AllocPage();
            ByteBuffer tamponVide = ByteBuffer.allocate(DBParams.SGBDPageSize);
            diskManager.WritePage(pageIdTamponVide, tamponVide);
            ByteBuffer tamponLuVide = ByteBuffer.allocate(DBParams.SGBDPageSize);
            diskManager.ReadPage(pageIdTamponVide, tamponLuVide);
            assertArrayEquals(tamponVide.array(), tamponLuVide.array());
        } catch (IOException e) {
            fail("Exception levée : " + e.getMessage());
        }
    }

    private void testAvecValeursMaximales() {
        testAvecValeurs(Integer.MAX_VALUE);
    }

    private void testAvecValeursMinimales() {
        testAvecValeurs(Integer.MIN_VALUE);
    }

    private void testAvecValeurs(int valeur) {
        try {
            PageId pageId = diskManager.AllocPage();
            ByteBuffer tampon = ByteBuffer.allocate(DBParams.SGBDPageSize);
            tampon.putInt(valeur);
            tampon.flip();
            diskManager.WritePage(pageId, tampon);

            ByteBuffer tamponLu = ByteBuffer.allocate(DBParams.SGBDPageSize);
            diskManager.ReadPage(pageId, tamponLu);
            assertEquals(valeur, tamponLu.getInt());
        } catch (IOException e) {
            fail("Exception levée : " + e.getMessage());
        }
    }

    private void testEcritureEtLecturePhrase() {
        try {
            String phrase = "We are the best, We are BDDA";
            PageId pageId = diskManager.AllocPage();
            ByteBuffer tamponPhrase = ByteBuffer.allocate(DBParams.SGBDPageSize);
            tamponPhrase.put(phrase.getBytes());
            tamponPhrase.flip();
            diskManager.WritePage(pageId, tamponPhrase);

            ByteBuffer tamponLuPhrase = ByteBuffer.allocate(DBParams.SGBDPageSize);
            diskManager.ReadPage(pageId, tamponLuPhrase);
            String phraseLue = new String(tamponLuPhrase.array()).trim();
            assertEquals(phrase, phraseLue);
        } catch (IOException e) {
            fail("Exception levée : " + e.getMessage());
        }
    }
}
