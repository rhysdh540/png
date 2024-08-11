package dev.rdh.png.chunk;

import lombok.Getter;

import dev.rdh.png.io.ByteInputStream;
import dev.rdh.png.io.ByteOutputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Chunk {
	@Getter
	protected final ChunkType type;
	protected byte[] data;

	protected final CRC32 crc = new CRC32();

	public Chunk(ChunkType type, byte[] data) {
		this.type = type;
		this.data = data;
	}

	public Chunk(String type, byte[] data) {
		this(new ChunkType(type), data);
	}

	public static Chunk fromStream(ByteInputStream stream) {
		try {
			int length = stream.readInt();
			String type = stream.readString(4);
			byte[] data = stream.readNBytes(length);
			int crc = stream.readInt();

			Chunk chunk = new Chunk(type, data);
			if(chunk.crc() != crc) {
				throw new IOException("CRC mismatch");
			}

			if(chunk.getType().isPublic()) {
				switch(chunk.getType().toString()) {
					case "IHDR": return new Header(data);
					case "IDAT": return new Data(data);
					case "IEND": return new End();
				}
			}

			return chunk;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public int length() {
		return data.length;
	}

	public int crc() {
		synchronized(crc) {
			crc.reset();
			crc.update(type.toBytes());
			crc.update(data);
			return (int) crc.getValue();
		}
	}

	@Override
	public String toString() {
		return type + "{length=" + data.length + ", crc=" + crc() + "}";
	}

	public byte[] getData() {
		return Arrays.copyOf(data, data.length);
	}

	public byte[] toBytes() {
		try(ByteOutputStream out = new ByteOutputStream(12 + data.length)) {
			out.writeInt(data.length);
			out.write(type.toBytes());
			out.write(data);
			out.writeInt(crc());
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Getter
	public static class ChunkType {

		@Getter(lombok.AccessLevel.NONE)
		private final char[] type;

		private final boolean critical;
		private final boolean isPublic;
		private final boolean reserved;
		private final boolean safeToCopy;

		public ChunkType(String type) {
			this(type.toCharArray());
		}

		public ChunkType(char[] type) {
			if(type.length != 4) {
				throw new IllegalArgumentException("Chunk type must be 4 characters long");
			}

			for(int i = 0; i < 4; i++) {
				if(type[i] > 127) {
					throw new IllegalArgumentException("Found non-US-ASCII character in chunk type " + new String(type) + " at index " + i);
				}
			}

			this.type = type;
			this.critical = Character.isUpperCase(type[0]);
			this.isPublic = Character.isUpperCase(type[1]);
			this.reserved = Character.isUpperCase(type[2]);
			this.safeToCopy = Character.isLowerCase(type[3]);
		}

		@Override
		public String toString() {
			return new String(type);
		}

		public byte[] toBytes() {
			byte[] bytes = new byte[4];
			for(int i = 0; i < 4; i++) {
				bytes[i] = (byte) type[i];
			}
			return bytes;
		}
	}
}
