package dev.rdh.png.util;

import lombok.Getter;

import java.util.Arrays;

import static dev.rdh.png.util.BitDepth.*;

public enum ColorType {
	GRAYSCALE(0, ONE, TWO, FOUR, EIGHT, SIXTEEN) {
		@Override
		public int[] convertToRGBA(byte[] scanline, BitDepth bitDepth) {
			int[] rgba = new int[scanline.length / bitDepth.getBytesPerPixel()];
			int index = 0;

			for (int i = 0; i < scanline.length; i += bitDepth.getBytesPerPixel()) {
				byte value = 0;
				for (int j = 0; j < bitDepth.getBytesPerPixel(); j++) {
					value |= (byte) ((scanline[i + j] & 0xFF) << (bitDepth.getBytesPerPixel() - 1 - j));
				}

				rgba[index++] = PNGUtils.pack(value, value, value, (byte) 0xFF);
			}

			return rgba;
		}
	},
	RGB(2, EIGHT, SIXTEEN) {
		@Override
		public int[] convertToRGBA(byte[] scanline, BitDepth bitDepth) {
			byte bytesPerPixel = bitDepth.getBytesPerPixel();

			int[] rgba = new int[scanline.length / bytesPerPixel];
			int index = 0;

			for (int i = 0; i < scanline.length; i += bytesPerPixel) {
				byte r = 0;
				byte g = 0;
				byte b = 0;

				for (int j = 0; j < bytesPerPixel; j++) {
					byte offset = (byte) (bytesPerPixel - 1 - j);

					r |= (byte) ((scanline[i + j] & 0xFF) << offset);
					g |= (byte) ((scanline[i + bytesPerPixel + j] & 0xFF) << offset);
					b |= (byte) ((scanline[i + 2 * bytesPerPixel + j] & 0xFF) << offset);
				}

				rgba[index++] = PNGUtils.pack(r, g, b, (byte) 0xFF);
			}

			return rgba;
		}
	},
	INDEXED(3, ONE, TWO, FOUR, EIGHT) {
		@Override
		public int[] convertToRGBA(byte[] scanline, BitDepth bitDepth) {
			throw new UnsupportedOperationException("Indexed color type not supported yet");
		}
	},
	GRAYSCALE_ALPHA(4, EIGHT, SIXTEEN) {
		@Override
		public int[] convertToRGBA(byte[] scanline, BitDepth bitDepth) {
			int[] rgba = new int[scanline.length / bitDepth.getBytesPerPixel()];
			int index = 0;

			for (int i = 0; i < scanline.length; i += bitDepth.getBytesPerPixel()) {
				int value = 0;
				for (int j = 0; j < bitDepth.getBytesPerPixel(); j++) {
					value |= (scanline[i + j] & 0xFF) << (bitDepth.getBytesPerPixel() - 1 - j);
				}

				rgba[index++] = value;
			}

			return rgba;
		}
	},
	RGB_ALPHA(6, EIGHT, SIXTEEN) {
		@Override
		public int[] convertToRGBA(byte[] scanline, BitDepth bitDepth) {
			byte bytesPerPixel = bitDepth.getBytesPerPixel();

			int[] rgba = new int[scanline.length / bytesPerPixel];
			int index = 0;

			for (int i = 0; i < scanline.length; i += bytesPerPixel) {
				byte r = 0;
				byte g = 0;
				byte b = 0;
				byte a = 0;

				for (int j = 0; j < bytesPerPixel; j++) {
					byte offset = (byte) (bytesPerPixel - 1 - j);

					r |= (byte) ((scanline[i + j] & 0xFF) << offset);
					g |= (byte) ((scanline[i + bytesPerPixel + j] & 0xFF) << offset);
					b |= (byte) ((scanline[i + 2 * bytesPerPixel + j] & 0xFF) << offset);
					a |= (byte) ((scanline[i + 3 * bytesPerPixel + j] & 0xFF) << offset);
				}

				rgba[index++] = PNGUtils.pack(r, g, b, a);
			}

			return rgba;
		}
	};

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

	public abstract int[] convertToRGBA(byte[] scanline, BitDepth bitDepth);
}
