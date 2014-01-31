package rt;

/**
 * Uses a box filter when accumulating samples on a film. A box filter means
 * that samples contribute only to the pixel that they lie in. Sample values
 * are simply averaged.
 */
public class BoxFilterFilm implements Film {
	
	private Spectrum[][] image;
	public int width, height;
	private Spectrum[][] unnormalized;
	private float nSamples[][];
	
	public BoxFilterFilm(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new Spectrum[width][height];
		unnormalized = new Spectrum[width][height];
		nSamples = new float[width][height];
		
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				image[i][j] = new Spectrum();
				unnormalized[i][j] = new Spectrum();
				nSamples[i][j] = 0.f;
			}
		}
	}
	
	public void addSample(double x, double y, Spectrum s)
	{
		if((int)x>=0 && (int)x<width && (int)y>=0 && (int)y<height)
		{
			unnormalized[(int)x][(int)y].r += s.r;
			unnormalized[(int)x][(int)y].g += s.g;
			unnormalized[(int)x][(int)y].b += s.b;
			nSamples[(int)x][(int)y]++;
			image[(int)x][(int)y].r = unnormalized[(int)x][(int)y].r/nSamples[(int)x][(int)y];
			image[(int)x][(int)y].g = unnormalized[(int)x][(int)y].g/nSamples[(int)x][(int)y];
			image[(int)x][(int)y].b = unnormalized[(int)x][(int)y].b/nSamples[(int)x][(int)y];
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Spectrum[][] getImage()
	{
		return image;
	}
}
