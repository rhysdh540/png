package dev.rdh.png.chunk;

import dev.rdh.png.io.ByteInputStream;
import dev.rdh.png.util.LineFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.InflaterInputStream;

public class Data extends Chunk {
	public static final ChunkType TYPE = new ChunkType("IDAT");

	public Data(byte[] data) {
		super(TYPE, data);
	}

	public Data(int length) {
		super(TYPE, new byte[length]);
	}

	public Stream<byte[]> getScanlines(Header ihdr) {
		InputStream in = new InflaterInputStream(new ByteInputStream(data));
		Iterator<byte[]> iterator = new ScanlineIterator(in, ihdr.getWidth(), ihdr.getHeight(), ihdr.getBitDepth());
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
	}


	private static final class ScanlineIterator implements Iterator<byte[]> {
		private final InputStream in;
		private final int width;
		private final int bytesPerPixel;

		private int remainingLines;
		private byte[] previousScanline;

		public ScanlineIterator(InputStream in, Header ihdr) {
			this.in = in;
			this.width = ihdr.getWidth();
			this.remainingLines = ihdr.getHeight();
			this.bytesPerPixel = ihdr.getBitDepth().getBytesPerPixel();
			this.previousScanline = null;
		}

		@Override
		public boolean hasNext() {
			return remainingLines > 0;
		}

		@Override
		public byte[] next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			try {
				int filterType = in.read();
				if (filterType == -1) {
					throw new IOException("Unexpected end of data");
				}
				LineFilter lineFilter = LineFilter.fromValue(filterType);
				byte[] scanline = in.readNBytes(width * bytesPerPixel);
				if (scanline.length < width * bytesPerPixel) {
					throw new IOException("Incomplete scanline data");
				}
				byte[] unfilteredLine = lineFilter.unfilter(scanline, previousScanline, bytesPerPixel);
				previousScanline = unfilteredLine;
				remainingLines--;
				return unfilteredLine;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
