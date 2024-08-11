package dev.rdh.png.util;

import lombok.Getter;

@Getter
public enum BitDepth {
	ONE(1),
	TWO(2),
	FOUR(4),
	EIGHT(8),
	SIXTEEN(16);

	private final byte bytesPerPixel;

	BitDepth(int value) {
		this.bytesPerPixel = (byte) value;
	}

	public static BitDepth fromValue(int value) {
		for (BitDepth bitDepth : BitDepth.values()) {
			if (bitDepth.bytesPerPixel == value) {
				return bitDepth;
			}
		}

		throw new IllegalArgumentException("Invalid bit depth value: " + value);
	}
}
