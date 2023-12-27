package dev.rdh.png.compression;

import java.util.Arrays;

public class JDKDeflater implements Deflater {

	private final java.util.zip.Deflater deflater;

	public JDKDeflater() {
		this.deflater = new java.util.zip.Deflater();
	}

	@Override
	public byte[] deflate(byte[] data) {
		while(!deflater.finished());
		deflater.reset();

		byte[] output = new byte[data.length];

		deflater.setInput(data);
		int len = deflater.deflate(output);
		deflater.end();
		return Arrays.copyOf(output, len);
	}

	@Override
	public void setCompressionLevel(int level) {
		deflater.setLevel(level);
	}
}
