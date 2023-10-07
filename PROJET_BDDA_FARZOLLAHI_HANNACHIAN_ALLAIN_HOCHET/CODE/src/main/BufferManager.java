package main;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferManager {
	private static BufferManager instance = null;
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
			if (frame.getPageId() != null && frame.getPageId().equals(pageIdx)) {
				System.out.println("Access Count for Page " + pageIdx + ": " + frame.getPageId().getAccessCount());
				return frame;
			}
		}
		System.out.println("Access Count for Page " + pageIdx + " not found!");
		return new Frame();
	}

	public Frame ReplaceLFU(PageId pageId) throws IOException {
		Frame frameToReplace = null;
		int minRefCount = Integer.MAX_VALUE;

		for (Frame frame : bufferPool) {
			if (frame.getPageId() != null && frame.getPageId().getAccessCount() < minRefCount
					&& frame.getPinCount() == 0) {
				minRefCount = frame.getPageId().getAccessCount();
				frameToReplace = frame;
			}
		}

		System.out.println("Frame to Replace: " + (frameToReplace != null ? frameToReplace.toString() : "None"));
		printBufferPoolStatus("Before LFU Replacement");

		// If all frames are pinned, handle this situation appropriately
		if (frameToReplace == null) {
			return new Frame();
		}

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

		printBufferPoolStatus("After LFU Replacement");

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
				DiskManager.getInstance().WritePage(pageId, buffer);
			}
			fr.reset();
		}
	}

	public void printBufferPoolStatus(String status) {
		System.out.println("Buffer Pool Status " + status + ":");
		for (int i = 0; i < bufferPool.length; i++) {
			System.out.println("Frame " + i + ": " + bufferPool[i]);
		}
		System.out.println();
	}

	public void setBufferPool(Frame[] bufferPool) {
		this.bufferPool = bufferPool;
	}
}
