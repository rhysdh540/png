package dev.rdh.png.chunk;

import lombok.Getter;

import dev.rdh.png.io.ByteInputStream;
import dev.rdh.png.io.ByteOutputStream;
import dev.rdh.png.util.BitDepth;
import dev.rdh.png.util.ColorType;

import java.io.IOException;
import java.io.UncheckedIOException;

@Getter
public final class Header extends Chunk {
	public static final ChunkType TYPE = new ChunkType("IHDR");

	public static final int HEADER_LENGTH = 13;
	public static final byte COMPRESSION_METHOD = 0;
	public static final byte FILTER_METHOD = 0;

	private int width, height;

	private BitDepth bitDepth;
	private ColorType colorType;

	private boolean interlace;

	@Getter(lombok.AccessLevel.NONE)
	private boolean dataIsUpToDate = true;

	public Header(byte[] data) {
		super(TYPE, data);

		if(data.length != HEADER_LENGTH) {
			throw new IllegalArgumentException("Invalid header length: " + data.length);
		}

		try(ByteInputStream in = new ByteInputStream(data)) {
			setWidth(in.readInt());
			setHeight(in.readInt());
			setBitDepth(BitDepth.fromValue(in.readByte()));
			setColorType(ColorType.fromValue(in.readByte()));

			int temp = in.readByte();
			if(temp != COMPRESSION_METHOD) {
				throw new IllegalArgumentException("Invalid compression method: " + temp);
			}

			temp = in.readByte();
			if(temp != FILTER_METHOD) {
				throw new IllegalArgumentException("Invalid filter method: " + temp);
			}

			temp = in.readByte();
			if(temp != 0 && temp != 1) {
				throw new IllegalArgumentException("Invalid interlace value: " + temp);
			}
			setInterlace(temp == 1);

			temp = in.readInt();
			if(temp != crc()) {
				throw new IllegalArgumentException("Invalid CRC: " + temp);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public Header(int width, int height, BitDepth bitDepth, ColorType colorType, boolean interlace) {
		super(TYPE, createHeaderData(width, height, bitDepth, colorType, interlace));
		this.width = width;
		this.height = height;
		this.bitDepth = bitDepth;
		this.colorType = colorType;
		this.interlace = interlace;
	}

	private void updateData() {
		if(dataIsUpToDate) return;
		this.data = createHeaderData(width, height, bitDepth, colorType, interlace);
		dataIsUpToDate = true;
	}

	private static byte[] createHeaderData(int width, int height, BitDepth bitDepth, ColorType colorType, boolean interlace) {
		try(ByteOutputStream out = new ByteOutputStream(HEADER_LENGTH)) {
			out.writeInt(width);
			out.writeInt(height);
			out.write(bitDepth.getValue());
			out.write(colorType.getValue());
			out.write(COMPRESSION_METHOD);
			out.write(FILTER_METHOD);
			out.writeBoolean(interlace);
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String toString() {
		return "Header{" + width + "x" + height + ", depth=" + bitDepth + ", type=" + colorType + (interlace ? ", interlaced" : "") + "}";
	}

	@Override
	public byte[] getData() {
		updateData();
		return super.getData();
	}

	@Override
	public int crc() {
		updateData();
		return super.crc();
	}

	@Override
	public byte[] toBytes() {
		updateData();
		return super.toBytes();
	}

	public void setWidth(int width) {
		if(width <= 0) {
			throw new IllegalArgumentException("Invalid width: " + width);
		}
		this.width = width;
		dataIsUpToDate = false;
	}

	public void setHeight(int height) {
		if(height <= 0) {
			throw new IllegalArgumentException("Invalid height: " + height);
		}
		this.height = height;
		dataIsUpToDate = false;
	}

	public void setBitDepth(BitDepth bitDepth) {
		if(!colorType.isValidBitDepth(bitDepth)) {
			throw new IllegalArgumentException("Invalid bit depth for color type: " + bitDepth + " for " + colorType);
		}
		this.bitDepth = bitDepth;
		dataIsUpToDate = false;
	}

	public void setColorType(ColorType colorType) {
		if(!colorType.isValidBitDepth(bitDepth)) {
			throw new IllegalArgumentException("Invalid bit depth for color type: " + bitDepth + " for " + colorType);
		}
		this.colorType = colorType;
		dataIsUpToDate = false;
	}

	public void setInterlace(boolean interlace) {
		this.interlace = interlace;
		dataIsUpToDate = false;
	}
}
