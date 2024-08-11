package dev.rdh.png.image;

public interface Image {
	int getWidth();
	int getHeight();

	/**
	 * Get the pixel at the given coordinates, where (0, 0) is the top-left corner.
	 * The pixel is returned as an ARGB integer, where the alpha channel is in the
	 * most significant 8 bits, followed by red, green, and blue.
	 */
	int getPixel(int x, int y);

	/**
	 * Set the pixel at the given coordinates, where (0, 0) is the top-left corner.
	 * The pixel is given as an ARGB integer, where the alpha channel is in the
	 * most significant 8 bits, followed by red, green, and blue.
	 */
	void setPixel(int x, int y, int pixel);
}
