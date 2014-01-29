package rt.scenes;

import rt.*;

public final class MandelbrotScene implements Scene {

	private String outputFilename;
	private int SPP;
	private int width;
	private int height;
	private Camera camera;
	private Film film;
	private IntegratorFactory integratorFactory;
	private SamplerFactory samplerFactory;
	private Tonemapper tonemapper;
	
	public MandelbrotScene()
	{
		outputFilename = new String("Mandelbrot");
		
		width = 1024;
		height = 1024;
		SPP = 16;
		camera = new DummyCamera(width, height);
		film = new Film(width, height);
		tonemapper = new ClampTonemapper();
		
		integratorFactory = new MandelbrotIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();
	}
	
	public Integrator makeIntegrator() {
		return integratorFactory.make(this);
	}

	public Sampler makeSampler() {
		return samplerFactory.make();
	}
	
	public String getOutputFilename()
	{
		return outputFilename;
	}
	
	public Camera getCamera() {
		return camera;
	}

	public Film getFilm() {
		return film;
	}

	public Intersectable getIntersectable() {
		return null;
	}

	public LightList getLightList() {
		return null;
	}

	public int getSPP() {
		return SPP;
	}
	
	public Tonemapper getTonemapper()
	{
		return tonemapper;
	}
	
	public void prepare()
	{
	}

}
