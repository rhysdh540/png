package dev.rdh.png;

import lombok.Getter;

@Getter
public enum ColorType {
	GRAYSCALE(0),
	RGB(2),
	INDEXED(3),
	GRAYSCALE_ALPHA(4),
	RGB_ALPHA(6);

	private final int value;

	ColorType(int value) {
		this.value = value;
	}

	public static ColorType fromValue(int value) {
		for (ColorType colorType : values()) {
			if (colorType.value == value) {
				return colorType;
			}
		}

		throw new IllegalArgumentException("Invalid color type value: " + value);
	}

	public int getBytesPerPixel(BitDepth bitDepth) {
		return switch(this) {
			case GRAYSCALE -> bitDepth.getValue();
			case RGB -> bitDepth.getValue() * 3;
			case INDEXED -> 1;
			case GRAYSCALE_ALPHA -> bitDepth.getValue() * 2;
			case RGB_ALPHA -> bitDepth.getValue() * 4;
		};
	}
}
