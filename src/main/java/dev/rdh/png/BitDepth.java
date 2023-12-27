package dev.rdh.png;

import lombok.Getter;

@Getter
public enum BitDepth {
	ONE(1),
	TWO(2),
	FOUR(4),
	EIGHT(8),
	SIXTEEN(16);

	private final int value;

	BitDepth(int value) {
		this.value = value;
	}

	public static BitDepth fromValue(int value) {
		for (BitDepth bitDepth : values()) {
			if (bitDepth.value == value) {
				return bitDepth;
			}
		}

		throw new IllegalArgumentException("Invalid bit depth value: " + value);
	}
}
