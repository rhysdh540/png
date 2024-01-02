package dev.rdh.png.util;

import lombok.Getter;

import java.util.Arrays;

import static dev.rdh.png.util.BitDepth.*;

public enum ColorType {
	GRAYSCALE(0, ONE, TWO, FOUR, EIGHT, SIXTEEN),
	RGB(2, EIGHT, SIXTEEN),
	INDEXED(3, ONE, TWO, FOUR, EIGHT),
	GRAYSCALE_ALPHA(4, EIGHT, SIXTEEN),
	RGB_ALPHA(6, EIGHT, SIXTEEN);

	@Getter
	private final int value;

	private final BitDepth[] validBitDepths;

	ColorType(int value, BitDepth... validBitDepths) {
		this.value = value;
		this.validBitDepths = validBitDepths;
	}

	public boolean isValidBitDepth(BitDepth bitDepth) {
		return Arrays.binarySearch(validBitDepths, bitDepth) >= 0;
	}

	public static ColorType fromValue(int value) {
		for (ColorType colorType : ColorType.values()) {
			if (colorType.value == value) {
				return colorType;
			}
		}

		throw new IllegalArgumentException("Invalid color type value: " + value);
	}
}
