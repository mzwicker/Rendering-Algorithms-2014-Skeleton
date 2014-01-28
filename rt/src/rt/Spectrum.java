package rt;

public class Spectrum {

	public float r, g, b;
	
	public Spectrum()
	{
		r = 0.f;
		g = 0.f;
		b = 0.f;
	}
	
	public Spectrum(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Spectrum(Spectrum s)
	{
		this.r = s.r;
		this.g = s.g;
		this.b = s.b;
	}
}
