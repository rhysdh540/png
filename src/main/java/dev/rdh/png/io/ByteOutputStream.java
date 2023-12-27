package dev.rdh.png.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteOutputStream extends DataOutputStream {
	public ByteOutputStream() {
		super(new ByteArrayOutputStream());
	}

	public byte[] toByteArray() {
		return ((ByteArrayOutputStream) out).toByteArray();
	}
}
