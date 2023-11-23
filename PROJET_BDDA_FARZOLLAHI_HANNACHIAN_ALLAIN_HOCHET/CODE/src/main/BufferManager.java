import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BufferManager {
	private static BufferManager instance = null;
	private Frame[] bufferPool;
	private Map<String, Integer> accessByPages;

	public BufferManager() {
		bufferPool = new Frame[DBParams.FrameCount];
		for (int i = 0; i < DBParams.FrameCount; i++)
			bufferPool[i] = new Frame();
		accessByPages = new HashMap<>();
	}

	// Get the singleton instance of BufferManager
	public static BufferManager getInstance() {
		if (instance == null)
			instance = new BufferManager();
		return instance;
	}

	public void reset() {
		for (int i = 0; i < bufferPool.length; i++)
			bufferPool[i] = new Frame();
		accessByPages.clear();
	}

	// Increment the access count of this pageId
	private void accessPage(PageId pageId) {
		int accessCount = accessByPages.getOrDefault(pageId.toString(), 0);
		accessCount++;
		accessByPages.put(pageId.toString(), accessCount);
	}

	// Return the frame who contains pageIdx
	private Frame findFrame(PageId pageIdx) {
		for (Frame frame : bufferPool) {
			if (frame.getPageId() != null && frame.getPageId().equals(pageIdx)) {
				// System.out.println("Access Count for Page " + pageIdx + ": " +
				// frame.getPageId().getAccessCount());
				return frame;
			}
		}
		System.out.println("Access Count for Page " + pageIdx + " not found!");
		return null;
	}

	// Return the frame we can replace with a new page
	private Frame replaceLFU() {
		Frame res = null;
		for (Frame frame : bufferPool) {
			if (frame.getPageId() == null) {
				res = frame;
				break;
			}
			if (frame.getPinCount() == 0) {
				if (res == null) {
					res = frame;
				} else {
					int resAccessCount = accessByPages.getOrDefault(res.getPageId().toString(), 0);
					int frameAccessCount = accessByPages.getOrDefault(frame.getPageId().toString(), 0);
					if (resAccessCount > frameAccessCount)
						res = frame;
				}
			}
		}
		if (res == null) {
			System.err.println("Out of memory >_< !");
			System.exit(1);
		}
		return res;
	}

	// Get the current page if it's in frames or load it with lfu. Access is stored
	// in frame
	public ByteBuffer getPage(PageId pageId) throws IOException {
		Frame frame = findFrame(pageId);
		if (frame == null) {
			// We must load the page
			frame = replaceLFU();
			flush(frame);
			frame.replacePage(pageId);
			DiskManager.getInstance().ReadPage(pageId, frame.getBuffer());
		}
		frame.incrementPinCount();
		accessPage(pageId);
		return frame.getBuffer();
	}

	// Release access on the page
	public void freePage(PageId pageId, boolean valDirty) throws IOException {
		Frame frame = findFrame(pageId);
		if (frame == null) {
			throw new IllegalArgumentException("This page is not in a frame!");
		}
		frame.decrementPinCount();
		accessPage(pageId);
		if (valDirty)
			frame.setDirty();
	}

	// Write a frame on the disk
	private void flush(Frame frame) throws IOException {
		if (frame.isDirty()) {
			DiskManager.getInstance().WritePage(frame.getPageId(), frame.getBuffer());
		}
		frame.reset();
	}

	// Write all frames buffers on the disk if needed and reset all flags
	public void flushAll() throws IOException {
		for (Frame frame : bufferPool)
			flush(frame);
	}

	public void printBufferPoolStatus(String status) {
		System.out.println("Buffer Pool Status " + status + ":");
		for (int i = 0; i < bufferPool.length; i++) {
			System.out.println("Frame " + i + ": " + bufferPool[i]);
		}
		System.out.println();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("===============\n");
		sb.append("BufferManager\n");
		for (Frame frame : bufferPool) {
			sb.append(frame).append("\n");
		}
		sb.append("Access: { ");
		for (String key : accessByPages.keySet()) {
			sb.append(key).append(": ").append(accessByPages.get(key)).append(", ");
		}
		sb.append("}\n");
		sb.append("===============");

		return sb.toString();
	}
}
