package dev.rdh.png.chunk;

public final class End extends Chunk {
	public static final End INSTANCE = new End();

	private End() {
		super("IEND", new byte[0]);
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
}
