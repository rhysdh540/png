package dev.rdh.png.chunk.data;

import dev.rdh.png.chunk.data.Filter.FilterType;

public class Line {

	//unfiltered data
	private final byte[] data;

	private final Line prev;

	public Line(Line prev, byte[] data, Filter filter) {
		this.prev = prev;
		this.data = filter.unfilter(prev.data, data);
	}

	public byte[] getData(FilterType filter) {
		return filter.filter(prev.data, data.clone());
	}
}
