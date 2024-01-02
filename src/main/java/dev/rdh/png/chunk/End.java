package dev.rdh.png.chunk;

public final class End extends Chunk {
	public static final ChunkType TYPE = new ChunkType("IEND");
	private static final byte[] EMPTY_DATA = new byte[0];

	public End() {
		super(TYPE, EMPTY_DATA);
	}

	public End(byte[] data) {
		this();
		if(data.length != 0) {
			throw new IllegalArgumentException("Invalid IEND chunk data length: " + data.length);
		}
	}

	@Override
	public byte[] getData() {
		return EMPTY_DATA;
	}

	@Override
	public byte[] toBytes() {
		return new byte[] {
				0x00, 0x00, 0x00, 0x00, // length
				0x49, 0x45, 0x4E, 0x44, // type
				// no data
				(byte) 0xAE, 0x42, 0x60, (byte) 0x82, // crc
		};
	}

	@Override
	public int crc() {
		return 0xAE_42_60_82;
	}

	@Override
	public String toString() {
		return "IEND";
	}
}
