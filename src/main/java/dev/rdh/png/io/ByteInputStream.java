package dev.rdh.png.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ByteInputStream extends DataInputStream {
	public ByteInputStream(byte[] data) {
		super(new BufferedInputStream(new ByteArrayInputStream(data)));
	}

	public String readString(int length) throws IOException {
		byte[] bytes = new byte[length];
		readFully(bytes);
		return new String(bytes);
	}
}
