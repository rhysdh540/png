package dev.rdh.png.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteOutputStream extends DataOutputStream {
	private final ByteArrayOutputStream baos;

	public ByteOutputStream() {
		this(32);
	}

	public ByteOutputStream(int size) {
		super(null);
		this.baos = new ByteArrayOutputStream(size);
		this.out = new BufferedOutputStream(baos, size);
	}

	public byte[] toByteArray() throws IOException {
		flush();
		return baos.toByteArray();
	}

	@Override
	public void close() throws IOException {
		super.close();
		baos.close();
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		baos.flush();
	}
}
