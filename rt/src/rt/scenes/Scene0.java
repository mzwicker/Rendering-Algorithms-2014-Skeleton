package rt.scenes;

import rt.*;

public class Scene0 implements Scene {
	
	private String outputFilename;
	private int SPP;
	private int width;
	private int height;
	private Camera camera;
	private Film film;
	private IntegratorFactory integratorFactory;
	private SamplerFactory samplerFactory;
	private Tonemapper tonemapper;
	private Intersectable root;
	
	public Scene0()
	{
		outputFilename = new String("Scene0");
		
		width = 1024;
		height = 1024;
		SPP = 16;
		camera = new FixedCamera(width, height);
		film = new Film(width, height);
		tonemapper = new ClampTonemapper();
		
		integratorFactory = new DebugIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();
		
		root = new CSGCube();
	}
	
	public IntegratorFactory getIntegratorFactory() {
		return integratorFactory;
	}

	public SamplerFactory getSamplerFactory() {
		return samplerFactory;
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
		return root;
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
