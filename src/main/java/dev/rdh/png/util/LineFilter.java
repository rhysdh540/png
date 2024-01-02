package dev.rdh.png.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.function.BinaryOperator;

@Getter
public enum LineFilter {
	NONE(0) {
		@Override
		protected byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator) {
			return Arrays.copyOf(scanline, scanline.length);
		}
	},
	SUB(1) {
		@Override
		protected byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator) {
			int len = scanline.length;
			byte[] newLine = new byte[len];
			for(int i = 0; i < len; i++) {
				byte left = i < bytesPerPixel ? 0 : scanline[i - bytesPerPixel];
				newLine[i] = operator.apply(scanline[i], left);
			}
			return newLine;
		}
	},
	UP(2) {
		@Override
		protected byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator) {
			int len = scanline.length;
			byte[] newLine = new byte[len];

			if(previousScanline == null) {
				System.arraycopy(scanline, 0, newLine, 0, scanline.length);
				return newLine;
			}

			for(int i = 0; i < len; i++) {
				newLine[i] = operator.apply(scanline[i], previousScanline[i]);
			}
			return newLine;
		}
	},
	AVERAGE(3) {
		@Override
		protected byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator) {
			int len = scanline.length;
			byte[] newLine = new byte[len];

			if(previousScanline == null) {
				previousScanline = new byte[len];
			}

			for(int i = 0; i < len; i++) {
				byte left = i < bytesPerPixel ? 0 : scanline[i - bytesPerPixel];
				byte up = previousScanline[i];
				newLine[i] = operator.apply(scanline[i], (byte) ((left + up) / 2));
			}
			return newLine;
		}
	},
	PAETH(4) {
		@Override
		protected byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator) {
			int len = scanline.length;
			byte[] newLine = new byte[len];

			for (int i = 0; i < scanline.length; i++) {
				byte left = i < bytesPerPixel ? 0 : scanline[i - bytesPerPixel];
				byte up = previousScanline == null ? 0 : previousScanline[i];
				byte upLeft = i < bytesPerPixel || previousScanline == null ? 0 : previousScanline[i - bytesPerPixel];
				newLine[i] = operator.apply(scanline[i], paethPredictor(left, up, upLeft));
			}

			return newLine;
		}

		private byte paethPredictor(byte left, byte up, byte upLeft) {
			int p = left + up - upLeft;
			int pLeft = Math.abs(p - left);
			int pUp = Math.abs(p - up);
			int pUpLeft = Math.abs(p - upLeft);

			if (pLeft <= pUp && pLeft <= pUpLeft) {
				return left;
			} else if (pUp <= pUpLeft) {
				return up;
			} else {
				return upLeft;
			}
		}
	};

	private final int value;

	LineFilter(int value) {
		this.value = value;
	}

	public static LineFilter fromValue(int value) {
		for (LineFilter lineFilter : LineFilter.values()) {
			if (lineFilter.getValue() == value) {
				return lineFilter;
			}
		}

		throw new IllegalArgumentException("Invalid line filter value: " + value);
	}

	public byte[] filter(byte[] scanline, byte[] previousScanline, int bytesPerPixel) {
		return logic(scanline, previousScanline, bytesPerPixel, (a, b) -> (byte) (a - b));
	}

	public byte[] unfilter(byte[] scanline, byte[] previousScanline, int bytesPerPixel) {
		return logic(scanline, previousScanline, bytesPerPixel,  (a, b) -> (byte) (a + b));
	}

	protected abstract byte[] logic(byte[] scanline, byte[] previousScanline, int bytesPerPixel, BinaryOperator<Byte> operator);
}
