package dev.rdh.png.image;

import dev.rdh.png.chunk.Chunk;
import dev.rdh.png.chunk.Data;
import dev.rdh.png.chunk.Header;
import dev.rdh.png.io.ByteInputStream;
import dev.rdh.png.util.PNGUtils;

import java.io.IOException;
import java.nio.IntBuffer;

public class PNGImage implements Image {
	private final int width;
	private final int height;
	private final IntBuffer imageData; // inflated and unfiltered

	public PNGImage(ByteInputStream inputStream) throws IOException {
		// Read the PNG header
		byte[] header = new byte[8];
		inputStream.read(header);
		if (!PNGUtils.isPNGHeader(header)) {
			throw new IllegalArgumentException("Not a PNG file");
		}

		// Read the IHDR chunk
		Chunk firstChunk = Chunk.fromStream(inputStream);
		if(!(firstChunk instanceof Header ihdr)) {
			throw new IllegalArgumentException("First chunk is not IHDR, but " + firstChunk.getType());
		}

		// Read the image dimensions
		width = ihdr.getWidth();
		height = ihdr.getHeight();

		// Read the image data
		imageData = IntBuffer.allocate(width * height * 4);
		PNGUtils.streamChunks(inputStream)
				.filter(chunk -> "IDAT".equals(chunk.getType().toString()))
				.forEach(chunk -> {
					Data idat = new Data(chunk.getData());
					idat.getScanlines(ihdr)
							.map(scanline -> ihdr.getColorType().convertToRGBA(scanline, ihdr.getBitDepth()))
							.forEach(imageData::put);
				});
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getPixel(int x, int y) {
		return imageData.get(y * width + x);
	}

	@Override
	public void setPixel(int x, int y, int color) {
		imageData.put(y * width + x, color);
	}
}
