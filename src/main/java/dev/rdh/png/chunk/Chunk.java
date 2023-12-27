package dev.rdh.png.chunk;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.zip.CRC32;

import dev.rdh.png.io.ByteOutputStream;

public class Chunk {
	public final String type;

	//excludes length, type and CRC
	@Getter
	protected byte[] data;

	public Chunk(String type, byte[] data) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(data);
		if(type.length() != 4) {
			throw new IllegalArgumentException("Chunk type must be 4 characters long");
		}
		this.type = type;
		this.data = data;
	}

	public byte[] toBytes() {
		try(ByteOutputStream out = new ByteOutputStream()) {
			out.writeInt(data.length);
			out.writeBytes(type);
			out.write(data);
			out.writeInt(crc());
			return out.toByteArray();
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public boolean isCritical() {
		return Character.isUpperCase(type.charAt(0));
	}

	public boolean isPublic() {
		return Character.isUpperCase(type.charAt(1));
	}

	public boolean isReserved() {
		return Character.isUpperCase(type.charAt(2));
	}

	public boolean isSafeToCopy() {
		return Character.isLowerCase(type.charAt(3));
	}

	/**
	 * Calculates the 32-bit Cyclical Redundancy Check (CRC) for the chunk
	 * @return the CRC
	 */
	public int crc() {
		CRC32 crc = new CRC32();
		crc.update(type.getBytes());
		crc.update(data);
		return (int) crc.getValue();
	}
}
