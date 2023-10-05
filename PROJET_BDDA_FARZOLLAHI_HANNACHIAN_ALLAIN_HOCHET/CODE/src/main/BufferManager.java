package main;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferManager {
	private static BufferManager instance = null; // Une seule instance de la classe est créée
	private Frame[] bufferPool;

	public BufferManager() {
		bufferPool = new Frame[DBParams.FrameCount];
		for (int i = 0; i < DBParams.FrameCount; i++)
			bufferPool[i] = new Frame();
	}

	public static BufferManager getInstance() {
		if (instance == null)
			instance = new BufferManager();
		return instance;
	}

	public Frame FindFrame(PageId pageIdx) {
		for (Frame frame : bufferPool) {
			if (frame.getPageId() != null && frame.getPageId().equals(pageIdx)) // Si la frame contient la page qu'on
																					// recherche
				return frame;
		}
		return null; // Retourne null si la page n'est pas trouvée dans le pool
	}


	public Frame ReplaceLFU(PageId pageId) throws IOException {
	    Frame frameToReplace = null;
	    int minRefCount = Integer.MAX_VALUE;

	    for (Frame frame : bufferPool) {
	        if (frame.getPageId().getAccessCount() < minRefCount && frame.getPinCount() == 0) {
	            minRefCount = frame.getPageId().getAccessCount();
	            frameToReplace = frame;
	        }
	    }

	    if (frameToReplace != null) {
	        if (frameToReplace.getDirty()) {
	            DiskManager.getInstance().WritePage(frameToReplace.getPageId(), frameToReplace.getBuffer());
	        }

	        DiskManager diskManager = DiskManager.getInstance();
	        PageId newPageId = pageId; 
	        ByteBuffer newBuffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
	        diskManager.ReadPage(newPageId, newBuffer);

	        frameToReplace.replacePage(newPageId);
	        frameToReplace.setBuffer(newBuffer);
	        frameToReplace.setDirty(); 
	    }

	    return frameToReplace;
	}
	
	public void freePage(PageId pageId, boolean valDirty) throws IOException {
	    Frame frame = FindFrame(pageId);

	    if (frame == null) {
	        throw new IllegalArgumentException("Error");
	    }
	    frame.decrementPinCount();
	    if (valDirty)
	        frame.setDirty();
	}


	public void FlushAll() throws IOException {
		for (Frame fr : bufferPool) {
			if (fr.getDirty()) {
				PageId pageId = fr.getPageId();
				ByteBuffer buffer = fr.getBuffer();
				DiskManager.getInstance().WritePage(pageId, buffer); // l’écriture de toutes les pages dont le flag dirty = 1 (true) sur disque
			}
			fr.reset(); // la remise à 0 de tous les flags/informations et contenus des buffers (buffer pool « vide »)
		}
	}

}