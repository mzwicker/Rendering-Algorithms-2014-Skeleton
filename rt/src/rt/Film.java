package rt;

/**
 * A film stores a 2D grid of {@link rt.Spectrum} representing an image.
 * Rendered samples can be added one by one to a film. Samples are
 * filtered using some filter (depending on the implementation of this 
 * interface) when added.
 */
public interface Film {
	
	/**
	 * Add a sample to the film at a specified floating point position. The position
	 * coordinates are assumed to be in image space.
	 * 
	 * @param x x-coordinate in image space 
	 * @param y y-coordinate in image space
	 * @param s sample to be added
	 */
	public void addSample(double x, double y, Spectrum s);
	
	/**
	 * Computes and returns the image stored in the film.
	 * @return the image
	 */
	public Spectrum[][] getImage();
	
	/**
	 * Returns width (in pixels) of film.
	 * 
	 * @return width in pixels
	 */
	public int getWidth();
	
	/**
	 * Returns height (in pixels) of film.
	 * 
	 * @return height in pixels
	 */
	public int getHeight();

}
