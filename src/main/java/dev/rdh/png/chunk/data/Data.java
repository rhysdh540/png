package dev.rdh.png.chunk.data;

import lombok.Getter;
import lombok.Setter;

import dev.rdh.png.chunk.Chunk;

import java.util.List;

@Getter
@Setter
public class Data extends Chunk {

	private List<Line> lines;

	public Data(byte[] data) {
		super("IDAT", data);
	}

	@Override
	public byte[] getData() {
		throw new UnsupportedOperationException();
	}
}
