package dev.rdh.png;

import lombok.Getter;

import dev.rdh.png.chunk.Chunk;
import dev.rdh.png.io.ByteInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public class PngImage {
	public static final List<Byte> MAGIC_NUMBER = List.of(new Byte[]{ (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A });
	public static final int CRC_LENGTH = 4;

	private final List<Chunk> chunks;

	public PngImage(List<Chunk> chunks) {
		Objects.requireNonNull(chunks);
		verify(chunks);

		this.chunks = new ArrayList<>(chunks);
	}

	public PngImage(Chunk... chunks) {
		this(Arrays.asList(chunks));
	}

	public PngImage(byte[] input) {
		List<Chunk> chunks = new ArrayList<>();

		verifyMagicNumber(input);

		try(ByteInputStream stream = new ByteInputStream(input)) {
			stream.skip(MAGIC_NUMBER.size());

			while(stream.available() > 0) {
				int length = stream.readInt();

				String name = new String(stream.readNBytes(4));

				byte[] data = stream.readNBytes(length);

				int crc = stream.readInt();

				Chunk chunk = new Chunk(name, data);
				int expectedCrc = chunk.crc();

				if(crc != expectedCrc) {
					throw new IllegalArgumentException("CRC does not match for chunk " + name);
				}

				chunks.add(chunk);
			}

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		verify(chunks);

		this.chunks = chunks;
	}

	public PngImage(File file) throws IOException {
		this(file.toPath());
	}

	public PngImage(Path path) throws IOException {
		this(Files.readAllBytes(path));
	}

	protected static void verifyMagicNumber(byte[] data) {
		byte[] possibleMagicNumber = Arrays.copyOfRange(data, 0, MAGIC_NUMBER.size());
		byte[] actualMagicNumber = new byte[MAGIC_NUMBER.size()];
		for(int i = 0; i < MAGIC_NUMBER.size(); i++) {
			actualMagicNumber[i] = MAGIC_NUMBER.get(i);
		}

		if(!Arrays.equals(possibleMagicNumber, actualMagicNumber)) {
			throw new IllegalArgumentException("Data is not a PNG image");
		}
	}

	protected static void verify(List<Chunk> chunks) {
		if(chunks.size() < 3) {
			throw new IllegalArgumentException("Must have at least 3 chunks");
		}

		if(!"IHDR".equals(chunks.get(0).type)) {
			throw new IllegalArgumentException("First chunk must be IHDR");
		}

		if(!"IEND".equals(chunks.get(chunks.size() - 1).type)) {
			throw new IllegalArgumentException("Last chunk must be IEND");
		}
	}
}
