package dev.rdh.png.util;

import dev.rdh.png.chunk.Chunk;
import dev.rdh.png.chunk.End;
import dev.rdh.png.io.ByteInputStream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PNGUtils {
	public static boolean isPNGHeader(byte[] header) {
		return header[0] == (byte) 0x89
			&& header[1] == (byte) 'P'
			&& header[2] == (byte) 'N'
			&& header[3] == (byte) 'G'
			&& header[4] == (byte) '\r'
			&& header[5] == (byte) '\n'
			&& header[6] == (byte) 0x1A
			&& header[7] == (byte) '\n';
	}

	public static Stream<Chunk> streamChunks(ByteInputStream inputStream) {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(new ChunkIterator(inputStream), Spliterator.ORDERED),
			false
		);
	}

	private static final class ChunkIterator implements Iterator<Chunk> {
		private final ByteInputStream in;
		private boolean endChunkFound = false;

		public ChunkIterator(ByteInputStream in) {
			this.in = in;
		}

		@Override
		public boolean hasNext() {
			return !endChunkFound;
		}

		@Override
		public Chunk next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			Chunk chunk = Chunk.fromStream(in);
			if (chunk instanceof End) {
				endChunkFound = true;
			}
			return chunk;
		}
	}

	public static int pack(byte[] data, int offset) {
		return pack(data[offset], data[offset + 1], data[offset + 2], data[offset + 3]);
	}

	public static int pack(byte[] data) {
		return pack(data, 0);
	}

	public static int pack(byte b1, byte b2, byte b3, byte b4) {
		return (b1 & 0xFF) << 24
			| (b2 & 0xFF) << 16
			| (b3 & 0xFF) << 8
			| (b4 & 0xFF);
	}

	public static byte[] unpack(int value) {
		return new byte[] {
			(byte) (value >> 24),
			(byte) (value >> 16),
			(byte) (value >> 8),
			(byte) value
		};
	}
}
