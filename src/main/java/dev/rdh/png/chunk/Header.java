package dev.rdh.png.chunk;

import lombok.Getter;

import dev.rdh.png.BitDepth;
import dev.rdh.png.ColorType;

@Getter
public class Header extends Chunk {

	private int width, height;

	private BitDepth bitDepth;

	private ColorType colorType;

	public Header(byte[] data) {
		super("IHDR", data);
	}
}
