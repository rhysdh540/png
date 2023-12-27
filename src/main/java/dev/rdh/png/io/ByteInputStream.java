package dev.rdh.png.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteInputStream extends DataInputStream {
	public ByteInputStream(byte[] data) {
		super(new ByteArrayInputStream(data));
	}

	public byte[] read(int length) {
		byte[] bytes = new byte[length];
		try {
			readFully(bytes);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}
}
