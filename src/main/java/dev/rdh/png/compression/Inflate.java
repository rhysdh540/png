package dev.rdh.png.compression;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@UtilityClass
public class Inflate {
	public byte[] inflate(byte[] data) {
		Inflater inflater = new Inflater();

		inflater.setInput(data);
		try {
			byte[] output = new byte[data.length * 2];
			int outputLength = inflater.inflate(output);
			inflater.end();

			return Arrays.copyOf(output, outputLength);
		} catch (DataFormatException e) {
			throw new RuntimeException(e);
		}
	}
}
