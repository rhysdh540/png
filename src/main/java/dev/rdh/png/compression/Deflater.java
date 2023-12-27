package dev.rdh.png.compression;

public interface Deflater {
	byte[] deflate(byte[] data);

	void setCompressionLevel(int level);

	static Deflater newInstance() {
		return new JDKDeflater();
	}
}
